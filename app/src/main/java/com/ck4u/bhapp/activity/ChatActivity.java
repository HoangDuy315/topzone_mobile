package com.ck4u.bhapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ck4u.bhapp.R;

public class ChatActivity extends AppCompatActivity {

    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imgBack = findViewById(R.id.backtomain);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backmainpage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(backmainpage);
            }
        });
    }
}