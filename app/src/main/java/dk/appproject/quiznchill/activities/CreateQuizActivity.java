package dk.appproject.quiznchill.activities;

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
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import dk.appproject.quiznchill.services.DatabaseService;
import dk.appproject.quiznchill.Globals;
import dk.appproject.quiznchill.dtos.Question;
import dk.appproject.quiznchill.R;

public class CreateQuizActivity extends AppCompatActivity {

    private ServiceConnection databaseServiceConnection;
    private DatabaseService databaseService;
    private String TAG = "CreateQuizActivity";
    private boolean bound;
    private EditText edtQuizName, edtQuestion, edtCorrectAnswer, edtWrongAnswerOne, edtWrongAnswerTwo;
    private ArrayList<Question> questions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);


         edtQuizName = findViewById(R.id.edtTxtCreateQuizQuizName);
         edtQuestion = findViewById(R.id.edtTxtCreateQuizQuestion);
         edtCorrectAnswer = findViewById(R.id.edtTxtCreateQuizCorrectAnswer);
         edtWrongAnswerOne = findViewById(R.id.edtTxtCreateQuizWrongAnswerOne);
         edtWrongAnswerTwo = findViewById(R.id.edtTxtCreateQuizWrongAnswerTwo);
        Button addQuestionBtn = findViewById(R.id.btnCreateQuizAddQuestion);
        Button okBtn = findViewById(R.id.btnCreateQuizOk);

        if(savedInstanceState != null){
            edtQuizName.setText(savedInstanceState.getString(Globals.QuizName));
            edtQuestion.setText(savedInstanceState.getString(Globals.Question));
            edtCorrectAnswer.setText(savedInstanceState.getString(Globals.CorrectAnswer));
            edtWrongAnswerOne.setText(savedInstanceState.getString(Globals.WrongAnswerOne));
            edtWrongAnswerTwo.setText(savedInstanceState.getString(Globals.WrongAnswerTwo));
            questions = (ArrayList<Question>) savedInstanceState.get(Globals.Questions);
        }

       addQuestionBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               ArrayList<String> incorrectAnswers = new ArrayList<>();
               incorrectAnswers.add(edtWrongAnswerOne.getText().toString());
               incorrectAnswers.add(edtWrongAnswerTwo.getText().toString());
               Question question = new Question(
                       edtQuizName.getText().toString(),
                       edtQuestion.getText().toString(),
                       edtCorrectAnswer.getText().toString(),
                       incorrectAnswers
               );

               questions.add(question);
               Toast.makeText(getApplicationContext(), R.string.QuestionAdded, Toast.LENGTH_LONG).show();
               edtQuestion.getText().clear();
               edtCorrectAnswer.getText().clear();
               edtWrongAnswerOne.getText().clear();
               edtWrongAnswerTwo.getText().clear();
           }

       });

       okBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
            databaseService.addQuizToDb(questions,edtQuizName.getText().toString(), true);
               Toast.makeText(getApplicationContext(), R.string.QuizIsAdded, Toast.LENGTH_LONG).show();
            finish();
           }
       });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupConnectionToDatabaseService();
        bindToDataBaseService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindFromDatabaseService();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(Globals.QuizName, edtQuizName.getText().toString());
        savedInstanceState.putString(Globals.Question, edtQuestion.getText().toString());
        savedInstanceState.putString(Globals.CorrectAnswer, edtCorrectAnswer.getText().toString());
        savedInstanceState.putString(Globals.WrongAnswerOne, edtWrongAnswerOne.getText().toString());
        savedInstanceState.putString(Globals.WrongAnswerTwo, edtWrongAnswerTwo.getText().toString());
        savedInstanceState.putSerializable(Globals.Questions, questions);
    }

    private void unbindFromDatabaseService() {
        if(bound){
            unbindService(databaseServiceConnection);
            bound = false;
            Log.d(TAG, "Databaseservice unbinded");
        }

    }

    private void bindToDataBaseService() {
        bindService(new Intent(CreateQuizActivity.this, DatabaseService.class), databaseServiceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
        Log.d(TAG, "Databaseservice binded");
    }

    private void setupConnectionToDatabaseService() {
        databaseServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                databaseService = ((DatabaseService.DatabaseServiceBinder)service).getService();
                // When the connection is established, the current word is fetched from the database
                Log.d(TAG, "Databaseservice connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                databaseService = null;
                Log.d(TAG, "Databaseservice disconnected");
            }
        };
    }
}
