package dk.appproject.quiznchill;

public class Question {

    public String question;
    public String correctAnswer;
    public String wrongAnswer1;
    public String wrongAnswer2;

    public Question(String _q, String _c, String _w1, String _w2)
    {
        question = _q;
        correctAnswer = _c;
        wrongAnswer1 = _w1;
        wrongAnswer2 = _w2;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public void setWrongAnswer1(String wrongAnswer1) {
        this.wrongAnswer1 = wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public void setWrongAnswer2(String wrongAnswer2) {
        this.wrongAnswer2 = wrongAnswer2;
    }
}
