package appers.com.buddytracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;


public class BuddyList extends ActionBarActivity {

    ListView buddyList;
    BuddyDB dbHelper;
    ArrayList<String> bList = new ArrayList<String>();
    String url = "http://jc305806.studentweb.jcu.edu.sg/BuddyTracker/buddy_list.php?Buddies=";
    String bNames="";
    ArrayList<BuddyLocation> bInfo = new ArrayList<BuddyLocation>();
    ArrayList<BuddyLocation> searchResult = new ArrayList<BuddyLocation>();
    EditText sText;
    ArrayAdapter<BuddyLocation> buddyListAdapter;

    TextView noMatch;
    TextView textError;
    ImageView imgSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddy_locator);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBuddyList();
    }

    private void getBuddyList() {

        ActionBar actionbar = getSupportActionBar();
        Utility.confActionBar(actionbar);

        sText = (EditText) findViewById(R.id.searchText);
        noMatch = (TextView)findViewById(R.id.textNoMatch);
        textError = (TextView) this.findViewById(R.id.textError);
        imgSearch = (ImageView) this.findViewById(R.id.imageView);
        imgSearch.setImageResource(R.mipmap.search);
        buddyList = (ListView) findViewById(R.id.buddyList);

        dbHelper = new BuddyDB(this);
        bList = dbHelper.getBuddies();
        if (bList.size() > 0) {
            for (String strName : bList) {
                bNames = bNames + strName + "-";
            }
            bNames = bNames.substring(0, bNames.length() - 1);
            url = url + bNames;
            Log.i("nnn", url);
            new GetBuddyListTask(this).execute(url);
            searchBuddies();
        }else {
            buddyList.setVisibility(View.INVISIBLE);
            textError.setVisibility(View.VISIBLE);
        }
        buddyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sendIntent = new Intent(BuddyList.this,message.class);
                startActivity(sendIntent);
            }
        });
    }


    private void searchBuddies() {


        sText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if(s.length() >= 1) {
                    Log.i("aapter", s.length() + "");
                    bInfo.clear();
                    String match = sText.getText().toString();
                    Log.i("searchResult",searchResult.size()+"");
                    for(int i = 0;i<searchResult.size();i++){
                        if(searchResult.get(i).getbName().startsWith(match)){
                            Log.i("aapter","match foun");
                            bInfo.add(searchResult.get(i));
                        }
                    }
                    Log.i("aapter",bInfo.size()+"");
                }else {
                    bInfo.clear();
                    for (BuddyLocation b : searchResult) {
                        bInfo.add(b);
                    }
                }

                if (bInfo.size()>=1) {
                    Log.i("insi chng aapter","aapter");
                    buddyList.setVisibility(View.VISIBLE);
                    noMatch.setVisibility(View.INVISIBLE);
                    textError.setVisibility(View.INVISIBLE);
                    buddyListAdapter.notifyDataSetChanged();
                }else {
                    Log.i("aapter",s.length()+"");
                    buddyList.setVisibility(View.INVISIBLE);
                    noMatch.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private class GetBuddyListTask extends AsyncTask<String, Void, ArrayList<BuddyLocation>> {

        private ProgressDialog dialog;

        public GetBuddyListTask(Context context){
            dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMessage("Retrieving Buddy Location..");
            dialog.show();
        }

        @Override
        protected ArrayList doInBackground(String... params) {
            ArrayList<BuddyLocation> buddyLocation = Utility.getBuddyLocationFromServer(params[0]);
            Log.i("Bu_list", buddyLocation.size() + "");
            return buddyLocation;
        }
        @Override
        protected void onPostExecute(ArrayList<BuddyLocation> bLocation) {
            dialog.dismiss();
            bInfo = bLocation;
            for (BuddyLocation b:bLocation){
                searchResult.add(b);
            }
            Log.i("size",bInfo.size()+"");
            buddyListAdapter = new
                    BuddyListAdapter(BuddyList.this,R.layout.row_item,bInfo);
            buddyList.setAdapter(buddyListAdapter);

        }
    }

    private static class BuddyListAdapter extends ArrayAdapter<BuddyLocation>{

        final ArrayList<BuddyLocation> bList;
        final Context context;
        int rowItem;

        public BuddyListAdapter(Context context, int resource, ArrayList<BuddyLocation> bList) {
            super(context, resource, bList);
            this.context = context;
            this.bList = bList;
            rowItem = resource;
        }

         static class ViewHolder {
            public TextView bName;
            public TextView bLocation;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null){
                LayoutInflater inflater = (LayoutInflater)
                        context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.buddy_row,null);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.bName = (TextView) rowView.findViewById(R.id.bName);
                viewHolder.bLocation = (TextView) rowView.findViewById(R.id.bLocation);
                rowView.setTag(viewHolder);
            }

            ViewHolder vHolder = (ViewHolder) rowView.getTag();
            BuddyLocation bLocation = bList.get(position);
            Log.i("hhh",bLocation.getbName());
            String strName = bLocation.getbName();
            String strLocation = bLocation.getLocation();
            vHolder.bName.setText(strName);
            vHolder.bLocation.setText(strLocation);

            return rowView;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buddy_locator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.addBuddy) {
            Intent intent = new Intent(BuddyList.this,AddBuddy.class);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.refreshLocation) {
            buddyList.setVisibility(View.VISIBLE);
            noMatch.setVisibility(View.INVISIBLE);
            new GetBuddyListTask(this).execute(url);
            return true;
        } else if(id == R.id.settings){
            Intent intent = new Intent(BuddyList.this,Settings.class);
            startActivity(intent);
            finish();
            return true;
        }else {
            Intent intent = new Intent(BuddyList.this, Deletebuddies.class);
            startActivity(intent);
            finish();
            return true;
        }
    }
}
