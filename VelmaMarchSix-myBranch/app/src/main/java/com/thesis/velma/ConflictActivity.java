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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static com.thesis.velma.ConflictFragment2.dateEnd;
import static com.thesis.velma.ConflictFragment2.dateStart;
import static com.thesis.velma.ConflictFragment2.timeStart;

/**
 * Created by admin on 8/10/2016.
 */
public class ConflictActivity extends AppCompatActivity {

    private static DataBaseHandler db;
    private ViewPager pager;
    private SmartTabLayout indicator;
    public Button skip;
    public Button BtnAddEvent;
    public EditText event;
    Context context;
    public CoordinatorLayout coordinatorLayout;

    public static TextView des;
    public static EditText descrip;
    public static TextView loc;
    public static TextView locate;
    public static TextView distanceduration;
    int PLACE_PICKER_REQUEST = 1;
    Double longtiude, latitude;

    public static String geolocation;
    String modetravel = "driving";
    PlaceAutocompleteFragment autocompleteFragment;
    private PendingIntent pendingIntent;

    ArrayList<String> myConflictEvents = new ArrayList<String>();
    ArrayList<String> myCurrentEvent = new ArrayList<>();
    Dialog dialog;
    String eventID, newdate, id;
    String con_sd, con_ed, con_name, con_desc;
    String e_name, e_desc, e_sd, e_ed, e_id, new_st, new_et, location;
    String coordinates;

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
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        event.setEnabled(false);
        descrip.setEnabled(false);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String name = preferences.getString("name", "value");
        String description = preferences.getString("desc", "value");
        coordinates = preferences.getString("coordinates", "value");
        location = preferences.getString("location", "value");
        locate.setText(location);
        autocompleteFragment.setText(location);

        Intent i = getIntent();
        new_st= i.getStringExtra("new_st");
        new_et= i.getStringExtra("new_et");
        Log.i("Event Ac1", ""+new_st + " "+new_et);



        event.setText(name);
        descrip.setText(description);
//        OnboardingFragment2_1.dateStart.setText(con_sd);
//        OnboardingFragment2_1.dateEnd.setText(con_ed);
        Log.i("Event conAct",con_name +" "+ con_desc );

        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        pref.putString("sd", con_sd);
        pref.putString("ed", con_ed);
        pref.putString("st", new_st);
        pref.putString("et", new_et);
        pref.apply();

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


                final String name = event.getText().toString();
                final String eventDescription = descrip.getText().toString();
                final String eventLocation = locate.getText().toString();
                final String startDate = dateStart.getText().toString();
                final String endDate = dateEnd.getText().toString();
                final String startTime = ConflictFragment2.timeStart.getText().toString();
                final String endTime = ConflictFragment2.timeEnd.getText().toString();
                final String notify = ConflictFragment2.alarming.getText().toString();
                final String invitedContacts = ConflictFragment3.mtxtinvited.getText().toString();
                final String lat = ""+latitude;
                final String lng = ""+longtiude;


                final Cursor c = db.conflictChecker(startDate,startTime,endDate,endTime);



                Log.d("StarTime", startDate + " " + startTime);
                Log.d("EndTime", endDate + " " + endTime);

//                }
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


