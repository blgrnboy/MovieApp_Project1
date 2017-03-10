package com.latchkostov.android.movieapp_project1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.iv_test);

        Picasso.with(this).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        String key = getApiKey();
        Log.d("key", key);
    }

    String getApiKey() {
        String key = null;
        InputStream is = getResources().openRawResource(R.raw.tmdb_apikey);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            key = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return key;
    }
}
