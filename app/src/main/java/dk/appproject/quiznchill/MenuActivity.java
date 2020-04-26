package dk.appproject.quiznchill;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity implements MenuListAdaptor.OnListItemListener{

    private static final int StartCode = 117;
    private static final int CreateCode = 42;

    private Button startButton, createButton;
    private RecyclerView currentGamesRecyclerView;
    private MenuListAdaptor menuListAdaptor;
    private RecyclerView.LayoutManager menuListLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
    public void onListItemClick(int index) {
        //OOOOOOH hvad skal der ske når der bliver trykket på et spiiiiil
    }
}
