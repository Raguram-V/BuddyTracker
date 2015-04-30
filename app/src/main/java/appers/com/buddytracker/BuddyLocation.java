package appers.com.buddytracker;

/**
 * Created by User on 4/26/2015.
 */
public class BuddyLocation {
    private String bName;
    private String location;

    public BuddyLocation(String bName,String location){
        this.bName = bName;
        this.location = location;
    }
    public String getbName() {
        return bName;
    }
    public String getLocation() {
        return location;
    }
}
