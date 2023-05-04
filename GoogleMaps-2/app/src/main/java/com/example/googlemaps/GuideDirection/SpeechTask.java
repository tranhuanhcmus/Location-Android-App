package com.example.googlemaps.GuideDirection;

import android.os.AsyncTask;
import android.util.Log;

import com.example.googlemaps.MapsActivity;

public class SpeechTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... params) {
        String data = params[0];

        GuideByText.callSpeech(data);
        Log.e("SpeechTask", "doInBackground: "+data );
        return null;
    }
}
