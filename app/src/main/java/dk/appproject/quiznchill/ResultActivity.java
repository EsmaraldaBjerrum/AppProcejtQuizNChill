package dk.appproject.quiznchill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentfrom = getIntent();
        if(intentfrom.getStringExtra("result").equals("winner")) {
            setContentView(R.layout.activity_result_winner);
        }
        else if(intentfrom.getStringExtra("result").equals("looser"))
        {
            setContentView(R.layout.activity_result_looser);
        }
    }
}
