package com.animalesvarados.animalesvaradosapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    public JSONArray elements;
    private Context mContext;
    private int username;

    public ReportAdapter(JSONArray elements, Context mContext) {
        this.elements = elements;
        this.mContext = mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.element_view_question_line);
            container = itemView.findViewById(R.id.element_view_container);
        }
    }

    @NonNull
    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(
                parent.getContext()).inflate(
                R.layout.qa_view, parent, false
        );
        return new ReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder, int position) {
        try{
            JSONObject element = elements.getJSONObject(position);
                //PROCESAR JSON

                holder.question.setText(element.getString("pregunta"));

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return elements.length();
    }
}
