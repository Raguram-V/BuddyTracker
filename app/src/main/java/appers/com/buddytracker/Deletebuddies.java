package appers.com.buddytracker;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;


public class Deletebuddies extends ActionBarActivity {

    ArrayList<String> buddyList = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView listView;
    BuddyDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deletebuddies);

        getSupportActionBar().setLogo(R.drawable.btlogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        buildBuddyList();

        Button btnRemove = (Button) this.findViewById(R.id.btn_delete);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int chkCount = listView.getCheckedItemCount();
                if (chkCount < 1) {
                    Utility.showCustomAlert(getLayoutInflater(),getApplicationContext(),
                            false,"Please Select Atleast One Buddy");
                }else {
                    SparseBooleanArray chkBuddies = listView.getCheckedItemPositions();
                    ArrayList<String> removeList = new ArrayList<String>();
                    for (int i = 0;i < chkBuddies.size();i++) {
                        int position = chkBuddies.keyAt(i);
                        if (chkBuddies.valueAt(i))
                            removeList.add(adapter.getItem(position));
                    }
                    if(dbHelper.deleteBuddies(removeList)) {
                        Utility.showCustomAlert(getLayoutInflater(), getApplicationContext(),
                                true, "Buddy Removal Successful");
                        buildBuddyList();
                    }
                    else
                        Utility.showCustomAlert(getLayoutInflater(),getApplicationContext(),
                                false,"Buddy Removal Failed");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Deletebuddies.this,BuddyList.class);
        startActivity(intent);
        finish();
    }

    private void buildBuddyList() {
        dbHelper = new BuddyDB(this);
        buddyList = dbHelper.getBuddies();
        String [] arrBuddies = new String[buddyList.size()];

        for(int i = 0;i<buddyList.size();i++){
            arrBuddies[i] = buddyList.get(i);
        }

        listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice,arrBuddies);
        listView.setAdapter(adapter);
    }
}
