package com.example.googlemaps.Direction;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class DownloadJsonDirection extends AsyncTask<String,Void,String> {

    private String link;

    private String TAG = "downloadJson";

    private DIrectionListener listener;
    public DownloadJsonDirection(DIrectionListener listener){
        this.listener = listener;
    }


    @Override
    protected String doInBackground(String[] params) {
        link = params[0];
        String data = "";

        try{
            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            int responseCode = httpURLConnection.getResponseCode();

            if(responseCode == httpURLConnection.HTTP_OK) {
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line_info = "";
                while((line_info = bufferedReader.readLine()) != null){
                    data += line_info + "\n";
                }

                //listener.onStartFindDirection();
                return data;
            }
        }catch (Exception e){
            Log.e(TAG, "doInBackground: " + e.getMessage() );
        }

        return null;
    }

    @Override
    protected void onPostExecute(String data) {
        try {
            parseJSon(data);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<>();

        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");

        for(int i = 0; i < jsonRoutes.length(); i++){
            JSONObject jsonRoute = jsonRoutes.getJSONObject(0);

            JSONObject poly = jsonRoute.getJSONObject("overview_polyline");

            JSONArray legs = jsonRoute.getJSONArray("legs");
            JSONObject leg = legs.getJSONObject(0);

            JSONObject distance = leg.getJSONObject("distance");
            JSONObject duration = leg.getJSONObject("duration");
            String end_address = leg.getString("end_address");
            JSONObject end_location = leg.getJSONObject("end_location");
            String start_address = leg.getString("start_address");
            JSONObject start_location = leg.getJSONObject("start_location");
            JSONArray steps = leg.getJSONArray("steps");

            // first step
            JSONObject firstStepObject = steps.getJSONObject(0);
            String firstStep = firstStepObject.getString("html_instructions");
            JSONObject distanceFirstStep = firstStepObject.getJSONObject("distance");
            int valueDistanceFirstStep = distanceFirstStep.getInt("value");

            // second step
            JSONObject secondStepObject = steps.getJSONObject(1);
            String maneuver = secondStepObject.getString("maneuver");

            Route route = new Route();
            route.start_address = start_address;
            route.end_address = end_address;
            route.distance = new Distance(distance.getString("text"),distance.getInt("value"));
            route.duration = new Duration(duration.getString("text"),duration.getInt("value"));
            route.start_location = new LatLng(start_location.getDouble("lat"),start_location.getDouble("lng"));
            route.end_location = new LatLng(end_location.getDouble("lat"),end_location.getDouble("lng"));
            route.firstStep = firstStep;
            route.distanceFirstStep = valueDistanceFirstStep;

            route.maneuver = maneuver;

            route.polyline = decodePolyLine(poly.getString("points"));

            routes.add(route);
            Log.e(TAG, "parseJSon: " + routes.get(0).polyline );
            Log.e(TAG, "parseJSon: " + start_address );
            Log.e(TAG, "parseJSon: " + end_address );


        }

        listener.onSuccessFindDirection(routes);

    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }


}
