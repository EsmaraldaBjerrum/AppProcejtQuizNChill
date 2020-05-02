package dk.appproject.quiznchill;

public class Player {

    private String name;
    private long facebookId;
    private int correctAnswers;
    private boolean finishedQuiz;

    public Player(String _name, long _facebookId, int _correctAnswer, boolean _finishedQuiz){
        name = _name;
        facebookId = _facebookId;
        correctAnswers = _correctAnswer;
        finishedQuiz = _finishedQuiz;
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
