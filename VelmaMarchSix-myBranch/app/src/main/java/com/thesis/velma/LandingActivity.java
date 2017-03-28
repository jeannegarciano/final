package com.thesis.velma;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.PeopleScopes;
import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;
import com.thesis.velma.helper.CheckInternet;
import com.thesis.velma.helper.DataBaseHandler;
import com.thesis.velma.helper.NetworkUtil;
import com.thesis.velma.helper.PeopleHelper;
import com.thesis.velma.helper.progressDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LandingActivity extends AppCompatActivity implements CalendarPickerController, OnConnectionFailedListener, View.OnClickListener, ConnectionCallbacks {


    //CalendarPickerController, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<People.LoadPeopleResult> {

    //WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener

    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;


    private boolean is_signInBtn_clicked;
    private int request_code;
    private FloatingActionButton fabButton;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private static final int CREATE_EVENT = 0;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    FloatingActionButton fab;
    AgendaCalendarView mAgendaCalendarView;

    Calendar startdate, enddate;
    Context mcontext;
    final int CALENDAR_PERMISSION = 42;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public static DataBaseHandler db;
    private static final String[] LOCATION_PERMS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION
    };
    static double origlatitude = 0, origlongitude = 0;

    private static final int LOCATION_REQUEST = 3;
    LocationManager locationManager;

    MaterialDialog dialog;
    String eventID, id;


    public static String profilename, imei, useremail;
    String[] invitedFriends = null;


    CheckInternet connectCheck;
    Long unixtime = null;
    String name, eventDescription, eventLocation,
            startDate, startTime, endDate, endTime;

    public static final String ROOT_URL = "http://velma.000webhostapp.com";
    private List<EventsEntity> eventsEntityList;

    private boolean refreshOnClick = false;
    private static final String TAG = "LandingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //buildNewGoogleApiClient();
        setContentView(R.layout.activity_activity_landing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAgendaCalendarView = (AgendaCalendarView) findViewById(R.id.agenda_calendar_view);
        mcontext = this;

        db = new DataBaseHandler(mcontext);
        connectCheck = new CheckInternet(this);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
        //then you use
        profilename = prefs.getString("FullName", null);
        imei = prefs.getString("imei", null);
        useremail = prefs.getString("Email", null);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        Intent myIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);

        fab = (FloatingActionButton) findViewById(R.id.fabButton);


        fab.setOnClickListener(this);


        LoadEvents();


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            getCurrentLocation();


        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                    },
                    LOCATION_REQUEST);
        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestProfile()
                .requestEmail()
                .requestServerAuthCode(getString(R.string.client_id))
                .requestScopes(Plus.SCOPE_PLUS_LOGIN, Plus.SCOPE_PLUS_PROFILE, new Scope(PeopleScopes.CONTACTS_READONLY), new Scope("https://www.googleapis.com/auth/plus.profile.emails.read"))
                .requestScopes(new Scope("https://www.googleapis.com/auth/contacts.readonly"))
                .build();

        google_api_client = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .addApi(AppIndex.API).build();


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle data = intent.getExtras();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case LOCATION_REQUEST:
                getCurrentLocation();
                break;
        }
    }

    public void LoadEvents() {
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        List<CalendarEvent> eventList = getEventList();

        minDate.set(Calendar.DAY_OF_YEAR, 1);
        maxDate.add(Calendar.YEAR, 1);


        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
    }

    public void getCurrentLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {

            // Acquire a reference to the system Location Manager

            origlatitude = 10.3157007;
            origlongitude = 123.88544300000001;


// Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    //makeUseOfNewLocation(location);
                    Log.d("Latlng", "" + location);
                    origlatitude = location.getLatitude();
                    origlongitude = location.getLongitude();

                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onProviderDisabled(String provider) {
                }
            };

// Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        } else {
            //       Toast.makeText(getBaseContext(), "Location Failed", Toast.LENGTH_SHORT).show();
        }
    }


    // region Interface - CalendarPickerController
    @Override
    public void onDaySelected(DayItem dayItem) {

    }


    @Override
    public void onEventSelected(final CalendarEvent event) {

        if (event.getId() != 0) {


            Cursor c = db.getEventDetails(event.getId());


            while (c.moveToNext()) {

                eventID = c.getString(c.getColumnIndex("EventID"));
                invitedFriends = c.getString(c.getColumnIndex("Extra1")).split(",");


                id = c.getString(c.getColumnIndex("_id"));

                Intent intent = new Intent(getBaseContext(), ShowEventDetails.class);
                intent.putExtra("key", id);
                Log.d("id: ", id);
                startActivity(intent);


            }

        }
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        }
    }

    // endregion


    private void buidNewGoogleApiClient() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(google_api_client);
        startActivityForResult(signInIntent, 0002);

    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            progressDialog.showDialog(mcontext, "Velma", "Signing In. Please wait...", false);

            GoogleSignInAccount acct = result.getSignInAccount();
            // Get account information
            String mFullName = acct.getDisplayName();
            String mEmail = acct.getEmail();

            String[] topicname = mEmail.split("@");

            Log.d("Topic", topicname[0] + "Velma");

            new PeoplesAsync().execute(acct.getServerAuthCode());


        } else {
            progressDialog.hideDialog();
        }

    }


    @Override
    public void onClick(View view) {
        if (view == fab) {

            int status = NetworkUtil.getConnectivityStatusString(mcontext);

            if (status == 0) {
                CheckInternet.showConnectionDialog(mcontext);
            } else {

                Intent intent = new Intent(LandingActivity.this, OnboardingActivity.class);
                startActivityForResult(intent, CREATE_EVENT);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:

                int status = NetworkUtil.getConnectivityStatusString(mcontext);

                if (status == 0) {
                    CheckInternet.showConnectionDialog(mcontext);
                } else {


                    refreshOnClick = true;
                    if (refreshOnClick) {
                        final ProgressDialog loading = android.app.ProgressDialog.show(this, "Fetching Events", "Please wait...", false, false);

                        RestAdapter adapter = new RestAdapter.Builder()
                                .setEndpoint(ROOT_URL)
                                .build();

                        ApiService apiService = adapter.create(ApiService.class);
                        apiService.getMyJSON(new Callback<List<EventsEntity>>() {
                            @Override
                            public void success(List<EventsEntity> eventsEntities, Response response) {

                                loading.dismiss();

                                eventsEntityList = eventsEntities;

                                db.deleteTable();

                                for (int i = 0; i < eventsEntityList.size(); i++) {

                                    int id = eventsEntityList.get(i).getId();
                                    String userID = eventsEntityList.get(i).getUserID();
                                    long eventID = eventsEntityList.get(i).getEventID();
                                    String eventName = eventsEntityList.get(i).getEventName();
                                    String eventDescription = eventsEntityList.get(i).getEventDescription();
                                    String eventLocation = eventsEntityList.get(i).getEventLocation();
                                    String startDate = eventsEntityList.get(i).getStartDate();
                                    String startTime = eventsEntityList.get(i).getStartTime();
                                    String endDate = eventsEntityList.get(i).getEndDate();
                                    String endTime = eventsEntityList.get(i).getEndTime();
                                    String invitedFriends = eventsEntityList.get(i).getInvitedFriends();
                                    String extra1 = eventsEntityList.get(i).getExtra1();
                                    String extra2 = eventsEntityList.get(i).getExtra2();
                                    String extra3 = eventsEntityList.get(i).getExtra3();
                                    String extra4 = eventsEntityList.get(i).getExtra4();


                                    if (useremail.equals(extra2)) {
                                        Log.d("cathlyn:", userID);

                                        db.saveEvent(userID, eventID, eventName, eventDescription, eventLocation, startDate, startTime, endDate, endTime, invitedFriends, extra1, extra2, extra3, extra4);
                                    }

//                                    onRestart();
                                    LoadEvents();
                                    refreshOnClick = false;

                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        });
                    }

                    return true;


                }

                return true;

            case R.id.action_sync:
                buidNewGoogleApiClient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


//
//    @Override
//    protected void onRestart() {
//
//        // TODO Auto-generated method stub
//        super.onRestart();
//        Intent i = new Intent(LandingActivity.this, LandingActivity.class);  //your class
//        startActivity(i);
//        finish();
//    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (!connectionResult.hasResolution()) {
            google_api_availability.getErrorDialog(this, connectionResult.getErrorCode(), request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {


        if (requestCode == CREATE_EVENT) {
//            MyEvent myevent = null;

            if (responseCode == RESULT_OK) {
                LoadEvents();
//                Calendar minDate = Calendar.getInstance();
//                Calendar maxDate = Calendar.getInstance();
//
//                List<CalendarEvent> eventList = getEventList();
//
//                minDate.set(Calendar.DAY_OF_YEAR, 1);
//                maxDate.add(Calendar.YEAR, 1);
//
//                mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
            }

        }

        if (requestCode == 0002) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleSignInResult(result);


        }

    }

    private List<CalendarEvent> getEventList() {
        List<CalendarEvent> eventList = new ArrayList<>();
        Cursor cursor = db.getEvents();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString1 = "", dateString2;
        Calendar sdate = null, edate = null;


        while (cursor.moveToNext()) {

            sdate = Calendar.getInstance();
            edate = Calendar.getInstance();

            dateString1 = cursor.getString(cursor.getColumnIndex("StartDate"));
            dateString2 = cursor.getString(cursor.getColumnIndex("EndDate"));


            try {
                sdate.setTime(formatter.parse(dateString1));
                edate.setTime(formatter.parse(dateString2));

            } catch (ParseException e) {
                e.printStackTrace();
                Log.d("MyDate", "Err" + e.getMessage());
            }

            Log.d("MyDate", "" + sdate.get(Calendar.DATE));

            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String name = cursor.getString(cursor.getColumnIndex("EventName"));
            String description = cursor.getString(cursor.getColumnIndex("EventDescription"));
            String location = cursor.getString(cursor.getColumnIndex("EventLocation"));

            Random rnd = new Random();

            int color = Color.argb(225, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            BaseCalendarEvent event1 = new BaseCalendarEvent(
                    id,
                    color,
                    name,
                    description,
                    location,
                    sdate.getTimeInMillis(), edate.getTimeInMillis(), 0, "No");
            eventList.add(event1);
        }

        return eventList;
    }

    //region THREADS

    public class PeoplesAsync extends AsyncTask<String, Void, List<String>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(String... params) {

            List<String> nameList = new ArrayList<>();

            try {
                People peopleService = PeopleHelper.setUp(LandingActivity.this, params[0]);

                db.deleteContacts();

                ListConnectionsResponse response = peopleService.people().connections()
                        .list("people/me")
                        // This line's really important! Here's why:
                        // http://stackoverflow.com/questions/35604406/retrieving-information-about-a-contact-with-google-people-api-java
                        .setRequestMaskIncludeField("person.names,person.emailAddresses,person.phoneNumbers")
                        .execute();
                List<Person> connections = response.getConnections();

                for (Person person : connections) {
                    if (!person.isEmpty()) {
                        List<Name> names = person.getNames();
                        List<EmailAddress> emailAddresses = person.getEmailAddresses();
                        List<PhoneNumber> phoneNumbers = person.getPhoneNumbers();

                        if (phoneNumbers != null)
                            for (PhoneNumber phoneNumber : phoneNumbers)
                                Log.d(TAG, "phone: " + phoneNumber.getValue());

                        if (emailAddresses != null)
                            for (EmailAddress emailAddress : emailAddresses) {
                                Log.d(TAG, "email: " + emailAddress.getValue());
                                db.saveContact("", emailAddress.getValue());
                            }

                        if (names != null)
                            for (Name name : names)
                                nameList.add(name.getDisplayName());

                        progressDialog.hideDialog();
//                        Intent intent = new Intent(mcontext, LandingActivity.class);
//                        mcontext.startActivity(intent);
//                        ((Activity) mcontext).finish();

                    } else {
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
                        prefs.edit().putBoolean("isLoggedIn", false).commit();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return nameList;
        }


        @Override
        protected void onPostExecute(List<String> nameList) {
            super.onPostExecute(nameList);

        }
    }


    //endregion

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        google_api_client.connect();
        AppIndex.AppIndexApi.start(google_api_client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(google_api_client, getIndexApiAction());
        google_api_client.disconnect();
    }

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Login Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onConnected(Bundle arg0) {

    }


    @Override
    public void onConnectionSuspended(int arg0) {
    }


}