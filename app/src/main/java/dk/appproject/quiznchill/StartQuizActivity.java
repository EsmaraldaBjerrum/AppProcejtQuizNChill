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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        Bundle extras = getIntent().getExtras();
        Opponents opponents = (Opponents) extras.getSerializable("opponents");

        setupConnectionToService();
        bindService(new Intent(StartQuizActivity.this, DatabaseService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        quizView = findViewById(R.id.rvStartQuizQuizzes);
        quizView.setHasFixedSize(true);
        quizLayout = new LinearLayoutManager(this);
        quizView.setLayoutManager(quizLayout);


        opponentView = findViewById(R.id.rvStartQuizOpponents);
        opponentView.setHasFixedSize(true);
        opponentLayout = new LinearLayoutManager(this);
        opponentView.setLayoutManager(opponentLayout);
        opponentAdapter = new StringViewAdapter(opponents.getNames(), StartQuizActivity.this, false);
        opponentView.setAdapter(opponentAdapter);

    }

    @Override
    public void onQuizClick(int position) {

    }

    @Override
    public void onFriendClick(int position) {

    }

    private void setupConnectionToService() {
        serviceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                db = (((DatabaseService.DatabaseServiceBinder) binder).getService());

                for (Map<String, Object> quiz : db.APIQuizzes) {
                    quizList.add(quiz.get("quizName").toString());
                }

                quizAdapter = new StringViewAdapter(quizList, StartQuizActivity.this, true);
                quizView.setAdapter(quizAdapter);
            }

            public void onServiceDisconnected(ComponentName className) {
                db = null;
            }

        };
    }
}
