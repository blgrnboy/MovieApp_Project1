package com.latchkostov.android.movieapp_project1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.latchkostov.android.movieapp_project1.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements
        HttpLoader.HttpLoaderCallbacks,
        MovieAdapter.MovieAdapterOnClickHandler
{

    private int currentMenuSelection;
    private MovieAdapter mAdapter;
    private TextView mErrorTextView;
    private ProgressBar pbLoadingIndicator;
    private String apiKey;
    private String baseMovieUrl;
    private Movie[] movies;
    private static final int MOVIE_LOADER = 1;
    private static final String TMDB_URL_KEY = "tmdbUrl";
    private HttpLoader movieHttpLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (movieHttpLoader == null) {
            movieHttpLoader = new HttpLoader(TMDB_URL_KEY, this, this);
        }

        pbLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorTextView = (TextView) findViewById(R.id.tv_main_error);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movies);

        // Layout
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        apiKey = getApiKey();
        baseMovieUrl = getResources().getString(R.string.tmdb_movieBaseURL);

        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, movieHttpLoader);

        if (savedInstanceState != null && savedInstanceState.containsKey("movies")) {
            this.movies = (Movie[]) savedInstanceState.getParcelableArray("movies");
            this.currentMenuSelection = savedInstanceState.getInt("currentMenuSelection");
            mAdapter.setMovies(this.movies);
        } else {
            loadPopularMovies();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (movies != null && movies.length > 0) {
            outState.putParcelableArray("movies", movies);
            outState.putInt("currentMenuSelection", this.currentMenuSelection);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Movie intentMovie = data.getParcelableExtra("movie");
            boolean changesOccurred = false;
            for (int i = 0; i < this.movies.length; i++) {
                if (this.movies[i].getId() == intentMovie.getId()) {
                    if (this.movies[i].isFavorite() != intentMovie.isFavorite()) {
                        this.movies[i] = intentMovie;
                        changesOccurred = true;
                    }
                }
            }

            if (changesOccurred) {
                if (currentMenuSelection == R.id.menu_favorite) {
                    this.movies = FavoriteMovies.getInstance().getAll();
                }
                mAdapter.setMovies(this.movies);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        currentMenuSelection = id;
        switch (id) {
            case R.id.menu_popularMovies:
                loadPopularMovies();
                return true;
            case R.id.menu_topRatedMovies:
                loadTopRatedMovies();
                return true;
            case R.id.menu_favorite:
                FavoriteMovies favoriteMovies = FavoriteMovies.getInstance();
                movies = favoriteMovies.getAll();
                mAdapter.setMovies(movies);
                setTitle("Favorite Movies");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("movie", movie);
        intent.putExtra("api_key", apiKey);
        startActivityForResult(intent, 1);
    }

    // HTTPLoaderCallbacks
    @Override
    public void onStartLoading() {
        pbLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(String data) {
        mErrorTextView.setText("");

        Movie[] newMovies = parseMovies(data);
        Movie[] favoriteMovies = FavoriteMovies.getInstance().getAll();
        if (favoriteMovies != null && favoriteMovies.length > 0) {
            for (Movie favoriteMovie : favoriteMovies) {
                for (Movie newMovie : newMovies) {
                    if (newMovie.getId() == favoriteMovie.getId()) {
                        newMovie.setFavorite(true);
                    }
                }
            }
        }
        this.movies = newMovies;
        mAdapter.setMovies(newMovies);
    }

    @Override
    public void onError(String error) {
        mErrorTextView.setText(error);
    }

    @Override
    public void onFinished(String data) {
        pbLoadingIndicator.setVisibility(View.INVISIBLE);
    }
    // END HTTPLoaderCallbacks

    private String getApiKey() {
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

    private void loadTopRatedMovies() {
        Uri uri = Uri.parse(baseMovieUrl).buildUpon()
                .appendPath("top_rated")
                .appendQueryParameter("api_key", apiKey).build();
        URL url = null;
        try {
            url = NetworkUtils.uriToUrl(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url != null) {
            queryData(url);
        }
        setTitle("Top Movies");
    }

    private void loadPopularMovies() {
        Uri uri = Uri.parse(baseMovieUrl).buildUpon()
                .appendPath("popular")
                .appendQueryParameter("api_key", apiKey).build();
        URL url = null;
        try {
            url = NetworkUtils.uriToUrl(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (url != null) {
            queryData(url);
        }
        setTitle("Popular Movies");
    }

    private void queryData(URL url) {
        Bundle bundle = new Bundle();
        bundle.putString(TMDB_URL_KEY, url.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieLoader = loaderManager.getLoader(MOVIE_LOADER);

        if (movieHttpLoader == null) {
            movieHttpLoader = new HttpLoader(TMDB_URL_KEY, this, this);
        }

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_LOADER, bundle, movieHttpLoader);
        } else {
            loaderManager.restartLoader(MOVIE_LOADER, bundle, movieHttpLoader);
        }
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
                movie.setId(movieObj.getInt("id"));
                movie.setOverview(movieObj.getString("overview"));
                movie.setPosterPath(movieObj.getString("poster_path"));
                movie.setTitle(movieObj.getString("title"));
                movie.setReleaseDate(movieObj.getString("release_date"));
                movie.setAdultMovie(movieObj.getBoolean("adult"));
                movie.setPopularity(movieObj.getDouble("popularity"));
                movie.setVoteCount(movieObj.getInt("vote_count"));
                movie.setVideo(movieObj.getBoolean("video"));
                movie.setVoteAverage(movieObj.getDouble("vote_average"));
                movie.setOriginalTitle(movieObj.getString("original_title"));
                movies.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return movies.toArray(new Movie[movies.size()]);
    }
}
