package com.latchkostov.android.movieapp_project1;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;

import com.latchkostov.android.movieapp_project1.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by latchk on 3/16/17.
 */

public class HttpLoader implements LoaderManager.LoaderCallbacks<String> {

    public interface HttpLoaderCallbacks {
        void onStartLoading();

        void onSuccess(String data);

        void onError(String data);

        void onFinished(String data);

    }

    private final String URL_KEY;
    private final HttpLoaderCallbacks mCallbacks;
    private final Context context;

    public HttpLoader(String urlKey, HttpLoaderCallbacks mCallbacks, Context view) {
        this.URL_KEY = urlKey;
        this.mCallbacks = mCallbacks;
        this.context = view;
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(context) {
            @Override
            protected void onStartLoading() {
                if (args == null) return;
                mCallbacks.onStartLoading();
                forceLoad();
            }

            @Override
            public String loadInBackground() {
                String strURL = args.getString(URL_KEY);

                if (strURL == null || TextUtils.isEmpty(strURL)) return null;

                String searchResults;
                try {
                    URL url = new URL(strURL);
                    try {
                        searchResults = NetworkUtils.getResponseFromHttpUrl(url);
                    } catch (IOException e) {
                        searchResults = "Error - " + e.getMessage();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    searchResults = "Error - " + e.getMessage();
                }

                return searchResults;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        this.mCallbacks.onFinished(data);
        if (data != null && !data.equals("")) {
            if (data.startsWith("Error")) {
                this.mCallbacks.onError(data);
            } else {
                this.mCallbacks.onSuccess(data);
            }
        } else {
            this.mCallbacks.onError("Something went wrong, there is no data!");
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

}
