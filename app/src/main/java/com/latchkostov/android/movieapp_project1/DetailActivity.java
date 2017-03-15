package com.latchkostov.android.movieapp_project1;

import android.app.Activity;
import android.content.Intent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.latchkostov.android.movieapp_project1.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailActivity extends AppCompatActivity
    implements MovieVideoAdapter.MovieVideoAdapterOnClickHandler,
    MovieReviewAdapter.MovieReviewAdapterOnClickHandler {

    private String apiKey;
    private Movie movie;
    private MovieVideo[] movieVideos;
    private MovieReview[] movieReviews;
    private TextView mMovieNameTextView;
    private ImageView mMoviePosterImageView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private Button mAddRemoveFavoriteButton;
    private TextView mMovieOverviewTextView;
    private TextView mMovieTrailersTextView;
    private TextView mMovieReviewsTextView;
    private ProgressBar mMovieReviewsProgressBar;
    private ProgressBar mMovieTrailersProgressBar;

    private MovieVideoAdapter mMovieVideoAdapter;
    private RecyclerView mMovieVideoRecyclerView;

    private MovieReviewAdapter mMovieReviewAdapter;
    private RecyclerView mMovieReviewRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // MovieVideo RecyclerView
        mMovieVideoRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie_videos);
        mMovieVideoRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager movieVideosLayoutManager = new LinearLayoutManager(this);
        mMovieVideoRecyclerView.setLayoutManager(movieVideosLayoutManager);
        mMovieVideoAdapter = new MovieVideoAdapter(this);
        mMovieVideoRecyclerView.setAdapter(mMovieVideoAdapter);

        // MovieReview RecyclerView
        mMovieReviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie_reviews);
        mMovieReviewRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager movieReviewsLayoutManager = new LinearLayoutManager(this);
        mMovieReviewRecyclerView.setLayoutManager(movieReviewsLayoutManager);
        mMovieReviewAdapter = new MovieReviewAdapter(this);
        mMovieReviewRecyclerView.setAdapter(mMovieReviewAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            this.movie = savedInstanceState.getParcelable("movie");
            this.movieReviews = (MovieReview[]) savedInstanceState.getParcelableArray("movieReviews");
            this.movieVideos = (MovieVideo[]) savedInstanceState.getParcelableArray("movieVideos");
            this.apiKey = savedInstanceState.getString("api_key");
        } else {
            Intent intent = getIntent();
            this.movie = intent.getParcelableExtra("movie");
            this.apiKey = intent.getStringExtra("api_key");
        }

        setTitle("Details");

        // Movie Name TextView
        mMovieNameTextView = (TextView) findViewById(R.id.tv_movie_name);
        mMovieNameTextView.setText(movie.getOriginalTitle());

        // Movie Poster ImageView
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        Picasso.with(this).load(movie.getPosterPath()).into(mMoviePosterImageView);

        // Release Date TextView
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movie.getReleaseDate());
        mReleaseDateTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        // Vote average TextView
        mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        mVoteAverageTextView.setText(String.valueOf(movie.getVoteAverage()) + "/10");

        // Favorite Button
        mAddRemoveFavoriteButton = (Button) findViewById(R.id.btn_addremovefavorite);
        setFavoriteButtonText();
        final FavoriteMovies favMovies = FavoriteMovies.getInstance();
        mAddRemoveFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movie.isFavorite()) {
                    favMovies.remove(movie);
                } else {
                    favMovies.add(movie);
                }
                setFavoriteButtonText();
            }
        });

        // Overview TextView
        mMovieOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        mMovieOverviewTextView.setText(movie.getOverview());

        // Trailers TextView
        mMovieTrailersProgressBar = (ProgressBar) findViewById(R.id.pb_trailers_loading_indicator);
        mMovieTrailersProgressBar.setVisibility(View.VISIBLE);
        mMovieTrailersTextView = (TextView) findViewById(R.id.tv_movie_trailers);
        new GetMovieTrailersTask().execute();

        // Reviews TextView
        mMovieReviewsProgressBar = (ProgressBar) findViewById(R.id.pb_reviews_loading_indicator);
        mMovieReviewsProgressBar.setVisibility(View.VISIBLE);
        mMovieReviewsTextView = (TextView) findViewById(R.id.tv_movie_reviews);
        new GetMovieReviewsTask().execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (movie != null) {
            outState.putParcelable("movie", movie);
            outState.putParcelableArray("movieVideos", movieVideos);
            outState.putParcelableArray("movieReviews", movieReviews);
            outState.putString("api_key", apiKey);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("movie", movie);
        setResult(Activity.RESULT_OK, intent);
        super.onBackPressed();
    }

    private void setFavoriteButtonText() {
        if (movie.isFavorite()) {
            mAddRemoveFavoriteButton.setText("Remove Favorite");
        } else {
            mAddRemoveFavoriteButton.setText("Add Favorite");
        }
    }

    private URL buildTrailerRequestURL() {
        Uri uri = Uri.parse(getString(R.string.tmdb_movieBaseURL)).buildUpon()
                .appendPath(String.valueOf(movie.getId()))
                .appendPath("videos")
                .appendQueryParameter("api_key", apiKey).build();
        URL url = null;
        try {
            url = NetworkUtils.uriToUrl(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private URL buildReviewsRequestURL() {
        Uri uri = Uri.parse(getString(R.string.tmdb_movieBaseURL)).buildUpon()
                .appendPath(String.valueOf(movie.getId()))
                .appendPath("reviews")
                .appendQueryParameter("api_key", apiKey).build();
        URL url = null;
        try {
            url = NetworkUtils.uriToUrl(uri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public void onClick(MovieVideo movieVideo) {

    }

    @Override
    public void onClick(MovieReview movieReview) {

    }

    // Task to get movie trailers
    public class GetMovieTrailersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url = buildTrailerRequestURL();
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
                response = "Error: " + e.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null && response.startsWith("Error")) {
                mMovieTrailersTextView.setText("Could not retrieve trailers: " + response);
            }
            else if (response != null && response.length() > 0) {
                try {
                    ArrayList<MovieVideo> tempMovieVideos = new ArrayList<MovieVideo>();
                    JSONObject reader = new JSONObject(response);
                    JSONArray results = reader.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        MovieVideo movieVideo = new MovieVideo();
                        JSONObject result = results.getJSONObject(i);
                        movieVideo.setId(result.getString("id"));
                        movieVideo.setIso_639_1(result.getString("iso_639_1"));
                        movieVideo.setIso_3166_1(result.getString("iso_3166_1"));
                        movieVideo.setKey(result.getString("key"));
                        movieVideo.setName(result.getString("name"));
                        movieVideo.setSite(result.getString("site"));
                        movieVideo.setSize(result.getInt("size"));
                        movieVideo.setType(result.getString("type"));
                        tempMovieVideos.add(movieVideo);
                    }
                    movieVideos = tempMovieVideos.toArray(new MovieVideo[tempMovieVideos.size()]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mMovieTrailersTextView.setText("Could not retrieve trailers: " + e.getMessage());
                }
            }

            mMovieTrailersProgressBar.setVisibility(View.INVISIBLE);
            mMovieVideoAdapter.setMovieVideos(movieVideos);
            //mMovieTrailersTextView.setText(response);
        }
    }

    // Task to get movie reviews
    public class GetMovieReviewsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url = buildReviewsRequestURL();
            String response = null;
            try {
                response = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException e) {
                e.printStackTrace();
                response = "Error:" + e.getMessage();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null && response.startsWith("Error")) {
                mMovieReviewsTextView.setText("Could not retrieve reviews: " + response);
            }
            else if (response != null && response.length() > 0) {
                try {
                    ArrayList<MovieReview> tempMovieReviews = new ArrayList<MovieReview>();
                    JSONObject reader = new JSONObject(response);
                    JSONArray results = reader.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        MovieReview movieReview = new MovieReview();
                        JSONObject result = results.getJSONObject(i);
                        movieReview.setId(result.getString("id"));
                        movieReview.setAuthor(result.getString("author"));
                        movieReview.setContent(result.getString("content"));
                        movieReview.setUrl(result.getString("url"));
                        tempMovieReviews.add(movieReview);
                    }
                    movieReviews = tempMovieReviews.toArray(new MovieReview[tempMovieReviews.size()]);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mMovieReviewsTextView.setText("Could not retrieve reviews: " + e.getMessage());
                }
            }
            mMovieReviewsProgressBar.setVisibility(View.INVISIBLE);
            mMovieReviewAdapter.setMovieReviews(movieReviews);
            //mMovieReviewsTextView.setText(response);
        }
    }
}
