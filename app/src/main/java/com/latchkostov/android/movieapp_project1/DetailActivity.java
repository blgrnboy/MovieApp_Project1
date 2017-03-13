package com.latchkostov.android.movieapp_project1;

import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private Movie movie;
    private TextView mMovieNameTextView;
    private ImageView mMoviePosterImageView;
    private TextView mReleaseDateTextView;
    private TextView mVoteAverageTextView;
    private TextView mMovieOverviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState != null && savedInstanceState.containsKey("movie")) {
            this.movie = savedInstanceState.getParcelable("movie");
        } else {
            Intent intent = getIntent();
            movie = (Movie) intent.getParcelableExtra("movie");
        }

        setTitle("Details");

        mMovieNameTextView = (TextView) findViewById(R.id.tv_movie_name);
        mMovieNameTextView.setText(movie.getOriginalTitle());
        mMoviePosterImageView = (ImageView) findViewById(R.id.iv_movie_poster);
        Picasso.with(this).load(movie.getPosterPath()).into(mMoviePosterImageView);
        mReleaseDateTextView = (TextView) findViewById(R.id.tv_movie_release_date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(movie.getReleaseDate());
        mReleaseDateTextView.setText(String.valueOf(calendar.get(Calendar.YEAR)));

        mVoteAverageTextView = (TextView) findViewById(R.id.tv_vote_average);
        mVoteAverageTextView.setText(String.valueOf(movie.getVoteAverage()) + "/10");

        mMovieOverviewTextView = (TextView) findViewById(R.id.tv_movie_overview);
        mMovieOverviewTextView.setText(movie.getOverview());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (movie != null) {
            outState.putParcelable("movie", movie);
        }
        super.onSaveInstanceState(outState);
    }
}