                calSet.setTimeInMillis(System.currentTimeMillis());
                calSet.clear();
                calSet.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]) - 1, Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1])-30);




                if (name.isEmpty()) {
                    event.setError("Enter event name");
                }
                if (eventDescription.isEmpty()) {
                    descrip.setError("Enter description");
                }
                if(eventLocation.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please add location", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                if (startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Please add date and time details", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
                StringTokenizer sd = new StringTokenizer(startDate, "-");
                StringTokenizer ed = new StringTokenizer(endDate, "-");
                StringTokenizer st = new StringTokenizer(startTime, ":");
                StringTokenizer et = new StringTokenizer(endTime, ":");

                String sdate = sd.nextToken() + sd.nextToken() + sd.nextToken();
                String edate = ed.nextToken() + ed.nextToken() + ed.nextToken();
                String stime = st.nextToken() + st.nextToken();
                String etime = et.nextToken() + et.nextToken();

                int sdd = Integer.parseInt(sdate);
                int edd = Integer.parseInt(edate);
                int stt = Integer.parseInt(stime);
                int ett = Integer.parseInt(etime);
                if (sdd > edd) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Invalid input: Start date is greater than end date", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else if (stt > ett) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Invalid input: Start time is greater than end time", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {





                    Log.i("Event con", "1");


                    if(c != null  && c.getCount()>0){

                        Log.i("Event con", "2");
                        c.moveToFirst();
                        while (!c.isAfterLast()) {

                            Toast.makeText(ConflictActivity.this, "Conflict in: " + c.getString(1)+ "   "+ c.getString(2) + "  "+ c.getString(3)+ "  "+ c.getString(4), Toast.LENGTH_LONG).show();
                            Log.i("Event log", c.getString(0) +" >> " +  c.getString(1) +" >> " + c.getString(2) +" >> " +  c.getString(3) +" >> " + c.getString(4) +" >> ");


                            c.moveToNext();
                        }






                        final AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("Error in inserting event")
                                .setIcon(R.drawable.velmalogo)
                                .setMessage("There is conflict, please change date and time or prioritize this event")
                                .setPositiveButton("Add Anyway", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {



                                        Log.i("Event con", "4");
                                        myCurrentEvent.add(name);
                                        myCurrentEvent.add(eventDescription);
                                        myCurrentEvent.add(eventLocation);
                                        myCurrentEvent.add(startDate);
                                        myCurrentEvent.add(endDate);
                                        myCurrentEvent.add(startTime);
                                        myCurrentEvent.add(endTime);
                                        myCurrentEvent.add(notify);
                                        myCurrentEvent.add(invitedContacts);


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

                                        String date = "" + mydates[0] + "-" + mydates[1] + "-" + mydates[2];

                                        int AM_PM;
                                        if (Integer.parseInt(mytimes[0]) < 12) {
                                            AM_PM = 0;
                                        } else {
                                            AM_PM = 1;
                                        }

                                        calSet.setTimeInMillis(System.currentTimeMillis());
                                        calSet.clear();
                                        calSet.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]) - 1, Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]) - 30);

                                        int count = LandingActivity.db.retrieveDayEvent();
//                LandingActivity.db.close();


                                        Intent myIntent = new Intent(context, AlarmReceiver.class);


                                        myIntent.putExtra("unix", unixtime);
                                        myIntent.putExtra("name", name);
                                        myIntent.putExtra("description", eventDescription);
                                        myIntent.putExtra("location", eventLocation);
                                        myIntent.putExtra("start", startTime);
                                        myIntent.putExtra("end", endTime);
                                        myIntent.putExtra("dateS", startDate);
                                        myIntent.putExtra("dateE", endDate);
                                        myIntent.putExtra("people", invitedContacts);

                                        Log.d("MyData", name);


                                        pendingIntent = PendingIntent.getBroadcast(context, count, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        Log.d("Count", "" + count);

                                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);



                                        LandingActivity.db.saveEvent(LandingActivity.imei, unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);
                                        OkHttp.getInstance(getBaseContext()).saveEvent(unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);

                                        for (int i = 0; i <= OnboardingFragment3.invitedContacts.size() - 1; i++) {
                                            String[] target = OnboardingFragment3.invitedContacts.get(i).split("@");
                                            OkHttp.getInstance(context).sendNotification("Invitation", unixtime, name, eventDescription, eventLocation,
                                                    startDate, startTime, endDate, endTime, notify, invitedContacts, target[0] + "Velma", lat, lng);//target[0]
                                        }



                                        dialog.dismiss();
                                        finish();

                                    }



                                }).setNeutralButton("Suggest",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent in = new Intent(ConflictActivity.this, DateListView.class);
                                                startActivity(in);
//
                                            }
                                        }

                                );


