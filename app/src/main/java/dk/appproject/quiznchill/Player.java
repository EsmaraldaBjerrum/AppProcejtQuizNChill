package dk.appproject.quiznchill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Player implements Serializable {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("facebookId")
    @Expose
    private long facebookId;
    @SerializedName("correctAnswers")
    @Expose
    private int correctAnswers;
    @SerializedName("finishedQuiz")
    @Expose
    private boolean finishedQuiz;

    public Player(String _name, long _facebookId, int _correctAnswer, boolean _finishedQuiz){
        name = _name;
        facebookId = _facebookId;
        correctAnswers = _correctAnswer;
        finishedQuiz = _finishedQuiz;
    }

    public Player(String _name, long _facebookId){
        name = _name;
        facebookId = _facebookId;
    }
    public Player(String _name){
        name = _name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(long facebookId) {
        this.facebookId = facebookId;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public boolean isFinishedQuiz() {
        return finishedQuiz;
    }

    public void setFinishedQuiz(boolean finishedQuiz) {
        this.finishedQuiz = finishedQuiz;
    }

}
