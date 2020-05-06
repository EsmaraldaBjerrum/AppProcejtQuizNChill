package dk.appproject.quiznchill;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Game {

    @SerializedName("players")
    @Expose
    private List<Player> players;
    @SerializedName("quizName")
    @Expose
    private String quizName;
    @SerializedName("quizMaster")
    @Expose
    private Player quizMaster;
    @SerializedName("isActive")
    @Expose
    private boolean isActive;

    public Game(List<Player> players, String quizName, Player quizMaster, boolean isActive) {
        this.players = players;
        this.quizName = quizName;
        this.quizMaster = quizMaster;
        this.isActive = isActive;
    }
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
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
