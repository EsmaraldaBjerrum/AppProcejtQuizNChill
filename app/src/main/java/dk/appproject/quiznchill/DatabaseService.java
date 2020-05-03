package dk.appproject.quiznchill;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService extends Service {

    private static final String TAG = DatabaseService.class.getSimpleName();

    private final IBinder binder = new DatabaseServiceBinder();

    public DatabaseService() {
    }

    public class DatabaseServiceBinder extends Binder{
        public DatabaseService getService(){return DatabaseService.this;}
    }

    // --------------------------------------------------------------------- //
    // ------------------------- SERVICE LIFE CYCLE ------------------------ //
    // --------------------------------------------------------------------- //
    @Override
    public void onCreate(){super.onCreate();}

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
       return super.onStartCommand(intent,flags,startId);
    }

    // ----------------------------------------------------------------------------- //
    // --------------------------------- QUIZZES ----------------------------------- //
    // ----------------------------------------------------------------------------- //


    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public List<Map<String, Object>> APIQuizzes = new ArrayList<>();
    public List<Map<String, Object>> PersonalQuizzes = new ArrayList<>();

    public void addQuizToDb(List<Question> questions, String quizName, boolean personal)
    {
        /*
                //TEST MED  QUIZ
        Quiz testQuiz = new Quiz();
        testQuiz.setQuestions(questions);
        Map<String, Quiz> quiz = new HashMap<>();
        quiz.put(Globals.QuizName, testQuiz);

        db.collection(Globals.PersonalQuizzes).document(quizName).set(quiz);
         */

        Map<String, Object> quiz = new HashMap<>();
        quiz.put(Globals.QuizName, quizName);
        quiz.put(Globals.Questions, questions);

        if (personal)
            db.collection(Globals.PersonalQuizzes).document(quizName).set(quiz);
        else
            db.collection(Globals.APIQuizzes).document(quizName).set(quiz);
    }

    //Inspiration from https://firebase.google.com/docs/firestore/quickstart#java_8
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

    public Quiz testGetQuiz(String name){

        final Quiz thisquiz = new Quiz();
        DocumentReference docRef = db.collection(Globals.PersonalQuizzes).document(name);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Quiz quiz = documentSnapshot.toObject(Quiz.class);

            }
        });
        return thisquiz;
    }

    /*public Question [] getQuizQuestions(String quizName, boolean isPersonal){
        HashMap<String, Object> result;
        if(isPersonal){
            db.collection(Globals.PersonalQuizzes)
                    .whereEqualTo("name", quizName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    result = document.getData();
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }else{
            db.collection(Globals.APIQuizzes)
                    .whereEqualTo("name", quizName)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });

        }
    }
    private Quiz convertToQuestions(HashMap<String, Object> map){


     */
    public String addGame(Game newGame){

        final String[] id = {null};
        Map<String, Game> game = new HashMap<>();
        game.put("game", newGame);

        db.collection("Games")
                .add(game)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        id[0] = documentReference.getId();
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        //Map<String, Object> game = new HashMap<>();
        //game.put("game", newGame);
        //db.collection("Games").document().set(newGame);

        return id[0];
    }

    public Game [] getPlayersGames(String playerName){

        //CollectionReference gamesRef = db.collection("Games");
        //gamesRef.whereArrayContainsAny("Players", Arrays.asList(playerName));

        db.collection("Games")
                .whereArrayContainsAny("Players", Arrays.asList())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return new Game[0];
    }

    public void updateGameStatus(String gameId, String player, int correctAnswers){

        //Gør noget med at opdatere spillet
    }


    // -------------------------------------------------------------------------- //
    // ------------------------- BINDING AND BROADCAST--------------------------- //
    // -------------------------------------------------------------------------- //

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //https://medium.com/the-sixt-india-blog/ways-to-communicate-between-activity-and-service-6a8f07275297
    //https://stackoverflow.com/questions/8802157/how-to-use-localbroadcastmanager
    private void sendBroadcast(String message)
    {
        Intent intent = new Intent(message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}




/*//Hente Quiz med database kald
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("PersonaleQuizzes").document("De gode spørgsmål");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        //Object quiz = document.getData();
                        Map<String, Object> currentQuiz = document.getData();
                        currentQizName = currentQuiz.get("name").toString();

                        Object questionMap = currentQuiz.get("questions");

//                        for(Map.Entry<String, Object> entry : questionMap.entrySet()) {
//                            String key = entry.getKey();
//                            HashMap value = entry.getValue();
//
//                            // do what you have to do here
//                            // In your case, another loop.
//                        }

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }*/
