package dk.appproject.quiznchill;

import java.util.ArrayList;

public class PlayerPerson {
    private String name;
    private ArrayList<String> activeGames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getActiveGames() {
        return activeGames;
    }

    public void setActiveGames(ArrayList<String> activeGames) {
        this.activeGames = activeGames;
    }

    public ArrayList<String> getFinishedGames() {
        return finishedGames;
    }

    public void setFinishedGames(ArrayList<String> finishedGames) {
        this.finishedGames = finishedGames;
    }

    private ArrayList<String> finishedGames;
}
