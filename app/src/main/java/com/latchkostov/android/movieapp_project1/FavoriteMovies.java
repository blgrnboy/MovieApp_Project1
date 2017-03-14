package com.latchkostov.android.movieapp_project1;

import java.util.ArrayList;

/**
 * Created by Latch on 3/14/2017.
 */

public class FavoriteMovies {
    private static final FavoriteMovies ourInstance = new FavoriteMovies();
    private ArrayList<Movie> favoriteMovies = new ArrayList<Movie>();
    private ArrayList<Integer> favoriteMovieIds = new ArrayList<Integer>();

    public static FavoriteMovies getInstance() {
        return ourInstance;
    }

    private FavoriteMovies() {
    }

    public void add(Movie movie) {
        boolean exists = false;
        for (Integer id : favoriteMovieIds) {
            if (movie.getId() == id) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            favoriteMovies.add(movie);
            favoriteMovieIds.add(movie.getId());
            movie.setFavorite(true);
        }
    }

    public void remove(Movie movie) {
        int index = -1;
        for (int i = 0; i < favoriteMovieIds.size(); i++) {
            if (movie.getId() == favoriteMovieIds.get(i)) {
                index = i;
                break;
            }
        }

        if (index > -1) {
            favoriteMovies.remove(index);
            favoriteMovieIds.remove(index);
            movie.setFavorite(false);
        }
    }

    public Movie[] getAll() {
        return favoriteMovies.toArray(new Movie[favoriteMovies.size()]);
    }
}
