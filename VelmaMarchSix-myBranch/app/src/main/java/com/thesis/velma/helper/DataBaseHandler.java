package com.thesis.velma.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.thesis.velma.helper.DBInfo.DataInfo;

import java.util.StringTokenizer;

import static com.thesis.velma.LandingActivity.db;

/**
 * Created by admin on 03/01/2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    public static final int database_version = 1;

    public String CREATE_EVENTS = "CREATE TABLE " + DataInfo.TBl_Events + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "" + DataInfo.UserID + " TEXT, " +
            "" + DataInfo.EventID + " TEXT, " +
            DataInfo.EventName + " TEXT," + DataInfo.EventDescription + " TEXT," + DataInfo.EventLocation + " TEXT," +
            DataInfo.EventStartDate + " TEXT," + DataInfo.EventStartTime + " TEXT," + DataInfo.EventEndDate + " TEXT," +
            DataInfo.EventEndTime + " TEXT," + DataInfo.Notify + " TEXT," + DataInfo.Extra1 + " TEXT," +
            DataInfo.Extra2 + " TEXT," + DataInfo.Extra3 + " TEXT," + DataInfo.Extra4 + " TEXT)";
    public String CREATE_CONTACTS = "CREATE TABLE " + DataInfo.TBlContacts + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DataInfo.Name + " TEXT," + DataInfo.EmailAdd + " TEXT," + DataInfo.Extra1 + " TEXT," +
            DataInfo.Extra2 + " TEXT," + DataInfo.Extra3 + " TEXT," + DataInfo.Extra4 + " TEXT)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS);
        db.execSQL(CREATE_CONTACTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    //region CONSTRUCTOR
    //***********************
    //CONSTRUCTOR
    //***********************
    public DataBaseHandler(Context context) {
        super(context, DataInfo.DATABASE_NAME, null, database_version);

    }

    //endregion

    //region METHODS

    public void saveEvent(String userid, Long eventid, String eventname, String eventDescription, String eventLocation,
                          String eventStartDate, String eventStartTime, String eventEndDate, String eventEndTime, String notify, String invitedfirends,
                           String userEmail, String lat, String lng) {

        SQLiteDatabase sql = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(DataInfo.UserID, userid);
        cv.put(DataInfo.EventID, eventid);
        cv.put(DataInfo.EventName, eventname);
        cv.put(DataInfo.EventDescription, eventDescription);
        cv.put(DataInfo.EventLocation, eventLocation);
        cv.put(DataInfo.EventStartDate, eventStartDate);
        cv.put(DataInfo.EventStartTime, eventStartTime);
        cv.put(DataInfo.EventEndDate, eventEndDate);
        cv.put(DataInfo.EventEndTime, eventEndTime);
        cv.put(DataInfo.Notify, notify);
        cv.put(DataInfo.Extra1, invitedfirends);
        cv.put(DataInfo.Extra2, userEmail);
        cv.put(DataInfo.Extra3, lat);
        cv.put(DataInfo.Extra4, lng);

        sql.insert(DataInfo.TBl_Events, null, cv);
    }

    public void saveContact(String name, String email) {

        SQLiteDatabase sql = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(DataInfo.Name, name);
        cv.put(DataInfo.EmailAdd, email);

        sql.insert(DataInfo.TBlContacts, null, cv);

    }

    public Cursor getEvents() {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events, null);

        return c;

    }

    public Cursor getContacts() {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT DISTINCT * FROM " + DataInfo.TBlContacts, null);

        return c;

    }

    public Cursor getEventDetails(long id) {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events + " Where _id=" + id, null);

        return c;
    }

    public void deleteEvent(long id) {

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(DataInfo.TBl_Events, "_id =" + id, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
    public Cursor getids(){

        SQLiteDatabase sql = db.getReadableDatabase();
        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events, null);

        return c;
    }
public Cursor getid(String eId){

    SQLiteDatabase sql = db.getReadableDatabase();
    Cursor c = sql.rawQuery("SELECT _id FROM " + DataInfo.TBl_Events+
            " WHERE EventID = '"+eId+"'", null);

    return c;
}
    public void deleteInvite(long eId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(DataInfo.TBl_Events, "EventID =" + eId, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    //endregion
    public Cursor conflictChecker(String sd, String st, String ed, String et) {

        SQLiteDatabase sql = db.getReadableDatabase();

    Cursor c = sql.rawQuery("SELECT _id, EventName, EventDescription,StartDate, StartTime, EndTime, EndDate  FROM " + DataInfo.TBl_Events+
        " WHERE ((StartTime BETWEEN '"+st+"' AND '"+et+
                "' ) AND ((StartDate BETWEEN '"+sd+"' AND '"+ed+
                "') OR (EndDate BETWEEN '"+sd+"' AND '"+ed+"') OR ('"+sd+
                "' BETWEEN StartDate AND EndDate))) OR ((EndTime BETWEEN '"+st+
                "' AND '"+et+"') AND ((StartDate BETWEEN '"+sd+"' AND '"+ed+
                "') OR (EndDate BETWEEN '"+sd+"' AND '"+ed+"') OR ('"+sd+
                "' BETWEEN StartDate AND EndDate))) OR (('"+st+
                "' BETWEEN StartTime AND EndTime) AND ((StartDate BETWEEN '"+sd+"' AND '"+ed+
                "') OR (EndDate BETWEEN '"+sd+"' AND '"+ed+"') OR ('"+sd+
                "' BETWEEN StartDate AND EndDate)))", null);

        return c;
    }

    public int retrieveDayEvent() {
        String countQuery = "SELECT  * FROM " + DataInfo.TBl_Events;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        Log.d("Velama", "" + cnt);
        cursor.close();
        return cnt;
    }

    public Cursor editConflictEvent(long id) {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT EventName, EventDescription, StartDate, EndDate FROM " + DataInfo.TBl_Events + " Where _id=" + id, null);

        return c;
    }

//    public void updateTable(long eventID, String eventName, String eventDescription, String eventLocation, String startDate,
//                            String startTime, String endDate, String endTime, String notify, String invitedFriends) {
//
//        SQLiteDatabase sql = this.getWritableDatabase();
//
//        ContentValues cv = new ContentValues();
//
//        cv.put(DataInfo.EventName, eventName);
//        cv.put(DataInfo.EventDescription, eventDescription);
//        cv.put(DataInfo.EventLocation, eventLocation);
//        cv.put(DataInfo.EventStartDate, startDate);
//        cv.put(DataInfo.EventStartTime, startTime);
//        cv.put(DataInfo.EventEndDate, endDate);
//        cv.put(DataInfo.EventEndTime, endTime);
//        cv.put(DataInfo.Notify, notify);
//        cv.put(DataInfo.Extra1, invitedFriends);
//
//        sql.update(DataInfo.TBl_Events, cv, "_id=" + eventID, null);
//        sql.close();
//
//    }


    public void updateEvent(long eventID, String eventName, String eventDescription, String eventLocation, String startDate,
                            String startTime, String endDate, String endTime, String notify, String invitedFriends, String Extra3, String Extra4) {

        SQLiteDatabase sql = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(DataInfo.EventName, eventName);
        cv.put(DataInfo.EventDescription, eventDescription);
        cv.put(DataInfo.EventLocation, eventLocation);
        cv.put(DataInfo.EventStartDate, startDate);
        cv.put(DataInfo.EventStartTime, startTime);
        cv.put(DataInfo.EventEndDate, endDate);
        cv.put(DataInfo.EventEndTime, endTime);
        cv.put(DataInfo.Notify, notify);
        cv.put(DataInfo.Extra1, invitedFriends);
        cv.put(DataInfo.Extra3, Extra3);
        cv.put(DataInfo.Extra4, Extra4);

        sql.update(DataInfo.TBl_Events, cv, "_id=" + eventID, null);
        sql.close();
    }

    public Cursor compareLocationA(String sd, String st) {

        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events + " WHERE (StartDate = '"+sd+"' AND EndTime <= '"+st+
                "') ORDER BY StartTime DESC LIMIT 1", null);

        return c;
    }

    public Cursor compareLocationB(String sd, String et) {
        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events + " WHERE (StartDate = '"+sd+"' AND StartTime >= '"+et+
                "') ORDER BY StartTime ASC LIMIT 1", null);

        return c;
    }

    public Cursor getEventNames(String sd, String ed,int i,int flag) {

        String minute1;
        String minute2;
        if((flag%2)==0) {
            minute1 = "00";
            minute2 = "30";
        }else{
            minute1 = "30";
            minute2 = "00";
        }

        String i2 =""+i;
        if(i2.length()==1){
            i2="0"+i2;
        }
        String temp2;
        if((flag%2)==0) {
            temp2 =""+(i);
        }else{
            temp2 =""+(i+1);
        }

        if(temp2.length()==1){
            temp2="0"+temp2;
        }

        if(i == 23){
            minute2 = "59";
            temp2=""+i;
        }
        Log.i("Event sd", sd);
        Log.i("Event ed", ed);
        Log.i("Event st", i2+minute1);
        Log.i("Event et", temp2+minute2);
        SQLiteDatabase sql = db.getReadableDatabase();

        Cursor c = sql.rawQuery("SELECT EventName FROM "+ DataInfo.TBl_Events+
                " WHERE ((StartDate BETWEEN '"+sd+"' AND '"+ed+
                "') OR (EndDate BETWEEN "+sd+" AND "+ed+
                ")) AND (('"+i2+""+minute1+"' BETWEEN StartTime AND EndTime) OR('"+temp2+
                ""+minute2+"' BETWEEN StartTime AND EndTime))", null);

        return c;
    }
//                      (substr(StartDate,7)||substr(StartDate,4,2)||substr(StartDate,1,2))

    public void deleteTable ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DataInfo.TBl_Events, null, null);
        db.close();
    }

    public Cursor searchEvents(String startDate, String startTime, String endDate, String endTime) {

        SQLiteDatabase sql = db.getReadableDatabase();

        Log.d("Query", "SELECT * FROM " + DataInfo.TBl_Events + " Where StartDate= '" + startDate + "' AND StartTime between '" + startTime + "' AND '" + endTime + "'");
        Cursor c = sql.rawQuery("SELECT * FROM " + DataInfo.TBl_Events + " Where StartDate= '" + startDate + "' ", null);//AND StartTime between '" + startTime + "' AND '" + endTime + "'

        return c;
    }

    public Cursor getMaxId()
    {
        SQLiteDatabase sql = db.getReadableDatabase();
        Cursor c = sql.rawQuery("SELECT _id FROM " + DataInfo.TBl_Events +" ORDER BY _id DESC LIMIT 1", null);


        return c;
    }

}
