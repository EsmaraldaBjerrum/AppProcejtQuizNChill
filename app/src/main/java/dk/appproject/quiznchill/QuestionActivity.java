package dk.appproject.quiznchill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/* Shared onClickListener inspired by https://stackoverflow.com/questions/25905086/multiple-buttons-onclicklistener-android
* */

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = QuestionActivity.class.getSimpleName();

    private Button option1, option2, option3;
    private TextView question;

    private String currentQizName;
    private Question[] currentQuizQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        option1 = findViewById(R.id.btnQuestionAnswer1);
        option2 = findViewById(R.id.btnQuestionAnswer2);
        option3 = findViewById(R.id.btnQuestionAnswer3);
        question = findViewById(R.id.txtQuestionText);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);

        //Hente Quiz med databasekald
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

    }

    /*Gøre ting:
    * Randomise hvad nummer det rigtige svar kommer til at stå på
    * Lav savedInstanceState
    *
    * */

}
