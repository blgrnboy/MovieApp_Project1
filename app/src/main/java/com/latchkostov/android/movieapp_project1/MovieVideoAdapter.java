package com.latchkostov.android.movieapp_project1;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by latchk on 3/14/17.
 */

// Movie video adapter
public class MovieVideoAdapter extends RecyclerView.Adapter<MovieVideoAdapter.MovieVideoAdapterViewHolder> {

    private MovieVideo[] movieVideos;
    private final MovieVideoAdapterOnClickHandler mClickHandler;

    public MovieVideoAdapter(MovieVideoAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    @Override
    public MovieVideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movievideos_list_item, parent, false);

        return new MovieVideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieVideoAdapterViewHolder holder, int position) {
        MovieVideo movieVideo = movieVideos[position];
        holder.mMovieVideoTextView.setText(movieVideo.getName());
    }

    @Override
    public int getItemCount() {
        if (null == movieVideos) return 0;
        return movieVideos.length;
    }

    public void setMovieVideos(MovieVideo[] movieVideos) {
        this.movieVideos = movieVideos;
        notifyDataSetChanged();
    }

    // ViewHolder
    public class MovieVideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView mMovieVideoTextView;

        public MovieVideoAdapterViewHolder(View view) {
            super(view);
            mMovieVideoTextView = (TextView) view.findViewById(R.id.tv_movie_video_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            MovieVideo movieVideo = movieVideos[getAdapterPosition()];
            mClickHandler.onClick(movieVideo);
        }
    }
    // END ViewHolder

    // Interface for onClick handler
    interface MovieVideoAdapterOnClickHandler {
        void onClick(MovieVideo movieVideo);
    }
    // END Interface for onClick handler

}
