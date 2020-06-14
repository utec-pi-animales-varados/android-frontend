package com.animalesvarados.animalesvaradosapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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



public class HistorialReportesActivity extends AppCompatActivity{

    RecyclerView mRecyclerView;
    HistorialReportesAdapter mAdapter;
    private TextView empty;

    public Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historialreportes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.main_recycler_view);
        empty = findViewById(R.id.empty_view);
        empty.setVisibility(View.GONE);
    }


    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume(){
        super.onResume();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getReportes();
    }


    public void getReportes(){
        String url = Constant.DB_URL.concat("/reportes/usuario/"+Constant.userId);
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
                        if(response.length() == 0 ) empty.setVisibility(View.VISIBLE);
                        mAdapter = new HistorialReportesAdapter(response, getActivity());
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
                Log.d("JWT", Constant.jwt);
                return params;
            }
        };
        queue.add(request);
    }



    public void onDetalleReporte(View view){
        Intent intent = new Intent(this, DetalleReporteActivity.class);
        //int user_id = getIntent().getExtras().getInt("user_id");
        //intent.putExtra("user_id",user_id);
        startActivity(intent);
    }




}