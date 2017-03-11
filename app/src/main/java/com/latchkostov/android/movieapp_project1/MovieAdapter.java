package com.latchkostov.android.movieapp_project1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Latch on 3/10/2017.
 */

// MovieAdapter
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private Movie[] movies;

    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movies_list_item, parent, false);

        return new MovieAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        Movie movie = movies[position];
        Picasso.with(holder.itemView.getContext()).load(movie.getPosterPath()).into(holder.mMovieImageView);
    }

    @Override
    public int getItemCount() {
        if (null == movies) return 0;
        return movies.length;
    }

    public void setMovies(Movie[] movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    // ViewHolder
    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMovieImageView;

        public MovieAdapterViewHolder(View view) {
            super(view);
            mMovieImageView = (ImageView) view.findViewById(R.id.iv_movie);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Movie movie = movies[getAdapterPosition()];
            mClickHandler.onClick(movie);
        }
    }
    // END ViewHolder

    // Interface for onClick handler
    public interface MovieAdapterOnClickHandler {
        void onClick(Movie movie);
    }
    // End Interface for onClick handler
}
// END MovieAdapter
