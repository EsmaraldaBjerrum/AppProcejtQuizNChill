package dk.appproject.quiznchill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StartQuizActivity extends AppCompatActivity implements StringViewAdapter.OnClickListener {

    private RecyclerView quizView;
    private RecyclerView opponentView;
    private RecyclerView.Adapter quizAdapter;
    private RecyclerView.Adapter opponentAdapter;
    private RecyclerView.LayoutManager quizLayout;
    private RecyclerView.LayoutManager opponentLayout;
    private List<String> quizList = new ArrayList<>();
    private DatabaseService db;
    private ServiceConnection serviceConnection;
    private boolean personal;
    private List<Question> chosenQuestions;
    private List<Player> chosenOpponents = new ArrayList<>();
    private List<Map<String, Object>> quizzes;
    private Opponents opponents;
    private String chosenQuiz;
    private Player user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        Bundle extras = getIntent().getExtras();
        opponents = (Opponents) extras.getSerializable(Globals.Opponents);
        user = (Player) extras.getSerializable(Globals.User);

        setupConnectionToService();
        bindService(new Intent(StartQuizActivity.this, DatabaseService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(quizReceiver, new IntentFilter(Globals.NewQuizzes));
        LocalBroadcastManager.getInstance(this).registerReceiver(idReceiver, new IntentFilter(Globals.GameID));

        quizView = findViewById(R.id.rvStartQuizQuizzes);
        quizView.setHasFixedSize(true);
        quizLayout = new LinearLayoutManager(this);
        quizView.setLayoutManager(quizLayout);
        quizAdapter = new StringViewAdapter(quizList, StartQuizActivity.this, true);
        quizView.setAdapter(quizAdapter);

        opponentView = findViewById(R.id.rvStartQuizOpponents);
        opponentView.setHasFixedSize(true);
        opponentLayout = new LinearLayoutManager(this);
        opponentView.setLayoutManager(opponentLayout);
        opponentAdapter = new StringViewAdapter(opponents.getNames(), StartQuizActivity.this, false);
        opponentView.setAdapter(opponentAdapter);


        Button btnPersonal = findViewById(R.id.btnStartQuizPersonal);
        btnPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.getPersonalQuizzes();
                personal = true;
            }
        });

        Button btnPublic = findViewById(R.id.btnStartQuizPublic);
        btnPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.getApiQuizzes();
                personal = false;
            }
        });

        Button btnOK = findViewById(R.id.btnStartQuizOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Player> players = new ArrayList<>();
                players = chosenOpponents;
                if(!personal) {
                    players.add(user);
                }
                Game game = new Game(players, chosenQuiz, (personal ? user : null), true);
                db.addGame(game);
            }
        });

    }

    private BroadcastReceiver quizReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            quizList.clear();

            quizzes = (personal ? db.PersonalQuizzes : db.APIQuizzes);

            for (Map<String, Object> quiz : quizzes) {
                quizList.add(quiz.get(Globals.QuizName).toString());
            }

            quizAdapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver idReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = db.GameId;

            for (int i=0; i < chosenQuestions.size(); i++ )
            {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(chosenQuestions.get(i));
                json = json.replace("incorrectAnswers", "incorrect_answers");
                json = json.replace("correctAnswer", "correct_answer");
                json = json.replace("\\u0026#039;", "'");
                json = json.replace("\\u0026quot;", "\"");
                json = json.replace("\\u0026amp;", "&");
                Question q = gson.fromJson(json, Question.class);
                chosenQuestions.remove(i);
                chosenQuestions.add(i, q);
            }

            Intent intentActivity = new Intent(StartQuizActivity.this, QuestionActivity.class);
            intentActivity.putExtra(Globals.Questions, (Serializable) chosenQuestions);
            intentActivity.putExtra(Globals.Opponents, (Serializable) chosenOpponents);

            //ADDED EXTRA
            intentActivity.putExtra(Globals.User, (Serializable) user);
            intentActivity.putExtra(Globals.GameID, id);
            startActivity(intentActivity);
        }
    };

    private void setupConnectionToService() {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                db = (((DatabaseService.DatabaseServiceBinder) binder).getService());
                db.getApiQuizzes();
            }
            public void onServiceDisconnected(ComponentName className) {
                db = null;
            }

        };
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(quizReceiver);
        super.onDestroy();
    }

    @Override
    public void onQuizClick(int position) {
        chosenQuiz = quizList.get(position);
        chosenQuestions = (List<Question>)quizzes.get(position).get(Globals.Questions);
        quizAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFriendClick(int position, boolean addOpponent) {
        if (addOpponent) {
            Opponents.Opponent o = opponents.data.get(position);
            Player p = new Player(o.name, o.id);
            chosenOpponents.add(p);
        }
        else
            chosenOpponents.remove(position);
    }
}
