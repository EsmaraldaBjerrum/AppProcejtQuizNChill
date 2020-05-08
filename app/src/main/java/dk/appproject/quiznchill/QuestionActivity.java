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
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/* Shared onClickListener inspired by https://stackoverflow.com/questions/25905086/multiple-buttons-onclicklistener-android
* */

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = QuestionActivity.class.getSimpleName();

    private ServiceConnection databaseServiceConnection;
    private DatabaseService databaseService;
    private boolean bound;

    private Button option1, option2, option3;
    private TextView questionText;

    private String currentQuizId;
    private Player currentPlayer;
    private List<Question> currentQuizQuestions;
    private int questionIndex;
    private int correctAnswers;
    private int indexOfCorrectAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        //Setup connection til database
        setupConnectionToDatabaseService();

        //Get variables from bundle
        Bundle extras = getIntent().getExtras();
        currentPlayer = (Player) extras.getSerializable(Globals.User);
        currentQuizQuestions = (ArrayList<Question>) extras.getSerializable(Globals.Questions);
        currentQuizId = (String) extras.getSerializable(Globals.GameID);

        option1 = findViewById(R.id.btnQuestionAnswer1);
        option2 = findViewById(R.id.btnQuestionAnswer2);
        option3 = findViewById(R.id.btnQuestionAnswer3);
        questionText = findViewById(R.id.txtQuestionText);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);

        questionIndex = 0;
        correctAnswers = 0;
        indexOfCorrectAnswer = 0;

        displayQuestion();


        //TEST QUESTIONS
        /*List<String> wQ = new ArrayList<>();
        wQ.add("Dumb");
        wQ.add("Boring and just a real piece of shit with no regards for human life and all that jazz");
        Question q = new Question("Alice", "Hatter is what?", "Mad", wQ);
        Question q2 = new Question("Alice", "Red Queen is what?", "Evil bitch", wQ);
        Question[] qs = new Question[2];
        qs[0] = q;
        qs[1] = q2;
        currentQuizQuestions = qs;*/
    }

    private void displayQuestion(){
        //Get the current question and wrong answers
        Question currentQuestion = (Question)currentQuizQuestions.get(questionIndex);
        List<String> incorrectAnswers = currentQuestion.getIncorrectAnswers();

        //Set QuestionText
        questionText.setText(currentQuestion.getQuestion());

        //Shuffle the position of the correct answer and set text for the options
        Random rand = new Random();
        int randomNumber = rand.nextInt(2) + 1;
        switch (randomNumber){
            case 1:
                option1.setText(currentQuestion.getCorrectAnswer());
                option2.setText(incorrectAnswers.get(0));
                option3.setText(incorrectAnswers.get(1));
                indexOfCorrectAnswer = 1;
                break;
            case 2:
                option1.setText(incorrectAnswers.get(0));
                option2.setText(currentQuestion.getCorrectAnswer());
                option3.setText(incorrectAnswers.get(1));
                indexOfCorrectAnswer = 2;
                break;
            case 3:
                option1.setText(incorrectAnswers.get(1));
                option2.setText(incorrectAnswers.get(0));
                option3.setText(currentQuestion.getCorrectAnswer());
                indexOfCorrectAnswer = 3;
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnQuestionAnswer1:
                if(indexOfCorrectAnswer == 1) {
                    correctAnswers++;
                }
                break;

            case R.id.btnQuestionAnswer2:
                if(indexOfCorrectAnswer == 2) {
                    correctAnswers++;
                }
                break;

            case R.id.btnQuestionAnswer3:
                if(indexOfCorrectAnswer == 3) {
                    correctAnswers++;
                }
                break;
            default:
                break;
        }

        questionIndex++;
        if(questionIndex < currentQuizQuestions.size()){
            displayQuestion();
        }else {
            databaseService.updateGameStatus(currentQuizId, currentPlayer.getName(), correctAnswers);
            //EVT broadcast?
            Intent intent = new Intent(QuestionActivity.this, StartQuizActivity.class);
            setResult(RESULT_CANCELED, intent);
            finish();
            // TODO: 07-05-2020 Hop tilbage til menu 
        }
    }

    //----------------------------------------------------------//
    //-----------------------Lifecycle--------------------------//
    //----------------------------------------------------------//

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //----------------------------------------------------------------------//
    //--------------------- Binding til Database service -------------------//
    //----------------------------------------------------------------------//

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

                //currentQuizQuestions = databaseService.SOMETHING!(); //Her kaldes efter spørgsmålene

            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                databaseService = null;
                Log.d(TAG, "DbService disconnected");
            }
        };
    }


    //TEST CODE FOR ADDING GAME AND ADDING QUIZ

        /*List<String> wQ = new ArrayList<>();
        wQ.add("deidheu");
        wQ.add("dedede");
        Question q = new Question("String category", "String question", "String correctAnswer", wQ);
        Question q2 = new Question("String category", "String question", "String correctAnswer", wQ);
        List<Question> qs = new ArrayList<>();
        qs.add(q);
        qs.add(q2);
        databaseService.addQuizToDb(qs, "hatterMaD", true);*/


        /*Player player1 = new Player("Kurt", 888, 0, false);
        Player player2 = new Player("Lone", 908, 0, false);
        Player[] players = new Player[2];
        players[0] = player1;
        players[1] = player2;
        Game game = new Game(players, "De gode quiz", player1, true);
        databaseService.addGame(game);*/
}
