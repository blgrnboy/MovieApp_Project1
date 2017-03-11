package com.latchkostov.android.movieapp_project1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements MovieAdapter.MovieAdapterOnClickHandler, MovieService.MovieCallBack {

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mTestTextView;
    private ProgressBar pbLoadingIndicator;
    private String apiKey;
    private String baseMovieUrl;
    private String baseMovieImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mTestTextView = (TextView) findViewById(R.id.tv_test);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        // Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        apiKey = getApiKey();
        baseMovieUrl = getResources().getString(R.string.tmdb_movieBaseURL);
        baseMovieImageUrl = getResources().getString(R.string.tmdb_imageBaseURL);
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
        MovieService movieService = new MovieService(apiKey, baseMovieUrl, baseMovieImageUrl);
        pbLoadingIndicator.setVisibility(View.VISIBLE);
        movieService.getTopMovies(this);
    }

    void loadPopularMovies() {
        MovieService movieService = new MovieService(apiKey, baseMovieUrl, baseMovieImageUrl);
        pbLoadingIndicator.setVisibility(View.VISIBLE);
        movieService.getPopularMovies(this);
    }

    @Override
    public void onClick(Movie movie) {

    }

    @Override
    public void onComplete(String jsonResult) {
        pbLoadingIndicator.setVisibility(View.INVISIBLE);

        Movie[] movies = parseMovies(jsonResult);
        mAdapter.setMovies(movies);
    }

    private Movie[] parseMovies(String json) {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        String tmdbBaseImagePath =
            getResources().getString(R.string.tmdb_imageBaseURL) +
            getResources().getString(R.string.tmdb_posterSize);

        try {
            JSONObject reader = new JSONObject(json);
            JSONArray resultArray = reader.getJSONArray("results");

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject movieObj = resultArray.getJSONObject(i);
                Movie movie = new Movie(tmdbBaseImagePath);
                movie.setOverview(movieObj.getString("overview"));
                movie.setPosterPath(movieObj.getString("poster_path"));
                movie.setTitle(movieObj.getString("title"));
                movie.setReleaseDate(movieObj.getString("release_date"));
                movie.setAdultMovie(movieObj.getBoolean("adult"));
                movie.setPopularity(movieObj.getDouble("popularity"));
                movie.setVoteCount(movieObj.getInt("vote_count"));
                movie.setVideo(movieObj.getBoolean("video"));
                movie.setVoteAverage(movieObj.getDouble("vote_average"));
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies.toArray(new Movie[movies.size()]);
    }

    @Override
    public void onError(String error) {
        // Display error somewhere
    }
}
