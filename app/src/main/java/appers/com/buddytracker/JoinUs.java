package appers.com.buddytracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;


public class JoinUs extends ActionBarActivity  {

    EditText editBuddyId;
    EditText editDisplayName;
    Button btnJoinUs;
    Button btnAddBuddies;
    ToggleButton btnShareLocation;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);

        ActionBar actionbar = getSupportActionBar();
        Utility.confActionBar(actionbar);

        if (Utility.isConnected(getApplicationContext())) {
            editBuddyId = (EditText) this.findViewById(R.id.editBuddyId);
            editDisplayName = (EditText) this.findViewById(R.id.editDisplayName);
            btnShareLocation = (ToggleButton) this.findViewById(R.id.toggleButton);


            btnJoinUs = (Button) this.findViewById(R.id.btnJoin);
            btnJoinUs.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {

                                                 String url = "http://jc305806.studentweb.jcu.edu.sg/" +
                                                         "BuddyTracker/buddy_insert.php?";
                                                 String buddyId = editBuddyId.getText().toString();
                                                 String displayName = editDisplayName.getText().toString();

                                                 if (!(buddyId.isEmpty()) && !(displayName.isEmpty())) {
                                                     url = url + "Buddy_Id=" + buddyId + "&Nick_Name=" + displayName
                                                             + "&Location=";
                                                     GetLocationTask locationTask = new GetLocationTask(JoinUs.this);
                                                     locationTask.execute(url);
                                                 } else {
                                                     Utility.showCustomAlert(getLayoutInflater(),getApplicationContext(),
                                                             false,"Please Fill Both the Fields");
                                                 }
                                             }
                                         }
            );

            btnAddBuddies = (Button) this.findViewById(R.id.btnAddBuddies);
            btnAddBuddies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent to AddBuddy page
                    Intent intent = new Intent(JoinUs.this,AddBuddy.class);
                    startActivity(intent);
                    finish();
                }
            });

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
        }else {
            Utility.showCustomAlert(getLayoutInflater(),getApplicationContext(),
                    false,"Please Turn on Wifi/3g");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private class GetLocationTask extends AsyncTask<String, Void, Boolean> {

        private ProgressDialog dialog;

        public GetLocationTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Getting Location");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String strLocation;
            if (Utility.shareLocation) {
                strLocation = Utility.getCurrentLocation(JoinUs.this);
            }else {
                strLocation = "NoData";
            }
            String url = params[0] + strLocation;
            String result =  Utility.sendServerRequest(url);
            boolean flag;
            if (result.equals("true")) {
                flag = true;
            }else {
                flag = false;
            }
            return(flag);
        }

        @Override
        protected void onPostExecute(Boolean regFlag) {
            //super.onPostExecute(regFlag);
            dialog.dismiss();
            boolean result = regFlag;
            if (result) {
                Log.i("respone","insi ev");
                btnJoinUs.setVisibility(View.GONE);
                btnAddBuddies.setVisibility(View.VISIBLE);
                savePreference();
                Utility.showCustomAlert(getLayoutInflater(),getApplicationContext(),
                        true,"Welcome Buddy");
            }else {
                Utility.showCustomAlert(getLayoutInflater(),getApplicationContext(),
                        false,"Registration Failed");
            }
        }


        public void savePreference() {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("user",editBuddyId.getText().toString());
            editor.commit();
        }


    }
}
