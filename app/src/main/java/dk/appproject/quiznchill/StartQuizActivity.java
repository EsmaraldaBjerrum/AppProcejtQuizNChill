package dk.appproject.quiznchill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class StartQuizActivity extends AppCompatActivity implements StringViewAdapter.OnClickListener {

    private RecyclerView quizView;
    private RecyclerView opponentView;
    private RecyclerView.Adapter quizAdapter;
    private RecyclerView.Adapter opponentAdapter;
    private RecyclerView.LayoutManager quizLayout;
    private RecyclerView.LayoutManager opponentLayout;
    private List<String> quizList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);

        Bundle extras = getIntent().getExtras();
        Opponents opponents = (Opponents) extras.getSerializable("opponents");

        quizView = findViewById(R.id.rvStartQuizQuizzes);
        quizView.setHasFixedSize(true);
        quizLayout = new LinearLayoutManager(this);
        quizView.setLayoutManager(quizLayout);
        quizList.add("Quiz 1");
        quizAdapter = new StringViewAdapter(quizList, StartQuizActivity.this, true);
        quizView.setAdapter(quizAdapter);

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
}
