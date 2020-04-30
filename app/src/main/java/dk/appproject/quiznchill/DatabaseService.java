package dk.appproject.quiznchill;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseService extends Service {

    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public DatabaseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
