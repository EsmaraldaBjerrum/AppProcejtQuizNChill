package dk.appproject.quiznchill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setupConnectionToDatabaseService();

        startButton = findViewById(R.id.btnMenuStartQuiz);
        createButton = findViewById(R.id.btnMenuCreateQuiz);

        //Setup of recyclerview
        currentGamesRecyclerView = findViewById(R.id.listMenuCurrentGames);
        currentGamesRecyclerView.setHasFixedSize(false);
        menuListLayoutManager = new LinearLayoutManager(this);
        currentGamesRecyclerView.setLayoutManager(menuListLayoutManager);
        menuListAdaptor = new MenuListAdaptor(this);
        currentGamesRecyclerView.setAdapter(menuListAdaptor);

        //Buttons functionality
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToStartQuiz = new Intent(MenuActivity.this, StartQuizActivity.class);
                startActivityForResult(goToStartQuiz, StartCode); //EVT forkert
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCreateQuiz = new Intent(MenuActivity.this, CreateQuizActivity.class);
                startActivityForResult(goToCreateQuiz, CreateCode);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindToDataBaseService();


    }

    private void getPlayersGames(){

        databaseService.getPlayersGames();
    }

    @Override
    public void onListItemClick(int index) {
        //OOOOOOH hvad skal der ske når der bliver trykket på et spiiiiil
    }

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
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                databaseService = null;
                Log.d(TAG, "DbService disconnected");
            }
        };
    }
}
