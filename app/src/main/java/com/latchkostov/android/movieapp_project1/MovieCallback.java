package com.latchkostov.android.movieapp_project1;

/**
 * Created by Latch on 3/13/2017.
 */

interface MovieCallback {
    void onComplete(String jsonResult);
    void onError(String error);
}
