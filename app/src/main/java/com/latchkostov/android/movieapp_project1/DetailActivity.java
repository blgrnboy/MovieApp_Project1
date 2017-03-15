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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private Button mAddRemoveFavoriteButton;
    private TextView mMovieTrailersTextView;
    private TextView mMovieReviewsTextView;
    private ProgressBar mMovieReviewsProgressBar;
    private ProgressBar mMovieVideosProgressBar;

    private MovieVideoAdapter mMovieVideoAdapter;

    private MovieReviewAdapter mMovieReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // MovieVideo RecyclerView
        RecyclerView mMovieVideoRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie_videos);
        mMovieVideoRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager movieVideosLayoutManager = new LinearLayoutManager(this);
        mMovieVideoRecyclerView.setLayoutManager(movieVideosLayoutManager);
        mMovieVideoAdapter = new MovieVideoAdapter(this);
        mMovieVideoRecyclerView.setAdapter(mMovieVideoAdapter);

        // MovieReview RecyclerView
        RecyclerView mMovieReviewRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie_reviews);
        mMovieReviewRecyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager movieReviewsLayoutManager = new LinearLayoutManager(this);
        mMovieReviewRecyclerView.setLayoutManager(movieReviewsLayoutManager);
        mMovieReviewAdapter = new MovieReviewAdapter(this);
        mMovieReviewRecyclerView.setAdapter(mMovieReviewAdapter);

        // Movie Videos ProgressBar and TextView
        mMovieVideosProgressBar = (ProgressBar) findViewById(R.id.pb_trailers_loading_indicator);
        mMovieVideosProgressBar.setVisibility(View.VISIBLE);
        mMovieTrailersTextView = (TextView) findViewById(R.id.tv_movie_trailers);

        // Movie Reviews ProgressBar and TextView
        mMovieReviewsProgressBar = (ProgressBar) findViewById(R.id.pb_reviews_loading_indicator);
        mMovieReviewsProgressBar.setVisibility(View.VISIBLE);
        mMovieReviewsTextView = (TextView) findViewById(R.id.tv_movie_reviews);

        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            // Instance was saved
            this.movie = savedInstanceState.getParcelable("movie");
            this.apiKey = savedInstanceState.getString("api_key");
            this.movieReviews = (MovieReview[]) savedInstanceState.getParcelableArray("movieReviews");
            mMovieReviewAdapter.setMovieReviews(this.movieReviews);
            mMovieReviewsProgressBar.setVisibility(View.INVISIBLE);
            this.movieVideos = (MovieVideo[]) savedInstanceState.getParcelableArray("movieVideos");
            mMovieVideoAdapter.setMovieVideos(this.movieVideos);
            mMovieVideosProgressBar.setVisibility(View.INVISIBLE);
        } else {
            executeFreshStartTasks();
        }

        setTitle("Details");

        // Overview TextView
        TextView mMovieOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        mMovieOverviewTextView.setText(movie.getOverview());

        // Movie Name TextView
        TextView mMovieNameTextView = (TextView) findViewById(R.id.tv_movie_name);
        mMovieNameTextView.setText(movie.getOriginalTitle());

        // Movie Poster ImageView
        ImageView mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        Picasso.with(this).load(movie.getPosterPath()).into(mMoviePosterImageView);

        // Release Date TextView
        TextView mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movie.getReleaseDate());
        mReleaseDateTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        // Vote average TextView
        TextView mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        mVoteAverageTextView.setText(String.format(getString(R.string.movieRating), movie.getVoteAverage()));

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

    private void executeFreshStartTasks() {
        Intent intent = getIntent();
        this.movie = intent.getParcelableExtra("movie");
        this.apiKey = intent.getStringExtra("api_key");
        new GetMovieTrailersTask().execute();
        new GetMovieReviewsTask().execute();
    }

    private void setFavoriteButtonText() {
        if (movie.isFavorite()) {
            mAddRemoveFavoriteButton.setText(getString(R.string.removeFavoriteButtonText));
        } else {
            mAddRemoveFavoriteButton.setText(getString(R.string.addFavoriteButtonText));
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
        Uri uri = Uri.parse(getString(R.string.youtube_video_base_url)).buildUpon()
                .appendQueryParameter("v", movieVideo.getKey()).build();

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onClick(MovieReview movieReview) {
    }

    // Task to get movie trailers
    private class GetMovieTrailersTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url = buildTrailerRequestURL();
            String response;
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
                mMovieTrailersTextView.setText(String.format(getString(R.string.trailersRetrievalError), response));
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
                    mMovieTrailersTextView.setText(String.format(getString(R.string.trailersRetrievalError), e.getMessage()));
                }
            }

            mMovieVideosProgressBar.setVisibility(View.INVISIBLE);
            mMovieVideoAdapter.setMovieVideos(movieVideos);
        }
    }

    // Task to get movie reviews
    private class GetMovieReviewsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            URL url = buildReviewsRequestURL();
            String response;
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
                mMovieReviewsTextView.setText(String.format(getString(R.string.reviewsRetrievalError), response));
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
                    mMovieReviewsTextView.setText(String.format(getString(R.string.reviewsRetrievalError), e.getMessage()));
                }
            }
            mMovieReviewsProgressBar.setVisibility(View.INVISIBLE);
            mMovieReviewAdapter.setMovieReviews(movieReviews);
        }
    }
}
