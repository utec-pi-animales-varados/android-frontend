package com.animalesvarados.animalesvaradosapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HistorialReportesAdapter extends RecyclerView.Adapter<HistorialReportesAdapter.ViewHolder>{

        public JSONArray elements;
        private Context context;


        public HistorialReportesAdapter(JSONArray elements, Context context){
            this.elements = elements;
            this.context = context;
        }

        public void showMessage(String message) {
            Toast.makeText(this.context, message, Toast.LENGTH_LONG).show();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView animal, ubicacion, fecha;
            RelativeLayout container;
            Button boton;


            public ViewHolder(View itemView) {
                super(itemView);
                animal = itemView.findViewById(R.id.animal);
                ubicacion = itemView.findViewById(R.id.ubicacion);
                fecha = itemView.findViewById(R.id.fecha);
                container = itemView.findViewById(R.id.element_view_container);
                boton = itemView.findViewById(R.id.btnVerReporte);
            }
        }

        @NonNull
        @Override
        public HistorialReportesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_view_reporte,parent, false);
            return new HistorialReportesAdapter.ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull HistorialReportesAdapter.ViewHolder holder, int position) {
            try {
                JSONObject element = elements.getJSONObject(position);
                final int id_reporte = element.getInt("id");
                final String fecha = element.getString("date");
                final String ubicacion = element.getString("latitude") + ", " +element.getString("longitude");
                final String animal = element.getJSONObject("animal").getString("name");
                holder.fecha.setText(fecha);
                holder.ubicacion.setText(ubicacion);
                holder.animal.setText(animal);



                holder.boton.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(context, DetalleReporteActivity.class);
                        intent.putExtra("id_reporte",id_reporte);
                        v.getContext().startActivity(intent);

                    }
                });



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        @Override
        public int getItemCount() {
            return elements.length();
        }



}

