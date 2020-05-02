package dk.appproject.quiznchill;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

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

    // ------------------------------------------------------------- //
    // ------------------------- SERVICE LIFE CYCLE ---------------- //
    // ------------------------------------------------------------- //
    @Override
    public void onCreate(){super.onCreate();}

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
       return super.onStartCommand(intent,flags,startId);
    }

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void AddQuizToDb(List<Question> questions, String quizName)
    {
        Map<String, Object> personaleQuiz = new HashMap<>();
        personaleQuiz.put("quizName", quizName);
        personaleQuiz.put("questions", questions);
        db.collection("PersonaleQuizzes").document(quizName).set(personaleQuiz);
    }


    // --------------------------------------------------------------//
    //-------------------------- CURRENT GAMES ----------------------//
    // --------------------------------------------------------------//

    public void AddGame(Game newGame){

        Map<String, Game> gameTest = new HashMap<>();
        gameTest.put("game", newGame);

        db.collection("Games")
                .add(gameTest)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
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
    }

    public Game [] getPlayersGames(String playerName){

        CollectionReference gamesRef = db.collection("Games");
        gamesRef.whereArrayContainsAny("Players", Arrays.asList(playerName));


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

    // ------------------------------------------------------------- //
    // ------------------------- BINDING --------------------------- //
    // ------------------------------------------------------------- //
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
