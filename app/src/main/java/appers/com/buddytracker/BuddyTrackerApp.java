package appers.com.buddytracker;

import android.app.Application;

/**
 * Created by User on 3/27/2015.
 */
public class BuddyTrackerApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        /*
        1. open db connection and Create a table (user if not present in db)
        2. Query the user table and get the row count
        3. if row count equals to 1, show BuddyLocator page
        4. else show JoinUs page
         */

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //close db connection
    }
}
