package appers.com.buddytracker;

import android.app.Activity;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 4/6/2015.
 */
public class Utility extends Activity {

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

    public static void showCustomAlert(LayoutInflater inflater, Context context, boolean status, String msg) {

        // Call toast.xml file for toast layout
        View toastRoot = inflater.inflate(R.layout.toast, null);

        ImageView statusImg = (ImageView) toastRoot.findViewById(R.id.statusImg);
        TextView statusMsg = (TextView) toastRoot.findViewById(R.id.errorMsg);

        if (status) {
            statusImg.setImageResource(R.drawable.tick);
        } else {
            statusImg.setImageResource(R.drawable.error);
        }
        statusMsg.setText(msg);

        Toast toast = new Toast(context);
        toast.setView(toastRoot);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
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

    public static String getJsonResponse(String strUrl) {
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(strUrl);
        HttpResponse response;
        String strResponse = null;
        try {
            response = client.execute(request);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.i("response", String.valueOf(statusCode));
            if (statusCode == 200) {
                HttpEntity httpEntity = response.getEntity();
                strResponse = EntityUtils.toString(httpEntity);
                return strResponse;
            } else {
                Log.i("response", "poor status");
                return strResponse;
            }
        } catch (Exception e) {
            Log.e("myApp", Log.getStackTraceString(e));
            return strResponse;
        }
    }

    public static ArrayList<BuddyLocation> getBuddyLocationFromServer(String strUrl) {
        String strJson = getJsonResponse(strUrl);
        ArrayList<BuddyLocation> buddyLocation = new ArrayList<BuddyLocation>();
        if (strJson != null) {
            buddyLocation = getBuddyLocationFromJson(strJson);
        }
        return buddyLocation;
    }

    public static void confActionBar(ActionBar aBar){
        aBar.setLogo(R.drawable.btlogo);
        aBar.setDisplayUseLogoEnabled(true);
        aBar.setDisplayShowHomeEnabled(true);
    }

    public static ArrayList<BuddyLocation> getBuddyLocationFromJson(String strResponse) {
        Log.i("jresponse",strResponse);
        ArrayList<BuddyLocation> buddyLocation = new ArrayList<BuddyLocation>();
        try {
            JSONObject jsonObject = new JSONObject(strResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("buddies");
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonBuddies = jsonArray.getJSONObject(i);
                buddyLocation.add(new BuddyLocation
                        (jsonBuddies.getString("buddy_id"),jsonBuddies.getString("location")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (BuddyLocation s:buddyLocation)
            Log.i("BLoc",s.getbName() + " " + s.getLocation());

        return buddyLocation;
    }

    public static ArrayList<String> getBuddyListFromJson(String strResponse) {
        ArrayList<String> buddyList = new ArrayList<String>();
        try {
            JSONObject jsonObject = new JSONObject(strResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("buddies");
            for (int i = 0;i<jsonArray.length();i++){
                JSONObject jsonBuddies = jsonArray.getJSONObject(i);
                String buddyName = jsonBuddies.getString("buddy_id");
                buddyList.add(buddyName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return buddyList;
    }

    public static ArrayList<String> getBuddyListFromServer(String strUrl) {
        String strJson = getJsonResponse(strUrl);
        ArrayList<String> buddyList = new ArrayList<String>();
        if (strJson != null) {
            buddyList = getBuddyListFromJson(strJson);
        }
        return buddyList;
    }

    public static String sendServerRequest(String url) {
         String strJson = getJsonResponse(url);
         if (strJson != null) {
             Log.i("respone", strJson);
             if (parseJson(strJson)) {
                 Log.i("receiver","pass");
                 return "true";
             } else {
                 return "false";
             }
         }else
             return "false";
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