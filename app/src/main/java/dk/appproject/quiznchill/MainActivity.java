package dk.appproject.quiznchill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1337;
    private static final String TAG = MainActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private FirebaseAuth firebaseAuth;

    private Opponents opponents = new Opponents();
    private ApiService service;
    private ServiceConnection serviceConnection;
    private Player userPlayer;


    TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        callbackManager = CallbackManager.Factory.create();

        LoginButton facebookLogin = findViewById(R.id.btnFacebookLogin);

        facebookLogin.setPermissions("email", "public_profile", "user_friends");

        // TODO: 04-05-2020
        // Sikre at man ikke behøver at logge ud for for at få venner med

        facebookLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });


        Button login = findViewById(R.id.btnMainOK);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                // TODO: 04-05-2020
                /* SÆT TILBAGE NÅR LOUISE IKKE SKAL TEST MERE
                if(user == null)
                {
                    Toast.makeText(MainActivity.this, "Please log in with Facebook to play a Quiz", Toast.LENGTH_SHORT).show();
                }
                else
                 */
                {
                    //userPlayer = new Player(user.getDisplayName(), userPlayer.getFacebookId());
                    userPlayer = new Player(user.getDisplayName());
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtra(Globals.Opponents, (Serializable) opponents);
                    intent.putExtra(Globals.User, (Serializable) userPlayer);
                    startActivity(intent);
                }
            }
        });
    }

    //Kald for venner
    public void sendGraphRequest(FirebaseUser user){
        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "/me/friends", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                // TODO: 04-05-2020 Enable button
                Log.d(TAG, "onCompleted: Den klarede api kald" + response.toString());

                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                opponents = gson.fromJson(response.getRawResponse(), Opponents.class);
            }
        });

        graphRequest.executeAsync();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            updateUI(user);
        }
    }

    private void handleFacebookToken(AccessToken token){
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI(user);
                            sendGraphRequest(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void updateUI(FirebaseUser user){
        // TODO: 04-05-2020
        // Sæt det korrekt op med at der vises hvilken spiller der er logget på
        //test.setText(user.getDisplayName());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
