package com.latchkostov.android.movieapp_project1;

import java.text.SimpleDateFormat;
import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Latch on 3/10/2017.
 */

public class Movie implements Parcelable {
    private int id;
    private String title;
    private String originalTitle;

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    private Date releaseDate;
    private String overview;
    private final String basePosterPath;
    private String posterPath;
    private boolean adultMovie;
    private double popularity;
    private int voteCount;
    private boolean video;
    private double voteAverage;

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    private boolean isFavorite;


    public Movie(String basePosterPath) {
        this.basePosterPath = basePosterPath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setReleaseDate(String releaseDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = dateFormat.parse(releaseDate);
            this.releaseDate = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Date getReleaseDate() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    private Movie(Parcel in){
        this.id = in.readInt();
        this.basePosterPath = in.readString();
        this.originalTitle = in.readString();
        this.overview = in.readString();
        this.posterPath = in.readString();
        this.releaseDate = new Date(in.readLong());
        this.title = in.readString();
        this.voteCount = in.readInt();
        this.popularity = in.readDouble();
        this.voteAverage = in.readDouble();
        this.adultMovie = in.readByte() != 0;
        this.video = in.readByte() != 0;
        this.isFavorite = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.basePosterPath);
        dest.writeString(this.originalTitle);
        dest.writeString(this.overview);
        dest.writeString(posterPath);
        dest.writeLong(releaseDate.getTime());
        dest.writeString(this.title);
        dest.writeInt(this.voteCount);
        dest.writeDouble(this.popularity);
        dest.writeDouble(this.voteAverage);
        dest.writeByte((byte) (this.adultMovie ? 1 : 0));
        dest.writeByte((byte) (this.video ? 1 : 0));
        dest.writeByte((byte) (this.isFavorite ? 1 : 0));
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
