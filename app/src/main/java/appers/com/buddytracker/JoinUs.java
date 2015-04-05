package appers.com.buddytracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;


public class JoinUs extends ActionBarActivity {

    EditText editBuddyId;
    EditText editDisplayName;
    Button btnJoinUs;
    Button btnAddBuddies;

    String buddyId;
    String displayName;
    String url = "http://jc305806.studentweb.jcu.edu.sg/BuddyTracker/buddy_insert.php?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_us);

        editBuddyId = (EditText) this.findViewById(R.id.editBuddyId);
        editDisplayName = (EditText) this.findViewById(R.id.editDisplayName);

        buddyId = editBuddyId.getText().toString();
        displayName = editDisplayName.getText().toString();

        btnJoinUs =  (Button) this.findViewById(R.id.btnJoin);
        btnJoinUs.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                    if(!buddyId.isEmpty() && (!displayName.isEmpty())) {
                        url = url + "Buddy_Id=" + buddyId + "&Nick_Name=" + displayName;
                        sendServerRequest(url);
                    }else {
                        Toast.makeText(getApplicationContext(),
                        "Please Fill Both the Fields",Toast.LENGTH_LONG).show();
                    }
                 }
             }
        );

        btnAddBuddies = (Button) this.findViewById(R.id.btnAddBuddies);
        btnAddBuddies.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent to AddBuddy page
                    finish();
                }
        });
    }

    public void sendServerRequest(String url){

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response;
        try{
            response = client.execute(request);
            String strResponse = response.toString();
            if (parseJson(strResponse)) {
                Toast.makeText(getApplicationContext(),
                        "Registration Successful",Toast.LENGTH_LONG).show();
                btnJoinUs.setVisibility(View.GONE);
                btnAddBuddies.setVisibility(View.VISIBLE);
                savePreference();
            }else {
                Toast.makeText(getApplicationContext(),
                        "Try Registering with another Id",Toast.LENGTH_LONG).show();
            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void savePreference() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user",buddyId);
        editor.commit();
    }

    public boolean parseJson(String strJson) {
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            int res = jsonObject.getInt("result");
            if (res == 1) {
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
