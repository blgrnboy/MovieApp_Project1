package com.latchkostov.android.movieapp_project1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Latch on 3/15/2017.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder> {

    private MovieReview[] movieReviews;
    private final MovieReviewAdapterOnClickHandler mClickHandler;

    public MovieReviewAdapter(MovieReviewAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @Override
    public MovieReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.moviereviews_list_item, parent, false);

        return new MovieReviewAdapter.MovieReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapterViewHolder holder, int position) {
        MovieReview movieReview = movieReviews[position];
        holder.mMovieReviewAuthorTextView.setText(
                String.format(App.getContext().getString(R.string.reviewAuthor), movieReview.getAuthor()));
        holder.mMovieReviewBodyTextView.setText(movieReview.getContent());
    }

    @Override
    public int getItemCount() {
        if (movieReviews == null) return 0;
        return movieReviews.length;
    }

    public void setMovieReviews(MovieReview[] movieReviews) {
        this.movieReviews = movieReviews;
        notifyDataSetChanged();
    }

    // ViewHolder
    public class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mMovieReviewAuthorTextView;
        public final TextView mMovieReviewBodyTextView;

        public MovieReviewAdapterViewHolder(View view) {
            super(view);
            mMovieReviewAuthorTextView = (TextView) view.findViewById(R.id.tv_movie_review_author);
            mMovieReviewBodyTextView = (TextView) view.findViewById(R.id.tv_movie_review_body);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MovieReview movieReview = movieReviews[getAdapterPosition()];
            mClickHandler.onClick(movieReview);
        }
    }
    // END ViewHolder

    // Interface for onClick handler
    public interface MovieReviewAdapterOnClickHandler {
        void onClick(MovieReview movieReview);
    }
    // END Interface for onClick handler

}