////////////////////////////////////////////////////////////////////////////////////
                        ListView modeList = new ListView(context);
                        myConflictEvents.clear();
                        myConflictEvents.add(myCurrentEvent.get(0));
                        c.moveToFirst();
                        eventID = c.getString(0);
                        Log.i("Event con", "5");

                        while (!c.isAfterLast()) {

                            StringTokenizer display1 = new StringTokenizer(c.getString(c.getColumnIndex("StartTime")), ":");
                            String s_hour = display1.nextToken();
                            String s_min = display1.nextToken();
                            StringTokenizer display2 = new StringTokenizer(c.getString(c.getColumnIndex("EndTime")), ":");
                            String e_hour = display2.nextToken();
                            String e_min = display2.nextToken();
                            String sdisplay = "";
                            String edisplay = "";
                            if (s_hour.length() == 1) {
                                s_hour = "0" + s_hour;
                            }
                            if (s_min.length() == 1) {
                                s_min = "0" + s_min;
                            }
                            if (Integer.parseInt(s_hour) > 12) {
                                s_hour = "" + (Integer.parseInt(s_hour) - 12);

                                sdisplay = s_hour + ":" + s_min + " PM";

                            } else if (Integer.parseInt(s_hour) == 12) {
                                sdisplay = s_hour + ":" + s_min + " PM";
                            } else {
                                sdisplay = s_hour + ":" + s_min + " AM";
                            }
                            if (e_hour.length() == 1) {
                                e_hour = "0" + e_hour;
                            }
                            if (e_min.length() == 1) {
                                e_min = "0" + e_min;
                            }
                            if (Integer.parseInt(e_hour) > 12) {
                                e_hour = "" + (Integer.parseInt(e_hour) - 12);

                                edisplay = e_hour + ":" + e_min + " PM";

                            } else if (Integer.parseInt(e_hour) == 12) {
                                edisplay = e_hour + ":" + e_min + " PM";
                            } else {
                                edisplay = e_hour + ":" + e_min + " AM";
                            }


                            Log.i("Event conn", " " + c.getString(c.getColumnIndex("EventName")));
                            Log.i("Event id", eventID);

                            myConflictEvents.add(c.getString(c.getColumnIndex("EventName")) + "   " + sdisplay + " to " + edisplay); //this adds an element to the list.
                            c.moveToNext();
                        }

                        ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, myConflictEvents);
                        modeList.setAdapter(modeAdapter);

                        builder.setView(modeList);

                        builder.create();
                        builder.show();
                    }





                    else {




                        Log.d("Calendar.YEAR", "" + Integer.parseInt(mydates[2]));
                        Log.d("Calendar.MONTH", "" + Integer.parseInt(mydates[1]));
                        Log.d("Calendar.DATE", "" + Integer.parseInt(mydates[0]));
                        Log.d("Calendar.HOUR_OF_DAY", "" + Integer.parseInt(mytimes[0]));
                        Log.d("Calendar.MINUTE", "" + Integer.parseInt(mytimes[1]));

                        calSet.setTimeInMillis(System.currentTimeMillis());
                        calSet.clear();
                        calSet.set(Integer.parseInt(mydates[2]), Integer.parseInt(mydates[1]) - 1, Integer.parseInt(mydates[0]), Integer.parseInt(mytimes[0]), Integer.parseInt(mytimes[1]) - 30);

                        int count = LandingActivity.db.retrieveDayEvent();
//                LandingActivity.db.close();


                        Intent myIntent = new Intent(context, AlarmReceiver.class);


                        myIntent.putExtra("unix", unixtime);
                        myIntent.putExtra("name", name);
                        myIntent.putExtra("description", eventDescription);
                        myIntent.putExtra("location", eventLocation);
                        myIntent.putExtra("start", startTime);
                        myIntent.putExtra("end", endTime);
                        myIntent.putExtra("dateS", startDate);
                        myIntent.putExtra("dateE", endDate);
                        myIntent.putExtra("people", invitedContacts);

                        Log.d("MyData", name);


                        pendingIntent = PendingIntent.getBroadcast(context, count, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Log.d("Count", "" + count);

                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pendingIntent);

                        LandingActivity.db.saveEvent(LandingActivity.imei, unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);
                        OkHttp.getInstance(getBaseContext()).saveEvent(unixtime, name, eventDescription, eventLocation, startDate, startTime, endDate, endTime, notify, invitedContacts, userEmail, lat, lng);


                        for (int i = 0; i <= OnboardingFragment3.invitedContacts.size() - 1; i++) {
                            String[] target = OnboardingFragment3.invitedContacts.get(i).split("@");
                            OkHttp.getInstance(context).sendNotification("Invitation", unixtime, name, eventDescription, eventLocation,
                                    startDate, startTime, endDate, endTime, notify, invitedContacts, target[0] + "Velma", lat, lng);//target[0]
                        }


                        Intent intent = new Intent(ConflictActivity.this, LandingActivity.class);
                        setResult(RESULT_OK, intent);
                        finish();
                        startActivity(intent);


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
