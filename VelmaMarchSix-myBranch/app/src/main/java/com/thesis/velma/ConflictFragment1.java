package com.thesis.velma;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.thesis.velma.helper.DataBaseHandler;
import com.thesis.velma.helper.OkHttp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.thesis.velma.OnboardingFragment2.dateEnd;
import static com.thesis.velma.OnboardingFragment2.dateStart;
import static com.thesis.velma.OnboardingFragment2.timeStart;

/**
 * Created by admin on 2/27/2017.
 */

public class ConflictFragment1 extends AppCompatActivity {

    private static DataBaseHandler db;
    private ViewPager pager;
    private SmartTabLayout indicator;
    public Button skip;
    public Button BtnAddEvent;
    public EditText event;
    Context context;

    public static TextView des;
    public static EditText descrip;
    public static TextView loc;
    public static TextView locate;
    public static TextView distanceduration;
    int PLACE_PICKER_REQUEST = 1;
    Double latitude, longtiude;

    public static String geolocation;
    String modetravel = "driving";
    PlaceAutocompleteFragment autocompleteFragment;
    private PendingIntent pendingIntent;

    ArrayList<String> myConflictEvents = new ArrayList<String>();
    ArrayList<String> myCurrentEvent = new ArrayList<>();
    Dialog dialog;
    String eventID, newdate;
    String con_sd ="", con_ed ="", con_name="", con_desc="",location;

    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        context = this;

        db = new DataBaseHandler(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //then you use
        userEmail = prefs.getString("Email", null);


        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/avenir-next-regular.ttf");
        event = (EditText) findViewById(R.id.eventname);
        event.setTypeface(custom_font);
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (SmartTabLayout) findViewById(R.id.indicator);
        BtnAddEvent = (Button) findViewById(R.id.btnAddEvent);
        BtnAddEvent.setTypeface(custom_font);
        BtnAddEvent.setVisibility(View.GONE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        location = preferences.getString("location", "value");
        locate.setText(location);
        autocompleteFragment.setText(location);




        des = (TextView) findViewById(R.id.eventName);
        des.setTypeface(custom_font);
        descrip = (EditText) findViewById(R.id.name);
        descrip.setTypeface(custom_font);
        loc = (TextView) findViewById(R.id.location);
        loc.setTypeface(custom_font);
        locate = (TextView) findViewById(R.id.locationText);
        locate.setTypeface(custom_font);
        locate.setHintTextColor(getResources().getColor(R.color.colorPrimary));


        distanceduration = (TextView) findViewById(R.id.distanceduration);
        distanceduration.setTypeface(custom_font);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);



        final AlertDialog a_build = new AlertDialog.Builder(context)
                .setTitle("Velma Suggestion")
                .setMessage("Do you want Velma to suggest the time? ")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent inn = new Intent(ConflictFragment1.this, DateListView.class);
                        startActivity(inn);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.timetimetime)
                .show();


        Intent i = getIntent();
        String id = i.getStringExtra("eventID");
        newdate = i.getStringExtra("newdate");
        Log.i("Event Ac1", ""+newdate + " "+id);

        final Cursor c = db.editConflictEvent(Long.valueOf(id));
        Log.i("Event Ac2", ""+c);

        c.moveToFirst();

        con_name = c.getString(0);
        con_desc = c.getString(1);
        con_sd = c.getString(2);
        con_ed = c.getString(3);


