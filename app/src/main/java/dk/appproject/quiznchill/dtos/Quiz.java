
package dk.appproject.quiznchill.dtos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import dk.appproject.quiznchill.dtos.Question;

public class Quiz {

    public Quiz(){}

    @SerializedName("Questions")
    @Expose
    private List<Question> questions = null;

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

}
