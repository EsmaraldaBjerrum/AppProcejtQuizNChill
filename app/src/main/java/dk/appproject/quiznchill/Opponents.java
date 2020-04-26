package dk.appproject.quiznchill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//Inspiration from https://stackoverflow.com/questions/32571199/facebook-sdk-getting-friends-using-my-app/32571406
public class Opponents implements Serializable {

    public List<Opponent> data = new ArrayList<>();


    public List<String> getNames() {

        List<String> list = new ArrayList<>();

        for (Opponent f : data) {

            list.add(f.name);
        }

        return list;
    }


    @Override
    public String toString() {
        return "FacebookFriendsInfo{" +
                "data=" + data +
                '}';
    }

    public class Opponent {
        public long id;
        public String name;


        @Override
        public String toString() {
            return "Friend{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}

