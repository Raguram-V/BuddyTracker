package appers.com.buddytracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class Settings extends ActionBarActivity {

    ToggleButton btnShareLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        btnShareLocation = (ToggleButton) findViewById(R.id.toggleButton);

        btnShareLocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Utility.shareLocation = true;
                }else {
                    Utility.shareLocation = false;
                }
            }
        });

        Button btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.this,BuddyList.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
