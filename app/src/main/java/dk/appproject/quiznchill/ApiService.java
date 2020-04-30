package dk.appproject.quiznchill;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiService extends Service {

    private RequestQueue queue;
    private Quiz quiz;

    public ApiService() {
    }

    public class ServiceBinder extends Binder {
        ApiService getService(){return ApiService.this;}
    }

    private final IBinder binder = new ServiceBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void getQuiz()
    {
        if(queue==null){
            queue = Volley.newRequestQueue(this);
        }
        String url = "https://opentdb.com/api.php?amount=10";


        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ConvertJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("ApiService", "Volley: " + error.toString());
            }
        });

        queue.add(request);

    }

    private void ConvertJSON(String json){

        String json2 = "{\"Questions\"" + json.substring(28);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Quiz q = gson.fromJson(json2, Quiz.class);
    }


}