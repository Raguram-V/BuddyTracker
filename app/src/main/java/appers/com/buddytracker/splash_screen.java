package appers.com.buddytracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class splash_screen extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (Utility.isConnected(this)) {
            boolean res = Utility.loadPreference(getApplicationContext());
            if (res) {
                Intent intent = new Intent(this,BuddyLocator.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this,JoinUs.class);
                startActivity(intent);
            }
            finish();
        }else {
            Toast.makeText(getApplicationContext(),
                    "Please Turn on Wifi/3g", Toast.LENGTH_LONG).show();
        }
    }
}
