package com.hackortreat.candyio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Button Enter = findViewById(R.id.button4);
        final MediaPlayer wolfsfx = MediaPlayer.create(this, R.raw.wolf_howl);
        Enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wolfsfx.start();
                openApp();
            };

        });
    }

    private void openApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}