package dk.appproject.quiznchill;

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
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private ServiceConnection databaseServiceConnection;
    private DatabaseService databaseService;
    private String TAG = "CreateQuizActivity";
    private boolean bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);
        final List<Question> questions = new ArrayList<>();

        final EditText edtQuizName = findViewById(R.id.edtTxtCreateQuizQuizName);
        final EditText edtQuestion = findViewById(R.id.edtTxtCreateQuizQuestion);
        final EditText edtCorrectAnswer = findViewById(R.id.edtTxtCreateQuizCorrectAnswer);
        final EditText edtWrongAnswerOne = findViewById(R.id.edtTxtCreateQuizWrongAnswerOne);
        final EditText edtWrongAnswerTwo = findViewById(R.id.edtTxtCreateQuizWrongAnswerTwo);
        Button addQuestionBtn = findViewById(R.id.btnCreateQuizAddQuestion);
        Button okBtn = findViewById(R.id.btnCreateQuizOk);

       addQuestionBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               List<String> incorrectAnswers = new ArrayList<>();

               incorrectAnswers.add(edtWrongAnswerOne.getText().toString());
               incorrectAnswers.add(edtWrongAnswerTwo.getText().toString());
               Question question = new Question(
                       edtQuizName.getText().toString(),
                       edtQuestion.getText().toString(),
                       edtCorrectAnswer.getText().toString(),
                       incorrectAnswers
               );


               questions.add(question);
               edtQuestion.getText().clear();
               edtCorrectAnswer.getText().clear();
               edtWrongAnswerOne.getText().clear();
               edtWrongAnswerTwo.getText().clear();
           }

       });

       okBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

            databaseService.AddQuizToDb(questions,edtQuizName.getText().toString());
               /*Player player1 = new Player("Kurt", 888, 0, false);
               Player player2 = new Player("Lone", 908, 0, false);
               Player[] players = new Player[2];
               players[0] = player1;
               players[1] = player2;
               Game game = new Game(Arrays.asList(players), "De gode quiz", player1, true);
               databaseService.AddGame(game);
               Log.d(TAG, "Questions added");
               finish();*/
           }
       });
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
                Log.d(TAG, "Word learner service connected");

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                databaseService = null;
                Log.d(TAG, "Word learner service disconnected");
            }
        };
    }
}
