package appers.com.buddytracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by User on 3/27/2015.
 */
public class BuddyTrackerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (loadPreference()) {
            //Go to Buddylocator page
        }else {
            //go to reg page
        }
    }

    public boolean loadPreference() {
        SharedPreferences pref = getApplicationContext()
                .getSharedPreferences("User", Context.MODE_PRIVATE);
        if (pref.getString("user",null)== null){
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //close db connection
    }
}