        event.setText(con_name);
        descrip.setText(con_desc);
//        OnboardingFragment2_1.dateStart.setText(con_sd);
//        OnboardingFragment2_1.dateEnd.setText(con_ed);
        Log.i("Event conAct",con_name +" "+ con_desc +" "+ con_sd +" "+ con_ed);
        //finCursor
        //   a_build.dismiss();
//
//        if(newdate =="1:00 - 2:00"){
//            OnboardingFragment2_1.dateStart.setText("1:00:00");
//            OnboardingFragment2_1.dateEnd.setText("2:00:00");
//        }
//        else if (newdate == "2:00 - 3:00"){
//            OnboardingFragment2_1.dateStart.setText("2:00:00");
//            OnboardingFragment2_1.dateEnd.setText("3:00:00");
//        }
//        else if (newdate == "3:00 - 4:00"){
//            OnboardingFragment2_1.dateStart.setText("3:00:00");
//            OnboardingFragment2_1.dateEnd.setText("4:00:00");
//        }
//
//        else if (newdate ==("4:00 - 5:00")){
//            OnboardingFragment2_1.dateStart.setText("4:00:00");
//            OnboardingFragment2_1.dateEnd.setText("5:00:00");
//        }
//
//        else if (newdate == ("5:00 - 6:00")){
//            OnboardingFragment2_1.dateStart.setText("5:00:00");
//            OnboardingFragment2_1.dateEnd.setText("6:00:00");
//        }
//
//        else if (newdate == "6:00 - 7:00"){
//            OnboardingFragment2_1.dateStart.setText("6:00:00");
//            OnboardingFragment2_1.dateEnd.setText("7:00:00");
//        }
//
//        else if (newdate =="7:00 - 8:00"){
//            OnboardingFragment2_1.dateStart.setText("7:00:00");
//            OnboardingFragment2_1.dateEnd.setText("8:00:00");
//        }
//
//        else if (newdate == "8:00 - 9:00"){
//            OnboardingFragment2_1.dateStart.setText("8:00:00");
//            OnboardingFragment2_1.dateEnd.setText("9:00:00");
//        }
//
//        else if (newdate  == "9:00 - 10:00"){
//            OnboardingFragment2_1.dateStart.setText("9:00:00");
//            OnboardingFragment2_1.dateEnd.setText("10:00:00");
//        }
//
//        else if (newdate == "10:00 - 11:00"){
//            OnboardingFragment2_1.dateStart.setText("10:00:00");
//            OnboardingFragment2_1.dateEnd.setText("11:00:00");
//        }
//
//        else if (newdate == "11:00 - 12:00"){
//            OnboardingFragment2_1.dateStart.setText("11:00:00");
//            OnboardingFragment2_1.dateEnd.setText("12:00:00");
//        }
//
//
//        else if (newdate  == "12:00 - 13:00"){
//            OnboardingFragment2_1.dateStart.setText("12:00:00");
//            OnboardingFragment2_1.dateEnd.setText("13:00:00");
//        }
//
//        else if (newdate == "13:00 - 14:00"){
//            OnboardingFragment2_1.dateStart.setText("13:00:00");
//            OnboardingFragment2_1.dateEnd.setText("14:00:00");
//        }
//
//        else if (newdate == "14:00 - 15:00"){
//            OnboardingFragment2_1.dateStart.setText("14:00:00");
//            OnboardingFragment2_1.dateEnd.setText("15:00:00");
//        }
//
//        else if (newdate == "15:00 - 16:00"){
//            OnboardingFragment2_1.dateStart.setText("15:00:00");
//            OnboardingFragment2_1.dateEnd.setText("16:00:00");
//        }
//
//        else if (newdate == "16:00 - 17:00"){
//            OnboardingFragment2_1.dateStart.setText("16:00:00");
//            OnboardingFragment2_1.dateEnd.setText("17:00:00");
//        }
//
//        else if (newdate == "17:00 - 18:00"){
//            OnboardingFragment2_1.dateStart.setText("17:00:00");
//            OnboardingFragment2_1.dateEnd.setText("18:00:00");
//        }
//
//        else if (newdate == "18:00 - 19:00"){
//            OnboardingFragment2_1.dateStart.setText("18:00:00");
//            OnboardingFragment2_1.dateEnd.setText("19:00:00");
//        }
//
//        else if (newdate == "19:00 - 20:00"){
//            OnboardingFragment2_1.dateStart.setText("19:00:00");
//            OnboardingFragment2_1.dateEnd.setText("20:00:00");
//        }
//
//        else if (newdate == "20:00 - 21:00"){
//            OnboardingFragment2_1.dateStart.setText("20:00:00");
//            OnboardingFragment2_1.dateEnd.setText("21:00:00");
//        }
//        else if (newdate == "21:00 - 22:00"){
//            OnboardingFragment2_1.dateStart.setText("21:00:00");
//            OnboardingFragment2_1.dateEnd.setText("22:00:00");
//        }
//        else if (newdate == "22:00 - 23:00"){
//            OnboardingFragment2_1.dateStart.setText("22:00:00");
//            OnboardingFragment2_1.dateEnd.setText("23:00:00");
//        }
//
//        else if (newdate == "23:00 - 24:00"){
//            OnboardingFragment2_1.dateStart.setText("23:00:00");
//            OnboardingFragment2_1.dateEnd.setText("24:00:00");
//        }






        autocompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(14.599512, 120.984222),
                new LatLng(14.599512, 120.984222)));


        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_GEOCODE)
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getAddress());//get place details here

                locate.setText("" + place.getAddress());
                geolocation = place.getAddress().toString();

                latitude = place.getLatLng().latitude;
                longtiude = place.getLatLng().longitude;


                Log.d("latlang", "" + latitude + ":" + longtiude);

                new getDetails().execute();

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new ConflictFragment2();
                    case 1:
                        return new ConflictFragment3();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        pager.setAdapter(adapter);

        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    BtnAddEvent.setVisibility(View.GONE);
                } else if (position == 1) {
                    BtnAddEvent.setVisibility(View.VISIBLE);
                }

            }

        });


        BtnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Random r = new Random();
                final long unixtime = (long) (1293861599 + r.nextDouble() * 60 * 60 * 24 * 365);
                final String lat = ""+latitude;
                final String lng = ""+longtiude;

                final String name = event.getText().toString();
                final String eventDescription = descrip.getText().toString();
                final String eventLocation = locate.getText().toString();
                final String startDate = dateStart.getText().toString();
                final String endDate = dateEnd.getText().toString();
                final String startTime = timeStart.getText().toString();
                final String endTime = OnboardingFragment2.timeEnd.getText().toString();
                final String notify = OnboardingFragment2.alarming.getText().toString();
                final String invitedContacts = OnboardingFragment3.mtxtinvited.getText().toString();



                myCurrentEvent.add(name);
                myCurrentEvent.add(eventDescription);
                myCurrentEvent.add(eventLocation);
                myCurrentEvent.add(startDate);
                myCurrentEvent.add(endDate);
                myCurrentEvent.add(startTime);
                myCurrentEvent.add(endTime);
                myCurrentEvent.add(notify);
                myCurrentEvent.add(invitedContacts);
                Log.d("StarTime", startDate + " " + startTime);
                Log.d("EndTime", endDate + " " + endTime);


                final Cursor c = db.conflictChecker(startDate,startTime,endDate,endTime);
                String[] mydates = startDate.split("-");
                String[] mytimes = startTime.split(":");


                //HARDCODED VALUES 10:51
                Calendar calNow = Calendar.getInstance();
                Calendar calSet = (Calendar) calNow.clone();


                Log.d("Calendar.YEAR", "" + Integer.parseInt(mydates[2]));
                Log.d("Calendar.MONTH", "" + Integer.parseInt(mydates[1]));
                Log.d("Calendar.DATE", "" + Integer.parseInt(mydates[0]));
                Log.d("Calendar.HOUR_OF_DAY", "" + Integer.parseInt(mytimes[0]));
                Log.d("Calendar.MINUTE", "" + Integer.parseInt(mytimes[1]));

                String date=""+mydates[0]+"-"+mydates[1]+"-"+mydates[2];

                int AM_PM;
                if (Integer.parseInt(mytimes[0]) < 12) {
                    AM_PM = 0;
                } else {
                    AM_PM = 1;
                }

//                calSet.set(Calendar.YEAR, Integer.parseInt(mydates[2]));
//                calSet.set(Calendar.MONTH, Integer.parseInt(mydates[1])-1);
//                calSet.set(Calendar.DATE, Integer.parseInt(mydates[0]));
//                calSet.set(Calendar.HOUR, Integer.parseInt(mytimes[0]));
//                calSet.set(Calendar.MINUTE, Integer.parseInt(mytimes[1]));
//                calSet.set(Calendar.SECOND, 0);
//                calSet.set(Calendar.MILLISECOND, 0);
//                calSet.set(Calendar.AM_PM, AM_PM);

                calSet.setTimeInMillis(System.currentTimeMillis());
                calSet.clear();
                calSet.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]) - 1, Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]));

                int count = LandingActivity.db.retrieveDayEvent();
//                LandingActivity.db.close();


                Intent myIntent = new Intent(context, AlarmReceiver.class);
                myIntent.putExtra("name", name);
                pendingIntent = PendingIntent.getBroadcast(context, count, myIntent, 0);
                Log.d("Count", ""+count);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);


