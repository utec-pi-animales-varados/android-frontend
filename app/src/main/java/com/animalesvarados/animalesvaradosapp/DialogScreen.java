package com.animalesvarados.animalesvaradosapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;

public class DialogScreen extends AppCompatDialogFragment {

    public Dialog onCreateDialog (Bundle savedInstanceState){
        AlertDialog.Builder builder =  new AlertDialog.Builder(getActivity());

        builder.setTitle("¿Te gustaría registrarte?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), DrawerActivity.class);
                        startActivity(intent);
                    }
                })
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        startActivity(intent);
                    }
                });

        return builder.create();
    }
}
