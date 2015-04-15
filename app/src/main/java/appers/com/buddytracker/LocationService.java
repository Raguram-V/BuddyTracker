package appers.com.buddytracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class LocationService extends Service implements LocationListener{

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("receiver","service running");
        //return super.onStartCommand(intent, flags, startId);
        LocationManager lManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.NETWORK_PROVIDER;
        lManager.requestLocationUpdates(provider,0,0,this);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("receiver","inside LOC");
        String strLocation = Utility.getLocality(getApplicationContext(),location);
        String strUser = Utility.getStoredUser(getApplicationContext());
        Log.i("receiver",strUser);
        String url = Utility.LOCATION_UPDATE_URL + "Buddy_Id=" + strUser + "&Location=" + strLocation;
        new UpdateLocationTask().execute(url);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}

    private class UpdateLocationTask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            Log.i("receiver",params[0]);
            Utility.sendServerRequest(params[0]);
            return null;
        }
    }

}
