package com.animalesvarados.animalesvaradosapp;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    RecyclerView.Adapter mAdapter;

    Location gps;
    Location net_loc;
    Location final_loc;
    double longitude;
    double latitude;

    public Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        //String username = getIntent().getExtras().get("username").toString();
        mRecyclerView = findViewById(R.id.main_recycler_view);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE},1);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        try {
            gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(gps != null)
        {
            final_loc = gps;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else if(net_loc != null)
        {
            final_loc = gps;
            latitude = final_loc.getLatitude();
            longitude = final_loc.getLongitude();
        }
        else
        {
            latitude = 0.0;
            longitude = 0.0;
        }
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //getQuestions();
    }

    public void onClickBtnSend(View v) {
        postReport();
    }

    public void getQuestions(){
        String url = Constant.DB_URL.concat("/reporte"); //TODO rest of url for question GET
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = response.getJSONArray("response");
                            mAdapter = new ReportAdapter(data, getActivity());
                            mRecyclerView.setAdapter(mAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer ".concat(Constant.jwt));
                return params;
            }
        };
        queue.add(request);
    }


    public void postReport(){
        String url = Constant.DB_URL.concat("/reportes");
        RequestQueue queue = Volley.newRequestQueue(this);
        final JSONObject parameters = new JSONObject();

        //PARAMS========================

        //date
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        //comment
        EditText view = (EditText)findViewById(R.id.txtComentario);
        String comment = view.getText().toString();

        //picture URLs TODO Hacer post con las imagenes a /uploadFiles y añadir el response a parameters
        JSONArray urls = new JSONArray();
        view = (EditText)findViewById(R.id.txtURL);
        String[] url_list = view.getText().toString().split(",");
        for(String u : url_list)
        {
            urls.put(u);
        }

        //animal id
        final JSONObject animal = new JSONObject();

        //user id (temp)
        final JSONObject usr = new JSONObject();

        //respuestas
        JSONArray respuestas = new JSONArray();

        JSONObject r1 = new JSONObject();
        JSONObject p1 = new JSONObject();

        try {
            usr.put("id","1");
            r1.put("textoRespuesta","a");
            p1.put("id",1);
            r1.put("pregunta",p1);
            animal.put("id",1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        respuestas.put(r1);


        //parse into json

        try {
            parameters.put("date",date);
            parameters.put("latitude",latitude);
            if(!comment.equals("")) {
                parameters.put("comment", comment);
            }
            parameters.put("longitude",longitude);
            parameters.put("picturesURLs",urls);
            parameters.put("animal",animal);
            parameters.put("usuario",usr);
            parameters.put("respuestas",respuestas);
        }
        catch (Exception e){
            e.printStackTrace();
        }



        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                parameters,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showMessage("Report posted!");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage("Error posting report.");
                error.printStackTrace();

            }
        }){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer ".concat(Constant.jwt));
                return params;
            }
        };
        queue.add(jsonObjectRequest);
    }
}
