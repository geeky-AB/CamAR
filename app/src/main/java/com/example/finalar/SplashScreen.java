package com.example.finalar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashScreen extends AppCompatActivity {

    ImageView bgImage;
    TextView appNameText;
    LottieAnimationView lottieAnimationView1;
    LottieAnimationView lottieAnimationView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        lottieAnimationView2 = findViewById(R.id.animateL);
        lottieAnimationView1 = findViewById(R.id.cubeDark);
        appNameText = findViewById(R.id.nameApp);

//        bgImage.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);
        appNameText.animate().translationY(-2800).setDuration(1000).setStartDelay(4000);
        lottieAnimationView1.animate().translationY(-2800).setDuration(1800).setStartDelay(4000);
        lottieAnimationView2.animate().translationY(2800).setDuration(1800).setStartDelay(4000);

        final Handler handler = new Handler();

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this, DashBoard.class));
                finish();
            }
        };
        handler.postDelayed(r,5200);
    }
}