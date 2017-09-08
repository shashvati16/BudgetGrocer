package capstone.android.project.com.capstoneproject.util;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import timber.log.Timber;


/**
 * Created by Shashvati on 8/16/2017.
 */

public class RemoteEndPointUtil {
    private static String TAG ="RemoteEndPointUtil.class";
    private static String BASE_URL = "https://www.mygrocerydeals.com/";
    public static String query="q";
    public static String suppliedLoc="supplied_location";
    public static String latitude="latitude";
    public static String longitude="longitude";

    public static URL buildURL(String item, String zipCode, double[] latlng) {
        Uri builtUri = Uri.parse(BASE_URL.concat("deals")).buildUpon()
                .appendQueryParameter(query, item)
                .appendQueryParameter(suppliedLoc, zipCode)
                .appendQueryParameter(latitude, String.valueOf(latlng[0]))
                .appendQueryParameter(longitude, String.valueOf(latlng[1])).build();
        URL finalUrl = null;
        try {
            finalUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Timber.v(TAG, "Built URI " + finalUrl);
        return finalUrl;


    }

    public static double[] getLatLong(String jsonResult) throws JSONException {
        double[] latlong = new double[2];
        JSONObject jsonObject = new JSONObject(jsonResult);
        JSONArray resultArray = jsonObject.getJSONArray("results");
        for (int i=0;i<resultArray.length();i++) {
            JSONObject object = resultArray.getJSONObject(i);
            JSONObject geo=object.getJSONObject("geometry");
            JSONObject loc = geo.getJSONObject("location");
            latlong[0] = loc.getDouble("lat");
            latlong[1] = loc.getDouble("lng");
        }
        return latlong;
    }


    public static URL buildLatLongURL(String zipCode) {
        URL latLongURL;
        try {
            latLongURL = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode);
        } catch (MalformedURLException ignored) {
            Timber.e(TAG, "Please check your internet connection.");
            return null;
        }
        return latLongURL;
    }
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
