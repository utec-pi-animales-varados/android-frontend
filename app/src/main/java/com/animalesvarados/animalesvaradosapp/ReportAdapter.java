package com.animalesvarados.animalesvaradosapp;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    public JSONArray elements;
    private Context mContext;
    private HashMap<Integer,String> answers = new HashMap<>();

    public ReportAdapter(JSONArray elements, Context mContext) {
        this.elements = elements;
        this.mContext = mContext;
    }

    public HashMap<Integer,String> getAnswers() {
            return answers;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        RelativeLayout container;
        EditText answer;

        public ViewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.element_view_question_line);
            answer = itemView.findViewById(R.id.element_view_answer_line);
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
    public void onBindViewHolder(@NonNull ReportAdapter.ViewHolder holder,final int position) {
        if(position == 0){
            holder.itemView.setVisibility(View.GONE);
            return;
        }
        try{
            JSONObject element = elements.getJSONObject(position);
            answers.put(position,"");
            holder.answer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();
                    answers.put(position,text);
                }
            });

                holder.question.setText(element.getString("texto"));

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return elements.length();
    }

}
