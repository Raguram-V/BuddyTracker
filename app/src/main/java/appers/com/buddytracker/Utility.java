package appers.com.buddytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 4/6/2015.
 */
public class Utility {

    public static boolean shareLocation = true;
    public static final String LOCATION_UPDATE_URL=
            "http://jc305806.studentweb.jcu.edu.sg/BuddyTracker/location_update.php?";

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        if (connectivityManager != null) {
            networkInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (!networkInfo.isAvailable()) {
                networkInfo = connectivityManager
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            }
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }

    public static boolean loadPreference(Context context) {
        SharedPreferences pref = getStoredPreference(context);
        if (pref != null) {
            String sPref = pref.getString("user","yts");
            if(sPref.equals("yts")) {
                return false;
            }else {
                return true;
            }
        }else
            return false;
    }

    public static SharedPreferences getStoredPreference(Context context){
        SharedPreferences pref = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        return pref;
    }

    public static String getStoredUser(Context context){
        String storedUser = null;
        SharedPreferences pref = getStoredPreference(context);
        if (loadPreference(context)) {
            storedUser = pref.getString("user","yts");
        }
        return storedUser;
    }

    public static String getCurrentLocation(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        String strLocation = getLocality(context,location);
        return strLocation;
    }

    public static String getLocality(Context context,Location location) {

        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);
        String strLocation;

        List<Address> Data = null;
        double latitude;
        double longitude;

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            try {
                Data = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            strLocation = Data.get(0).getThoroughfare();
            strLocation = strLocation.replaceAll("\\s+", "");
        } else {
            strLocation = "Not Found";
        }
        return strLocation;
    }

    public static String sendServerRequest(String url) {

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.i("response", String.valueOf(statusCode));
            if (statusCode == 200) {
                HttpEntity httpEntity = response.getEntity();
                String strResponse = EntityUtils.toString(httpEntity);
                //String strResponse = response.toString();
                Log.i("respone", strResponse);
                if (parseJson(strResponse)) {
                    Log.i("receiver","pass");
                    return "true";
                } else {
                    return "false";
                }
            } else {
                Log.i("response", "poor status");
                return "false";
            }
        } catch (Exception e) {
            Log.e("myApp", Log.getStackTraceString(e));
            return "false";
        }
    }


    public static boolean parseJson(String strJson) {
        String i;
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            i = jsonObject.getString("result");
            boolean res = i.equals("true");
            Log.i("respone", String.valueOf(res));
            if (res) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}