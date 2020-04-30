package dk.appproject.quiznchill;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
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

    public void AddQuizToDb(List<Question> questions, String quizName)
    {
        Map<String, Object> personaleQuiz = new HashMap<>();
        personaleQuiz.put("quizName", quizName);
        personaleQuiz.put("questions", questions);
        db.collection("PersonaleQuizzes").document(quizName).set(personaleQuiz);
    }
    // ------------------------------------------------------------- //
    // ------------------------- BINDING --------------------------- //
    // ------------------------------------------------------------- //
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
