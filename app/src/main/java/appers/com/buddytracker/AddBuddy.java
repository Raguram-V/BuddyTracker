package appers.com.buddytracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;


public class AddBuddy extends ActionBarActivity {
    AutoCompleteTextView buddyId;
    Button btnTrackBuddies;
    static ArrayList<String> buddies;
    BuddyDB dbHelper;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_buddy);

        getSupportActionBar().setLogo(R.drawable.btlogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Utility.isConnected(getApplicationContext())) {
            buddyId = (AutoCompleteTextView) this.findViewById(R.id.editBuddyId);
            String url = "http://jc305806.studentweb.jcu.edu.sg/" +
                    "BuddyTracker/buddy_validator.php";
            new BuddyValidateTask(AddBuddy.this)
                    .execute(url);

            buddyId.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    dbHelper = new BuddyDB(AddBuddy.this);
                    if(!(dbHelper.isBuddyExist(adapter.getItem(position)))) {
                        if (dbHelper.addBuddy(adapter.getItem(position))) {
                            btnTrackBuddies.setVisibility(View.VISIBLE);
                            Utility.showCustomAlert(getLayoutInflater(), getApplicationContext(),
                                    true, "New Buddy Added Successfully");
                        } else {
                            Utility.showCustomAlert(getLayoutInflater(), getApplicationContext(),
                                    false, "Unable to Add Buddy");
                        }
                    }else{
                        Utility.showCustomAlert(getLayoutInflater(), getApplicationContext(),
                                false, "Buddy Already Exists");
                    }
                }
            });

            btnTrackBuddies = (Button) this.findViewById(R.id.btnTrackBuddies);
            btnTrackBuddies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Take the user Buddy List screen
                    dbHelper = new BuddyDB(AddBuddy.this);
                    ArrayList<String> bus = dbHelper.getBuddies();
                    String str="";
                    for(String s:bus){
                        str = str + " " + s;
                    }
                    Utility.showCustomAlert(getLayoutInflater(), getApplicationContext(),
                            true, str);
                }
            });
        }
    }


    private class BuddyValidateTask extends AsyncTask<String, Void, ArrayList<String>> {

        private ProgressDialog dialog;

        public BuddyValidateTask(Context context){
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMessage("Processing..");
            dialog.show();
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            ArrayList<String> buddyList = Utility.getBuddyListFromServer(params[0]);
            Log.i("Bu_list",buddyList.size()+"");
            return buddyList;
        }
        @Override
        protected void onPostExecute(ArrayList<String> bList) {
            dialog.dismiss();
            AddBuddy.buddies = bList;
            int size = bList.size();
            String [] arrBuddies = new String[size];
            for(int i = 0;i<size;i++){
                arrBuddies[i] = bList.get(i);
                Log.i("item",arrBuddies[i]);
            }

            adapter = new ArrayAdapter<String>
                    (AddBuddy.this,android.R.layout.simple_dropdown_item_1line,arrBuddies);
            buddyId.setAdapter(adapter);
        }
    }
}
