package com.latchkostov.android.movieapp_project1;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.latchkostov.android.movieapp_project1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by latchk on 3/10/17.
 */

public class MovieService {

    String apiKey;
    private final String movieBaseUrl;
    private final String imageBaseUrl;
    private String searchResults;
    private MovieCallBack callBack;

    public MovieService(String apiKey, String movieBaseUrl, String imageBaseUrl, MovieCallBack callBack) {
        this.apiKey = apiKey;
        this.movieBaseUrl = movieBaseUrl;
        this.imageBaseUrl = imageBaseUrl;
        this.callBack = callBack;
    }

    public Movie[] getTopMovies() {
        Uri uri = Uri.parse(movieBaseUrl).buildUpon()
                .appendPath("top_rated")
                .appendQueryParameter("api_key", apiKey).build();
        URL url = uriToUrl(uri);

        if (url != null) {
            new MovieDatabaseTask().execute(url);
        }
        ArrayList<Movie> movies = new ArrayList<Movie>();

        return null;
    }

    public Movie[] getPopularMovies() {
        Uri uri = Uri.parse(movieBaseUrl).buildUpon()
                .appendPath("popular")
                .appendQueryParameter("api_key", apiKey).build();
        URL url = uriToUrl(uri);

        if (url != null) {
            new MovieDatabaseTask().execute(url);
        }
        ArrayList<Movie> movies = new ArrayList<Movie>();

        return null;
    }

    private URL uriToUrl(Uri uri) {
        URL url = null;
        try {
            url =  new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    // Interface
    public interface MovieCallBack {
        void onComplete(Movie[] movies);
        void onError(String error);
    }

    // Task to make the HTTP call
    public class MovieDatabaseTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL url = params[0];
            String searchResults = null;
            try {
                searchResults = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                callBack.onError(e.getMessage());
                e.printStackTrace();
            }

            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null && !s.equals("")) {
                searchResults = s;
                Log.d("Test", searchResults);
                callBack.onComplete(null);
            } else {
                callBack.onError("Something went wrong, there is no data!");
            }
        }
    }

}
