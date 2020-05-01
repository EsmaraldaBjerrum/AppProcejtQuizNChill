package dk.appproject.quiznchill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Shared onClickListener inspired by https://stackoverflow.com/questions/25905086/multiple-buttons-onclicklistener-android
* */

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = QuestionActivity.class.getSimpleName();

    private ServiceConnection databaseServiceConnection;
    private DatabaseService databaseService;
    private boolean bound;

    private Button option1, option2, option3;
    private TextView question;

    private String currentQizName;
    private Question[] currentQuizQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //Setup coonection til database
        databaseService = new DatabaseService();
        setupConnectionToDatabaseService();

        option1 = findViewById(R.id.btnQuestionAnswer1);
        option2 = findViewById(R.id.btnQuestionAnswer2);
        option3 = findViewById(R.id.btnQuestionAnswer3);
        question = findViewById(R.id.txtQuestionText);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);

        //Hente Quiz med database kald
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
                }
            }
        });

        //Spørgsmål med kald fra database eller som intent
        displayQuestions();
    }

    private void displayQuestions(){

    }

    private void shuffelOptions(){

    }

    @Override
    public void onClick(View v) {

        /*List<String> wQ = new ArrayList<>();
        wQ.add("deidheu");
        wQ.add("dedede");
        Question q = new Question("String category", "String question", "String correctAnswer", wQ);
        Question q2 = new Question("String category", "String question", "String correctAnswer", wQ);
        List<Question> qs = new ArrayList<>();
        qs.add(q);
        qs.add(q2);
        databaseService.AddQuizToDb(qs, "hat");


        Player player1 = new Player("Kurt", 888, 0, false);
        Player player2 = new Player("Lone", 908, 0, false);
        Player[] players = new Player[2];
        players[0] = player1;
        players[1] = player2;
        Game game = new Game(players, "De gode quiz", player1, true);
        databaseService.addGame(game);*/
    }

    //-----------------------Lifecycles--------------------------//

    @Override
    protected void onStart() {
        super.onStart();
        bindToDataBaseService();
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupConnectionToDatabaseService();
        bindToDataBaseService();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unbindFromDatabaseService();
    }


    private void unbindFromDatabaseService() {
        if(bound){
            unbindService(databaseServiceConnection);
            bound = false;
            Log.d(TAG, "DbService unbinded");
        }
    }

    private void bindToDataBaseService() {
        bindService(new Intent(QuestionActivity.this, DatabaseService.class), databaseServiceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
        Log.d(TAG, "Databaseservice binded");
    }

    private void setupConnectionToDatabaseService() {
        databaseServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                databaseService = ((DatabaseService.DatabaseServiceBinder)service).getService();
                Log.d(TAG, "DbService connected");
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                databaseService = null;
                Log.d(TAG, "DbService disconnected");
            }
        };
    }
}
