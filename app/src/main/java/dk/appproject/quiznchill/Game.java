package dk.appproject.quiznchill;

public class Game {

    private Player [] players;
    private String quizName;
    private Player quizMaster;
    private boolean isActive;

    public Game(Player[] players, String quizName, Player quizMaster, boolean isActive) {
        this.players = players;
        this.quizName = quizName;
        this.quizMaster = quizMaster;
        this.isActive = isActive;
    }
    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public String getQuizName() {
        return quizName;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
    }

    public Player getQuizMaster() {
        return quizMaster;
    }

    public void setQuizMaster(Player quizMaster) {
        this.quizMaster = quizMaster;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
