package com.animalesvarados.animalesvaradosapp;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.net.Uri;

import android.os.Environment;
import android.os.FileUtils;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    final int PICK_IMAGE_GALLERY = 2;
    final int PICK_IMAGE_CAMERA = 1;
    ArrayList<byte[]> imgLocation = new ArrayList<>();
    Bitmap bitmap;
    byte[] b;
    ArrayList<String> images = new ArrayList<>();
    private RequestQueue rq;

    private Button button;
    public Activity getActivity(){
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        SharedPreferenceConfig sharedPreferencesConfig = new SharedPreferenceConfig(getApplicationContext());

        if(!sharedPreferencesConfig.read_login_status()){
            button = (Button) findViewById(R.id.btnLogin);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openDialog();
                }
            });
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.main_recycler_view);
        rq = Volley.newRequestQueue(this);
        SeekBar seekBar = findViewById(R.id.slider_bar);
        seekBar.setOnSeekBarChangeListener(sliderListener);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE},1);
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
            final_loc = net_loc;
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getQuestions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rq = null;
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
                            if(response.length() > 0){
                                LinearLayout slider_layout = findViewById(R.id.slider_layout);
                                slider_layout.setVisibility(View.VISIBLE);
                            }
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

    public void addImgGallery(View v)
    {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_IMAGE_GALLERY);

    }

    public void addImgCamera(View v)
    {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(android.os.Environment.getExternalStorageDirectory(),"temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(f));
        startActivityForResult(intent,PICK_IMAGE_CAMERA);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            String[] filepath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri,filepath,null,null,null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filepath[0]);
            String picPath = cursor.getString(columnIndex);
            cursor.close();
            Log.d("ABSOLUTE PATH",picPath);

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                b = baos.toByteArray();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        else if(requestCode == PICK_IMAGE_CAMERA && resultCode == RESULT_OK)
        {
            File f = new File(Environment.getExternalStorageDirectory().toString());
            for(File temp : f.listFiles())
            {
                if (temp.getName().equals("temp.jpg"))
                {
                    f = temp;
                    break;
                }
            }
            try {
                Bitmap bitmap;
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),bitmapOptions);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                b = baos.toByteArray();
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            getImgLinks();

        }

    }

    public void getImgLinks(){

            String url = Constant.DB_URL.concat("/uploadImagen");
            VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                    new Response.Listener<NetworkResponse>() {
                        @Override
                        public void onResponse(NetworkResponse response) {
                            String obj = new String(response.data);
                            Log.d("UPLOAD SUCCESS", obj.toString());
                            images.add(obj);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Authorization", "Bearer ".concat(Constant.jwt));
                    return map;
                }

                @Override
                protected Map<String, DataPart> getByteData() throws AuthFailureError {
                    Map<String, DataPart> params = new HashMap<>();

                    params.put("image", new DataPart("imgUp.png", b));

                    return params;
                }
            };

            int socketTimeout = 30000;
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            volleyMultipartRequest.setRetryPolicy(policy);
            rq.add(volleyMultipartRequest);

    }


    public void postReport(){
        String url = Constant.DB_URL.concat("/reportes");
        final JSONObject parameters = new JSONObject();

        //PARAMS========================

        //date
        String date = Clock.systemDefaultZone().instant().toString();

        //comment
        EditText view = (EditText)findViewById(R.id.txtComentario);
        String comment = view.getText().toString();

        //picture URLs

        JSONArray urls = new JSONArray();

        for(String u : images)
        {
            urls.put(u);
        }

        //respuestas
        JSONArray respuestas = new JSONArray();

        HashMap<Integer,String> answers = mAdapter.getAnswers();

        JSONObject p = new JSONObject();
        JSONObject r = new JSONObject();

        SeekBar seekBar = findViewById(R.id.slider_bar);
        try {
            p.put("id", (1));
            r.put("textoRespuesta", String.valueOf(seekBar.getProgress()));
            r.put("pregunta", p);
            respuestas.put(r);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(Map.Entry<Integer,String> entry: answers.entrySet())
        {
            if(entry.getKey() > 0){
                if (!entry.getValue().equals("")) {
                    p = new JSONObject();
                    r = new JSONObject();
                    try {
                        Log.d((("Entry " + (entry.getKey() + 1))), entry.getValue());
                        p.put("id", (entry.getKey() + 1));
                        r.put("textoRespuesta", entry.getValue());
                        r.put("pregunta", p);
                        respuestas.put(r);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        }

        Log.d("respuestas",respuestas.toString());

        //parse into json

        try {
            parameters.put("animalid",animalId);
            parameters.put("usuarioid", Constant.userId);
            parameters.put("date",date);
            parameters.put("latitude",latitude);
            if(!comment.equals("")) {
                parameters.put("comment", comment);
            }
            parameters.put("longitude",longitude);
            parameters.put("picturesURLs",urls);
            parameters.put("respuestas",respuestas);
            parameters.put("longitudAnimal",100);
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
                }
        ){
            @Override
            public Map<String,String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","Bearer ".concat(Constant.jwt));
                return params;
            }
        };
        rq.add(jsonObjectRequest);
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

    SeekBar.OnSeekBarChangeListener sliderListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            TextView slider = findViewById(R.id.slider_text);
            if(progress == 21){
                slider.setText("Varamiento masivo");
            }
            else{
                slider.setText("Número de animales: " + progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public void openDialog(){
        DialogScreen exampledialog = new DialogScreen();
        exampledialog.show(getSupportFragmentManager(), "example dialog");
    }

}
