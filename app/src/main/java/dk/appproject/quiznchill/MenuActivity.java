package dk.appproject.quiznchill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuActivity extends AppCompatActivity implements MenuListAdaptor.OnListItemListener{

    private static final int StartCode = 117;
    private static final int CreateCode = 42;
    private static final String TAG = MenuActivity.class.getSimpleName();

    private Button startButton, createButton;
    private RecyclerView currentGamesRecyclerView;
    private MenuListAdaptor menuListAdaptor;
    private RecyclerView.LayoutManager menuListLayoutManager;

    private DatabaseService databaseService;
    private ServiceConnection databaseServiceConnection;
    private boolean bound;

    //Player and opponents and games
    private Opponents opponents;
    private Player user;
    private List<Game> games = new ArrayList<>();
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        position = 0;

        //Setup of database
        setupConnectionToDatabaseService();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverGames, new IntentFilter(Globals.Games));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverQuiz, new IntentFilter(Globals.GameFromMenu));

        Bundle extras = getIntent().getExtras();
        opponents = (Opponents) extras.getSerializable(Globals.Opponents);
        user = (Player) extras.getSerializable(Globals.User);

        startButton = findViewById(R.id.btnMenuStartQuiz);
        createButton = findViewById(R.id.btnMenuCreateQuiz);

        //Setup of recyclerview
        currentGamesRecyclerView = findViewById(R.id.listMenuCurrentGames);
        currentGamesRecyclerView.setHasFixedSize(false);
        menuListLayoutManager = new LinearLayoutManager(this);
        currentGamesRecyclerView.setLayoutManager(menuListLayoutManager);
        menuListAdaptor = new MenuListAdaptor(games, user,this);
        currentGamesRecyclerView.setAdapter(menuListAdaptor);

        //Buttons functionality
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToStartQuiz = new Intent(MenuActivity.this, StartQuizActivity.class);
                goToStartQuiz.putExtra(Globals.Opponents, opponents);
                goToStartQuiz.putExtra(Globals.User, (Serializable) user);
                startActivityForResult(goToStartQuiz, Globals.RequestCode);
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCreateQuiz = new Intent(MenuActivity.this, CreateQuizActivity.class);
                startActivityForResult(goToCreateQuiz, CreateCode);
            }
        });

        if(savedInstanceState != null)
        {
            games = (List<Game>) savedInstanceState.getSerializable(Globals.Games);
            menuListAdaptor.setPlayersGames(games);
            menuListAdaptor.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindToDataBaseService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindFromDatabaseService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindToDataBaseService();
    }

    @Override
    public void onListItemClick(int position) {
        this.position = position;
        if(games.get(position).isActive()){
            //Check if user i quiz master
            if(games.get(position).getQuizMaster() != null){
                if(games.get(position).getQuizMaster().equals(user.getName())){
                    Toast.makeText(getApplicationContext(), "Friends are still playing", Toast.LENGTH_SHORT);
                }
            }else{
                //Iteration through players to find current user
                for(Player p : games.get(position).getPlayers()){
                    if(p.getName().equals(user.getName())){
                        if(p.isFinishedQuiz()){
                            Toast.makeText(getApplicationContext(), "Waiting for opponents", Toast.LENGTH_SHORT).show();
                        }else{
                        databaseService.getQuizForGame(games.get(position).getQuizName());
                        }
                    }
                }
            }
        }else {
            Toast.makeText(getApplicationContext(), "Game is finished", Toast.LENGTH_SHORT).show();
        }
    }

    //-------------------------------------------------------------------------//
    //--------------------- Broadcast from Database service -------------------//
    //-------------------------------------------------------------------------//

    private BroadcastReceiver receiverGames = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            games.clear();
            games = databaseService.playersGames;
            menuListAdaptor.setPlayersGames(games);
            menuListAdaptor.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver receiverQuiz = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            List<Question> questions = new ArrayList<>();
            Map<String, Object> quiz = databaseService.QuizFromMenu;
            List<Question> questionsHashMaps = (List<Question>)quiz.get(Globals.Questions);


            for (int i = 0; i < questionsHashMaps.size(); i++ )
            {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                String json = gson.toJson(questionsHashMaps.get(i));
                json = json.replace("incorrectAnswers", "incorrect_answers");
                json = json.replace("correctAnswer", "correct_answer");
                json = json.replace("\\u0026#039;", "'");
                json = json.replace("\\u0026quot;", "'");
                json = json.replace("\\u0026amp;", "&");
                Question q = gson.fromJson(json, Question.class);
                questions.add(q);
            }

            Intent intentActivity = new Intent(MenuActivity.this, QuestionActivity.class);
            intentActivity.putExtra(Globals.Questions, (Serializable) questions);
            intentActivity.putExtra(Globals.User, (Serializable) user);
            intentActivity.putExtra(Globals.GameID, games.get(position).getGameId());
            startActivity(intentActivity);
        }
    };

    //--------------------- Binding til Database service -------------------//

    private void unbindFromDatabaseService() {
        if(bound){
            unbindService(databaseServiceConnection);
            bound = false;
            Log.d(TAG, "DbService unbinded");
        }
    }

    private void bindToDataBaseService() {
        bindService(new Intent(MenuActivity.this, DatabaseService.class), databaseServiceConnection, Context.BIND_AUTO_CREATE);
        bound = true;
        Log.d(TAG, "Databaseservice binded");
    }

    private void setupConnectionToDatabaseService() {
        databaseServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                databaseService = ((DatabaseService.DatabaseServiceBinder)service).getService();
                Log.d(TAG, "DbService connected");
                databaseService.getPlayersGames(user.getName());
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                databaseService = null;
                Log.d(TAG, "DbService disconnected");
            }
        };
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Globals.Games, (Serializable) games);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Globals.RequestCode)
        {
            Toast.makeText(getApplicationContext(), "You have finished the quiz!", Toast.LENGTH_SHORT);
            databaseService.getPlayersGames(user.getName());
        }
    }
}
