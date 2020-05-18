package com.animalesvarados.animalesvaradosapp;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    ReportAdapter mAdapter;

    Spinner spinner;
    ArrayList<String> animales;
    int animalId = 0;

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //String username = getIntent().getExtras().get("username").toString();
        mRecyclerView = findViewById(R.id.main_recycler_view);
        //mAdapter = null;
        //mRecyclerView.setAdapter(mAdapter);


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

        //SPINNER

        animales=new ArrayList<>();
        spinner=(Spinner)findViewById(R.id.animales_sp);
        loadAnimalesInSpinner();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String animal= spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                animalId = i;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }

            });
    }


    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("Setting manager","pre");
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d("Setting manager","post");
        getQuestions();
    }


    public void onClickBtnSend(View v) {
        if(animalId != 0) {
            postReport();
        }
    }

    public void getQuestions(){
        String url = Constant.DB_URL.concat("/preguntas");
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONArray parameters = new JSONArray();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                parameters,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            Log.d("Creating recycler view",response.toString());
                            mAdapter = new ReportAdapter(response, getActivity());
                            mRecyclerView.setAdapter(mAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("RecyclerView","Volley error");
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

        HashMap<Integer,String> answers = mAdapter.getAnswers();

        JSONObject r;
        JSONObject p;

        for(Map.Entry<Integer,String> entry: answers.entrySet())
        {
            if(!entry.getValue().equals("")){
                p = new JSONObject();
                r = new JSONObject();
                try{
                    Log.d((("Entry " + (entry.getKey() + 1))),entry.getValue());
                    p.put("id",(entry.getKey()+1));
                    r.put("textoRespuesta",entry.getValue());
                    r.put("pregunta",p);
                    respuestas.put(r);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

        }

        Log.d("respuestas",respuestas.toString());

        try {
            usr.put("id","1");

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //parse into json

        try {
            animal.put("id",animalId);
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

        Log.d("param",parameters.toString());

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




        private void loadAnimalesInSpinner(){
            String url = Constant.DB_URL.concat("/animales");
            RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{
                        JSONArray jsonArray= new JSONArray(response);
                        animales.add("Seleccione un animal");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            String name=jsonObject.getString("name");
                            animales.add(name);
                        }
                        spinner.setAdapter(new ArrayAdapter<String>(ReportActivity.this, android.R.layout.simple_spinner_dropdown_item, animales));
                    }catch (JSONException e){e.printStackTrace();}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }){
                @Override
                public Map<String,String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Authorization","Bearer ".concat(Constant.jwt));
                    return params;
                }};
            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        }



}
