package com.hackiu.covid_19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;

public class Bot extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);

        LayoutInflater layoutInflater = LayoutInflater.from(Bot.this);
        final View view = layoutInflater.inflate(R.layout.bot, null);

        final AlertDialog alertDialog = new AlertDialog.Builder(Bot.this)
                .setView(view).create();
        alertDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
            }
        },3300);
    }
}
