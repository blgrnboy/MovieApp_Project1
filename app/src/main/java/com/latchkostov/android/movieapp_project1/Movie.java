package com.latchkostov.android.movieapp_project1;

/**
 * Created by Latch on 3/10/2017.
 */

public class Movie {
    private String title;
    private String releaseDate;
    private String overview;
    private final String basePosterPath;
    private String posterPath;
    private boolean adultMovie;
    private double popularity;
    private int voteCount;
    private boolean video;
    private double voteAverage;


    public Movie(String basePosterPath) {
        this.basePosterPath = basePosterPath;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setOverview(String title) {
        overview = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setPosterPath(String imageName) {
        posterPath = basePosterPath + imageName;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setAdultMovie(boolean adultMovie) {
        this.adultMovie = adultMovie;
    }

    public boolean getAdultMovie() {
        return this.adultMovie;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public double getPopularity() {
        return this.popularity;
    }



    public boolean isAdultMovie() {
        return adultMovie;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getVoteCount() {
        return this.voteCount;
    }
}
