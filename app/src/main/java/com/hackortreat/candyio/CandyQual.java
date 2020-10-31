package com.hackortreat.candyio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

public class CandyQual extends AppCompatActivity {
    Button btn_close;
    Button btn_high;
    Button btn_med;
    Button btn_low;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candy_qual);
        btn_close = (Button) findViewById((R.id.button2));
        btn_high = (Button) findViewById((R.id.button));
        btn_med = (Button) findViewById((R.id.button3));
        btn_low = (Button) findViewById((R.id.button4));

        btn_close.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        }));
        btn_high.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        }));
        btn_low.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        }));
        btn_med.setOnClickListener((new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                finish();
            }
        }));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.7));


    }
}