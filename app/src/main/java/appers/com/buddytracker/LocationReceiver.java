package appers.com.buddytracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

public class LocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("receiver", "test");
        if(intent.getExtras()!= null){
            Log.i("receiver","insi extras");
            NetworkInfo ni = (NetworkInfo) intent.getExtras().
                    get(ConnectivityManager.EXTRA_NETWORK_INFO);
            Intent locationIntent = new Intent(context,LocationService.class);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED && Utility.shareLocation == true){
                Log.i("receiver","starting service");
                context.startService(locationIntent);
            }else {
                Log.i("receiver","end");
                context.stopService(locationIntent);
            }
        }
    }
}
