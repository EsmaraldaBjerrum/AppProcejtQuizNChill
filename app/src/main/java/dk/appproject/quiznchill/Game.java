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
    @SerializedName("active")
    @Expose
    private boolean active;

    public Game(List<Player> players, String quizName, Player quizMaster, boolean active) {
        this.players = players;
        this.quizName = quizName;
        this.quizMaster = quizMaster;
        this.active = active;
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
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
