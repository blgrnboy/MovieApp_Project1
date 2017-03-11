package com.latchkostov.android.movieapp_project1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler, MovieService.MovieCallBack {

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String apiKey;
    private String baseMovieUrl;
    private String baseMovieImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        // Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        apiKey = getApiKey();
        baseMovieUrl = getResources().getString(R.string.tmdb_movieBaseURL);
        baseMovieImageUrl = getResources().getString(R.string.tmdb_imageBaseURL);
        //Log.d("key", key);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_popularMovies:
                loadPopularMovies();
                return true;
            case R.id.menu_topRatedMovies:
                loadTopRatedMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    void loadTopRatedMovies() {
        MovieService movieService = new MovieService(apiKey, baseMovieUrl, baseMovieImageUrl, this);
        movieService.getTopMovies();
    }

    void loadPopularMovies() {
        MovieService movieService = new MovieService(apiKey, baseMovieUrl, baseMovieImageUrl, this);
        movieService.getPopularMovies();
    }

    @Override
    public void onClick(Movie movie) {

    }

    @Override
    public void onComplete(Movie[] movies) {
        Log.d("DONE", "DONE!");
    }

    @Override
    public void onError(String error) {
        // Display error somewhere
    }
}
