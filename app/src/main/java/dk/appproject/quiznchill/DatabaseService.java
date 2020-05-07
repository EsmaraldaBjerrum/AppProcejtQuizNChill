package dk.appproject.quiznchill;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService extends Service {

    private static final String TAG = DatabaseService.class.getSimpleName();
    private static final int NOTIFY_ID = 142;
    private static final String ChannelId = "42";
    Notification notification = new Notification();

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final IBinder binder = new DatabaseServiceBinder();

    public List<Map<String, Object>> APIQuizzes = new ArrayList<>();
    public List<Map<String, Object>> PersonalQuizzes = new ArrayList<>();
    public String GameId = null;

    public DatabaseService() {

    }

    public class DatabaseServiceBinder extends Binder{
        public DatabaseService getService(){return DatabaseService.this;}
    }

    // --------------------------------------------------------------------- //
    // ------------------------- SERVICE LIFE CYCLE ------------------------ //
    // --------------------------------------------------------------------- //

    @Override
    public void onCreate(){
        super.onCreate();


       // sendOutStartGameNotification("De gode quiz");
       }

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
       return super.onStartCommand(intent,flags,startId);
    }

    // ----------------------------------------------------------------------------- //
    // --------------------------------- QUIZZES ----------------------------------- //
    // ----------------------------------------------------------------------------- //

    public void addQuizToDb(List<Question> questions, String quizName, boolean personal)
    {
        Map<String, Object> quiz = new HashMap<>();
        quiz.put(Globals.QuizName, quizName);
        quiz.put(Globals.Questions, questions);

        if (personal)
            db.collection(Globals.PersonalQuizzes).document(quizName).set(quiz);
        else
            db.collection(Globals.APIQuizzes).document(quizName).set(quiz);
    }

    public void getApiQuizzes()
    {
        APIQuizzes.clear();
        db.collection(Globals.APIQuizzes).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                       APIQuizzes.add(document.getData());
                    }
                    sendBroadcast(Globals.NewQuizzes);
                }
            }
        });
    }

    public void getPersonalQuizzes()
    {
        PersonalQuizzes.clear();
        db.collection(Globals.PersonalQuizzes).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PersonalQuizzes.add(document.getData());
                    }
                    sendBroadcast(Globals.NewQuizzes);
                }
            }
        });
    }

    public Map<String, Object> QuizFromMenu = new HashMap<>();
    public void getQuizForGame(String quizName){
        QuizFromMenu.clear();
        //Sørg for at den sender en quiz ud til MEnu
        db.collection(Globals.APIQuizzes).whereEqualTo(Globals.QuizName, quizName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                QuizFromMenu = document.getData();
                            }
                            sendBroadcast(Globals.GameFromMenu);
                        }
                    }
                });
        if(QuizFromMenu.isEmpty()){
            db.collection(Globals.PersonalQuizzes).document(quizName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            QuizFromMenu = document.getData();
                        }
                    }
                }
            });
        }

    }

    // --------------------------------------------------------------//
    //-------------------------- CURRENT GAMES ----------------------//
    // --------------------------------------------------------------//

    public void addGame(Game newGame){

        List<String> playerNames = getListOfPlayerNames(newGame);
        Map<String, Object> game = new HashMap<>();
        game.put("game", newGame);
        game.put("playerNames", playerNames);

        db.collection(Globals.Games)
                .add(game)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        GameId = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        sendBroadcast(Globals.GameID);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private List<String> getListOfPlayerNames(Game game){
        List<String> names = new ArrayList<>();
        for (Player p: game.getPlayers()){
            names.add(p.getName());
        }
        if(game.getQuizMaster() !=null) {
            names.add(game.getQuizMaster().toString());
        }
        return names;
    }

    public List<Game> playersGames = new ArrayList<>();
    public void getPlayersGames(String playerName){

        db.collection("Games")
                .whereArrayContainsAny("playerNames", Arrays.asList(playerName))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //Converting document to Game
                                Object gameObject = document.getData().get(Globals.Game);
                                GsonBuilder gsonbuilder = new GsonBuilder();
                                Gson gson = gsonbuilder.create();
                                String json = gson.toJson(gameObject);
                                Game game = gson.fromJson(json, Game.class);

                                game.setGameId(document.getId());

                                //Adding game to list and broadcasting changes to menu
                                playersGames.add(game);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            sendBroadcast(Globals.Games);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void updateGameStatus(final String gameId, final String playerName, final int correctAnswers){
        db.collection(Globals.Games).document(gameId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                   DocumentSnapshot document = task.getResult();

                   if(document.exists()){
                       Object players = ((Map<String, Object>) document.getData().get(Globals.Game)).get(Globals.Players);

                      ArrayList<Player> playerArrayList = convertFirestorePlayersToArrayList(players);

                       for(Player player : playerArrayList){
                           if(player.getName().equals(playerName)){
                               player.setCorrectAnswers(correctAnswers);
                               player.setFinishedQuiz(true);
                           }
                       }
                       setPLayerStatus(gameId,playerArrayList);
                   }
                   else {
                       Log.d(TAG, "No such document");
                   }
                }
                else{
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
    private void setPLayerStatus(String gameId, ArrayList<Player> players){
        db.collection(Globals.Games).document(gameId).update(Globals.GamePlayers,players).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot succesfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private ArrayList<Player> convertFirestorePlayersToArrayList(Object players){
        Gson gson = new Gson();
        String json = gson.toJson(players);
        Type type = new TypeToken<ArrayList<Player>>(){}.getType();
        return gson.fromJson(json,type);
    }

    // -------------------------------------------------------------------------- //
    // ------------------------- BINDING AND BROADCAST--------------------------- //
    // -------------------------------------------------------------------------- //

    @Override
    public IBinder onBind(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    ChannelId,
                    "Notification Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        return binder;
    }

    //https://medium.com/the-sixt-india-blog/ways-to-communicate-between-activity-and-service-6a8f07275297
    //https://stackoverflow.com/questions/8802157/how-to-use-localbroadcastmanager
    private void sendBroadcast(String message)
    {
        Intent intent = new Intent(message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    // ------------------------------------------------------------- //
    // ---------------------- NOTIFICATIONS ------------------------ //
    // ------------------------------------------------------------- //

    public void sendOutStartGameNotification(String gameId) {
        db.collection(Globals.Games).document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if((boolean)((Map<String,Object>)documentSnapshot.getData().get(Globals.Game)).get(Globals.Active))
                {
                    notification = new NotificationCompat.Builder(DatabaseService.this, ChannelId)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.YouGotAGame))
                            .build();
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(DatabaseService.this);
                    notificationManagerCompat.notify(1337,notification);
                    startForeground(NOTIFY_ID,notification);
                }
            }
        });
        sendOutFinishNotificationIfTheGameIsFinished(gameId);
    }

    public void sendOutFinishNotificationIfTheGameIsFinished(final String gameId){
        db.collection(Globals.Games).document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Object players = ((Map<String, Object>) documentSnapshot.getData().get(Globals.Game)).get(Globals.Players);

                ArrayList<Player> playerArrayList = convertFirestorePlayersToArrayList(players);

                boolean quizPlayersFinish = false;
                for (Player player : playerArrayList){
                    quizPlayersFinish = player.isFinishedQuiz();
                }

                if(quizPlayersFinish){
                    setGameAsFinish(gameId);
                    notification = new NotificationCompat.Builder(DatabaseService.this, ChannelId)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(getString(R.string.app_name))
                            .setContentText(getString(R.string.GameIsFinish))
                            .build();
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(DatabaseService.this);
                    notificationManagerCompat.notify(1337,notification);
                    startForeground(NOTIFY_ID,notification);
                }
            }
        });
    }

    private void setGameAsFinish(String gameId){
        db.collection(Globals.Games).document(gameId).update(Globals.GameActive,false).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot succesfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
}