package appers.com.buddytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by User on 4/20/2015.
 */
public class BuddyDB extends SQLiteOpenHelper {

    public static final String TABLE_BUDDIES = "Buddies";
    public static final String BUDDY_ID = "Id";
    public static final String  BUDDY_NAME= "buddyName";

    public static final String  DB_NAME= "buddies.db";
    public static final int  DB_VERSION= 1;

    public static final String  DB_CREATE= "create table "
            + TABLE_BUDDIES + "(" + BUDDY_ID
            + " integer primary key autoincrement, " + BUDDY_NAME
            + " text not null" + ");";

    public String[] allColumns = {
            BUDDY_ID, BUDDY_NAME
    };


    public BuddyDB (Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public boolean addBuddy(String strBuddyName){
        SQLiteDatabase sdb = getWritableDatabase();
        ContentValues cValues = new ContentValues();
        cValues.put(BUDDY_NAME,strBuddyName);
        long newRow = sdb.insert(TABLE_BUDDIES,null,cValues);
        sdb.close();

        if (newRow == -1) {
            return false;
        }else
            return true;
    }

    public boolean isBuddyExist(String strBuddyName){
        boolean flag;
        SQLiteDatabase sdb = getReadableDatabase();
        Cursor c = sdb.query(TABLE_BUDDIES,new String[] {BUDDY_NAME},BUDDY_NAME + "=?",
                new String[] {strBuddyName},null,null,null,null);

        int rowCount = c.getCount();
        Log.i("Count", rowCount + "");
        if(rowCount >= 1)
            flag = true;
        else
            flag = false;
        sdb.close();
        return flag;
    }


    public ArrayList<String> getBuddies(){
        ArrayList<String> buddies = new ArrayList<String>();
        SQLiteDatabase sdb = getReadableDatabase();
        Cursor c = sdb.query(TABLE_BUDDIES,new String[] {BUDDY_NAME},null,null,null,null,null);
        Log.i("row count",c.getCount()+"");

        if (c.moveToFirst()) {
            while(!c.isAfterLast()) {
                buddies.add(c.getString(c.getColumnIndex(BUDDY_NAME)));
                c.moveToNext();
            }
        }
        sdb.close();
        return buddies;
    }

    public boolean deleteBuddies(ArrayList<String> buddies){
        SQLiteDatabase sdb = getWritableDatabase();
        int delRows = sdb.delete(TABLE_BUDDIES,BUDDY_NAME,(String[]) buddies.toArray());
        sdb.close();
        if (delRows > 0) {
            return true;
        }else
            return false;
    }

}
