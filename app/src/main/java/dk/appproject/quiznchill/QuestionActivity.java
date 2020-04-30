package dk.appproject.quiznchill;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class QuestionActivity extends AppCompatActivity {

    private Button option1, option2, option3;
    private TextView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        option1 = findViewById(R.id.btnQuestionAnswer1);
        option2 = findViewById(R.id.btnQuestionAnswer2);
        option3 = findViewById(R.id.btnQuestionAnswer3);
        question = findViewById(R.id.txtQuestionText);
    }
}