//                Calendar myAlarmDate = Calendar.getInstance();
//                myAlarmDate.setTimeInMillis(System.currentTimeMillis());
//                //myAlarmDate.set(Integer.parseInt(mydates[1]), 11, 25, 12, 00, 0);
//                myAlarmDate.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]), Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]), 0);
//                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//                Intent _myIntent = new Intent(context, Alarm_Receiver.class);
//                _myIntent.putExtra("MyMessage", name);
//                PendingIntent _myPendingIntent = PendingIntent.getBroadcast(context, 123, _myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                alarmManager.set(AlarmManager.RTC_WAKEUP, myAlarmDate.getTimeInMillis(), _myPendingIntent);


                if (name.isEmpty()) {
                    Toast.makeText(context, "Invalid Event Name", Toast.LENGTH_SHORT).show();
                } else if (eventDescription.isEmpty()) {
                    Toast.makeText(context, "Invalid Event Description", Toast.LENGTH_SHORT).show();
                } else if (startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(context, "Please add Starting date and completion date.", Toast.LENGTH_SHORT).show();
                }

                else {



                    if(c != null  && c.getCount()>0){

                        c.moveToFirst();
                        while (!c.isAfterLast()) {

                            Toast.makeText(ConflictFragment1.this, "Conflict in: " + c.getString(1)+ "   "+ c.getString(2) + "  "+ c.getString(3)+ "  "+ c.getString(4), Toast.LENGTH_LONG).show();
                            Log.i("Event log", c.getString(0) +" >> " +  c.getString(1) +" >> " + c.getString(2) +" >> " +  c.getString(3) +" >> " + c.getString(4) +" >> ");


                            c.moveToNext();
                        }


                        new AlertDialog.Builder(context)
                                .setTitle("Error in inserting event")
                                .setMessage("There is conflict, please change date and time or prioritize this event")
                                .setPositiveButton("CHOOSE NEW EVENT", new DialogInterface.OnClickListener() {
                                    public void onClick(final DialogInterface dialog, int which) {



                                        dialog.dismiss();

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                                .setPositiveButton("Add current event", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {



                                                        myCurrentEvent.add(name);
                                                        myCurrentEvent.add(eventDescription);
                                                        myCurrentEvent.add(eventLocation);
                                                        myCurrentEvent.add(startDate);
                                                        myCurrentEvent.add(endDate);
                                                        myCurrentEvent.add(startTime);
                                                        myCurrentEvent.add(endTime);
                                                        myCurrentEvent.add(notify);
                                                        myCurrentEvent.add(invitedContacts);

                                                        c.moveToFirst();

                                                        event.setText(c.getString(1));
                                                        descrip.setText(c.getString(2));
                                                        OnboardingFragment2.dateStart.setText(c.getString(3));
                                                        OnboardingFragment2.dateEnd.setText(c.getString(6));
                                                        Log.i("Event con",c.getString(1) +" "+ c.getString(2) +" "+ c.getString(3) +" "+ c.getString(6));


//                                                        String e_name, e_desc, e_sd, e_ed;
//                                                        e_name = c.getString(1);
//                                                        e_desc = c.getString(2);
//                                                        e_sd = c.getString(3);
//                                                        e_ed =  c.getString(6);
//
//
//                                                        event.setText(e_name);
//                                                        descrip.setText(e_desc);
//                                                        OnboardingFragment2.dateStart.setText(e_sd);
//                                                        OnboardingFragment2.dateEnd.setText(e_ed);
//                                                        Log.i("Event con", e_name +" "+ e_desc +" "+ e_sd +" "+ e_ed);

//                                                        Intent in = new Intent(OnboardingActivity.this, OnboardingActivity2.class);
//                                                        startActivity(in);
//
//                                                        db.saveEvent(LandingActivity.imei, unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts);
//                                                        OkHttp.getInstance(getBaseContext()).saveEvent(unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts);

                                                        LandingActivity.db.saveEvent(LandingActivity.imei, unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);
                                                        OkHttp.getInstance(getBaseContext()).saveEvent(unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);


//                                                        for (int i = 0; i <= OnboardingFragment3.invitedContacts.size() - 1; i++) {
//                                                            String[] target = OnboardingFragment3.invitedContacts.get(i).split("@");
//                                                            OkHttp.getInstance(context).sendNotification("Invitation", unixtime, name, eventDescription, eventLocation,
//                                                                    startDate, startTime, endDate, endTime, notify, invitedContacts, target[0] + "Velma");//target[0]
//                                                        }

                                                        for (int i = 0; i <= OnboardingFragment3.invitedContacts.size() - 1; i++) {
                                                            String[] target = OnboardingFragment3.invitedContacts.get(i).split("@");
                                                            OkHttp.getInstance(context).sendNotification("Invitation", unixtime, name, eventDescription, eventLocation,
                                                                    startDate, startTime, endDate, endTime, notify, invitedContacts, target[0] + "Velma", lat, lng);//target[0]
                                                        }


                                                        Intent intent = new Intent();
                                                        setResult(RESULT_OK, intent);
                                                        finish();

                                                        db.deleteEvent(Long.parseLong(eventID));
                                                        OkHttp.getInstance(context).deleteEvent(eventID);

//
//                                                            Intent intent = new Intent();
//                                                            setResult(RESULT_OK, intent);
//                                                            finish();
                                                        dialog.dismiss();


                                                    }
                                                }).setNegativeButton("Change", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {


                                                        dialog.dismiss();
                                                        OnboardingFragment2.dateStart.setText("");
                                                        OnboardingFragment2.dateEnd.setText("");
                                                        OnboardingFragment2.timeStart.setText("");
                                                        OnboardingFragment2.timeEnd.setText("");
                                                        dialog.dismiss();



                                                        // Do stuff when user neglects.
                                                    }
                                                });
                                        builder.setTitle("Select Event Priority");

////////////////////////////////////////////////////////////////////////////////////
                                        ListView modeList = new ListView(context);
                                        myConflictEvents.add(myCurrentEvent.get(0));
                                        c.moveToFirst();
                                        eventID =  c.getString(0);

                                        while (!c.isAfterLast()) {


                                            Log.i("Event conn", " "+c.getString(c.getColumnIndex("EventName")) );
                                            Log.i("Event id", eventID);
                                            myConflictEvents.add(c.getString(c.getColumnIndex("EventName"))); //this adds an element to the list.
                                            c.moveToNext();
                                        }



                                        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1,myConflictEvents );
                                        modeList.setAdapter(modeAdapter);
                                        builder.setView(modeList);
                                        builder.create();
                                        builder.show();


                                    }
                                })
                                .setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        dialog.dismiss();
                                    }
                                })


                                .setIcon(R.drawable.velma)
                                .show();

