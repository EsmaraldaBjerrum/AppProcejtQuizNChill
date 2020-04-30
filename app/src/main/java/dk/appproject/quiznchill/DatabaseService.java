package dk.appproject.quiznchill;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseService extends Service {
    private final IBinder binder = new DatabaseServiceBinder();


    public DatabaseService() {
    }

    public class DatabaseServiceBinder extends Binder{
        public DatabaseService getService(){return DatabaseService.this;}
    }

    // ------------------------------------------------------------- //
    // ------------------------- SERVICE LIFE CYCLE ---------------- //
    // ------------------------------------------------------------- //
    @Override
    public void onCreate(){super.onCreate();}

    @Override
    public int onStartCommand(Intent intent, int flags,int startId){
       return super.onStartCommand(intent,flags,startId);
    }

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public List<Map<String, Object>> APIQuizzes = new ArrayList<>();
    public List<Map<String, Object>> PersonalQuizzes = new ArrayList<>();

    public void addQuizToDb(List<Question> questions, String quizName, boolean personal)
    {
        Map<String, Object> quiz = new HashMap<>();
        quiz.put("quizName", quizName);
        quiz.put("questions", questions);

        if (personal)
            db.collection("PersonalQuizzes").document(quizName).set(quiz);
        else
            db.collection("APIQuizzes").document(quizName).set(quiz);
    }

    //Inspiration from https://firebase.google.com/docs/firestore/quickstart#java_8
    public void getPersonalQuizzes()
    {
        PersonalQuizzes.clear();
        db.collection("PersonalQuizzes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        PersonalQuizzes.add(document.getData());
                    }
                    sendBroadcast("new-quizzes");
                }
            }
        });
    }


    public void getApiQuizzes()
    {
        APIQuizzes.clear();
        db.collection("APIQuizzes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                         APIQuizzes.add(document.getData());
                    }
                    sendBroadcast("new-quizzes");
                }
            }
        });
    }
    // ------------------------------------------------------------- //
    // ------------------------- BINDING --------------------------- //
    // ------------------------------------------------------------- //
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    //https://medium.com/the-sixt-india-blog/ways-to-communicate-between-activity-and-service-6a8f07275297
    //https://stackoverflow.com/questions/8802157/how-to-use-localbroadcastmanager
    private void sendBroadcast(String message)
    {
        Intent intent = new Intent(message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