//


                    }

                    else {

                        LandingActivity.db.saveEvent(LandingActivity.imei, unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);
                        OkHttp.getInstance(getBaseContext()).saveEvent(unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);


//                                                        for (int i = 0; i <= OnboardingFragment3.invitedContacts.size() - 1; i++) {
//                                                            String[] target = OnboardingFragment3.invitedContacts.get(i).split("@");
//                                                            OkHttp.getInstance(context).sendNotification("Invitation", unixtime, name, eventDescription, eventLocation,
//                                                                    startDate, startTime, endDate, endTime, notify, invitedContacts, target[0] + "Velma");//target[0]
//                                                        }

                        for (int i = 0; i <= OnboardingFragment3.invitedContacts.size() - 1; i++) {
                            String[] target = OnboardingFragment3.invitedContacts.get(i).split("@");
                            OkHttp.getInstance(context).sendNotification("Invitation", unixtime, name, eventDescription, eventLocation,
                                    startDate, startTime, endDate, endTime, notify, invitedContacts, target[0] + "Velma", lat, lng);//target[0]
                        }
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();


                    }
                }


            }
        });



    }

    class getDetails extends AsyncTask<Void, Void, String> {

        protected String getASCIIContentFromEntity(HttpEntity entity)
                throws IllegalStateException, IOException {
            InputStream in = entity.getContent();
            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0)
                    out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected String doInBackground(Void... params) {

            String text = null;
            String coordinates = latitude + "," + longtiude;
            Log.d("Coordinates", coordinates);
            try {
                String regAPIURL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" + LandingActivity.origlatitude + "," + LandingActivity.origlongitude;
                regAPIURL = regAPIURL + "&destinations=" + URLEncoder.encode(coordinates);
                regAPIURL = regAPIURL + "&mode=" + URLEncoder.encode(modetravel);
                regAPIURL = regAPIURL + "&key=AIzaSyDWjoAbJf9uDrLCFAM_fCSWxP0muVEGbOA";
                Log.d("URI", regAPIURL);
                HttpGet httpGet = new HttpGet(regAPIURL);
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 60000;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 60000;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                HttpResponse response = httpClient.execute(httpGet);
                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);

            } catch (Exception e) {
                text = null;
            }

            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("Result", s);


            if (s != null) {


                try {


                    String distance = new JSONObject(s)
                            .getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0)
                            .getJSONObject("distance").getString("text");

                    String duration = new JSONObject(s)
                            .getJSONArray("rows")
                            .getJSONObject(0)
                            .getJSONArray("elements")
                            .getJSONObject(0)
                            .getJSONObject("duration").getString("text");

                    distanceduration.setText("Distance : " + distance + ": Duration : " + duration);
                    distanceduration.setVisibility(View.VISIBLE);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    }


}
