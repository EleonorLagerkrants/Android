package dv606.el222ja.assignment2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MyCountriesActivity extends AppCompatActivity implements CalendarProviderClient {

    SimpleCursorAdapter listAdapter;
    ListView list;
    SharedPreferences prefs;
    View view;
    private PreferenceChangeListener prefListener = null;
    String updatedCountry;
    int updatedYear;
    int updatedID;
    String sortArg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_countries);
        getMyCountriesCalendarId();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create list and attach adapter
        list = (ListView) findViewById(R.id.countryList);
        listAdapter = new SimpleCursorAdapter(this,
                R.layout.custom_list_layout,
                null,
                new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART },
                new int[]{R.id.cText, R.id.yText},
                0);

        //Calls a method that uses View Binder to change the styles of the list's rows
        myViewBinderMethod();
        list.setAdapter(listAdapter);


        registerForContextMenu(list);

        //Set up of a preference manager
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefListener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
        view = this.getWindow().getDecorView();
        setPrefBackground(view);

        String sort = prefs.getString("sort", "");
        sortArg = sort;

        getLoaderManager().initLoader(LOADER_MANAGER_ID, null, this);
    }

    // Creates contex menu for list items
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo contextMenuInfo) {
        super.onCreateContextMenu(menu, v, contextMenuInfo);
        if(v.getId()==R.id.countryList) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }
    }

    //Switch cases for when an list entry's context menu items are pressed.
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.event_edit:
                long selected = info.id;
                updatedID = (int)selected;
                startActivityForResult(new Intent(MyCountriesActivity.this, EditCountry.class), 2);
                return true;
            case R.id.event_delete:
                long sel = info.id;
                int i = (int)sel;
                deleteEvent(i);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_country, menu);
        return true;
    }

    /*
    Method that depending on what item is selected in the menu does a corresponding action.
    If a sort button is pressed, the list is updated accordingly.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_add:
                startActivityForResult(new Intent(MyCountriesActivity.this, AddCountry.class), 1);
                return true;
            case R.id.sort_country_asc:
                sortArg = "title ASC";
                updateAdapter();
                return true;
            case R.id.sort_country_desc:
                sortArg = "title DESC";
                updateAdapter();
                return true;
            case R.id.sort_year_asc:
                sortArg = "dtstart ASC";
                updateAdapter();
                return true;
            case R.id.sort_year_desc:
                sortArg =  "dtstart DESC";
                updateAdapter();
                return true;
            case R.id.set_pref:
                startActivity(new Intent(this, SimplePreferenceActivity.class));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    /*
    Gets the information from AddCountry or EditCountry and add the information/ updates to the list/.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String listCountry = data.getStringExtra("country");
            int listYear = Integer.parseInt(data.getStringExtra("year"));
            addNewEvent(listYear, listCountry);
        }
        else if(requestCode == 2 && resultCode == RESULT_OK && data != null) {
            String listCountry = data.getStringExtra("country");
            int listYear = Integer.parseInt(data.getStringExtra("year"));
            updatedCountry = listCountry;
            updatedYear = listYear;
            updateEvent(updatedID, updatedYear, updatedCountry);

        }
    }

    //Checks if a calendar is created or if there is none, creates a new calendar.
    @Override
    public long getMyCountriesCalendarId() {
        Cursor cursor = null;
        ContentResolver cr = getContentResolver();
        cursor = cr.query(CALENDARS_LIST_URI, CALENDARS_LIST_PROJECTION, CALENDARS_LIST_SELECTION, CALENDARS_LIST_SELECTION_ARGS, null);
        if (cursor.moveToNext()) {
            return cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX);
        }
        else {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_TITLE);
            values.put(CalendarContract.Calendars.OWNER_ACCOUNT, ACCOUNT_TITLE);
            values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            values.put(CalendarContract.Calendars.NAME, CALENDAR_TITLE);
            values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDAR_TITLE);
            values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
            Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
            builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, ACCOUNT_TITLE);
            builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
            builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");
            Uri uri = getContentResolver().insert(builder.build(), values);

            cursor = cr.query(CALENDARS_LIST_URI, CALENDARS_LIST_PROJECTION, CALENDARS_LIST_SELECTION, CALENDARS_LIST_SELECTION_ARGS, null);
            if (cursor.moveToFirst()) {
                return cursor.getLong(PROJ_CALENDARS_LIST_ID_INDEX);
            }
        }
        return -1;
    }

    //Adds a new event to a calendar.
    @Override
    public void addNewEvent(int year, String country) {
        long calendarID = getMyCountriesCalendarId();
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        long startMillis = CalendarUtils.getEventStart(year);
        long endMillis = CalendarUtils.getEventEnd(year);
        String timeZoneID = CalendarUtils.getTimeZoneId();
        values.put(CalendarContract.Events.TITLE, country);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZoneID);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarID);
        Uri uri = cr.insert(EVENTS_LIST_URI, values);
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);
    }

    //Updates an existing event
    @Override
    public void updateEvent(int eventId, int year, String country) {
        ContentValues values = new ContentValues();
        long startMillis = CalendarUtils.getEventStart(year);
        long endMillis = CalendarUtils.getEventEnd(year);
        values.put(CalendarContract.Events.TITLE, country);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        String[] selArgs = new String[]{Long.toString(eventId)};
        ContentResolver cr = getContentResolver();
        int updated = cr.update(EVENTS_LIST_URI, values, CalendarContract.Events._ID + " =? ", selArgs);

    }

    //Deletes an existing event
    @Override
    public void deleteEvent(int eventId) {
        String[] selArgs = new String[]{Long.toString(eventId)};
        ContentResolver cr = getContentResolver();
        int deleted = cr.delete(EVENTS_LIST_URI, CalendarContract.Events._ID + " =? ", selArgs);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_MANAGER_ID:
                return new CursorLoader(this, EVENTS_LIST_URI, EVENTS_LIST_PROJECTION,
                        null, null, sortArg);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case LOADER_MANAGER_ID:
                listAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case LOADER_MANAGER_ID:
                listAdapter.swapCursor(null);
                break;
        }
    }

    //Sets the prefered background
    public void setPrefBackground(View view) {
        String backColor = prefs.getString("backColor", "white");

        if (backColor.equals("Red")) {
            view.setBackgroundColor(Color.RED);
        } else if (backColor.equals("Yellow")) {
            view.setBackgroundColor(Color.YELLOW);
        } else if (backColor.equals("Green")) {
            view.setBackgroundColor(Color.GREEN);
        } else if (backColor.equals("White")) {
            view.setBackgroundColor(Color.WHITE);
        } else if (backColor.equals("Black")) {
            view.setBackgroundColor(Color.BLACK);
        }
    }

    // Sets the prefered text color
    public void setPrefTextColor(TextView tv) {
        String textColor = prefs.getString("textColor", "white");

        if (textColor.equals("Red")) {
            tv.setTextColor(Color.RED);
        } else if (textColor.equals("Yellow")) {
            tv.setTextColor(Color.YELLOW);
        } else if (textColor.equals("Green")) {
            tv.setTextColor(Color.GREEN);
        } else if (textColor.equals("White")) {
            tv.setTextColor(Color.WHITE);
        } else if (textColor.equals("Black")) {
            tv.setTextColor(Color.BLACK);
        }
    }

    // Sets the prefered font size
    public void setPrefFontSize(TextView tv) {
        String fontSize = prefs.getString("fontSize", "5");

        if(fontSize.equals("10"))
            tv.setTextSize(10);
        else if(fontSize.equals("15"))
            tv.setTextSize(15);
        else if(fontSize.equals("20"))
            tv.setTextSize(20);
    }

    // Edits the different columns of the list, sets prefered text color and font size
    // for both and converts the date long into an int
    public void myViewBinderMethod() {
        listAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == 1) {
                    TextView tv2 = (TextView) view;
                    setPrefTextColor(tv2);
                    setPrefFontSize(tv2);
                }
                if (columnIndex == 2) {
                    long ms = cursor.getLong(columnIndex);
                    int year = CalendarUtils.getEventYear(ms);
                    TextView tv = (TextView)view;
                    String str = String.valueOf(year);
                    tv.setText(str);
                    setPrefTextColor(tv);
                    setPrefFontSize(tv);
                    return true;
                }
                return false;
            }
        });
    }

    //Updates adapter, saves the current sorting option to preferences.
    public void updateAdapter() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("sort", sortArg);
        editor.commit();
        getLoaderManager().restartLoader(LOADER_MANAGER_ID, null, this);

        listAdapter = new SimpleCursorAdapter(this,
                R.layout.custom_list_layout,
                null,
                new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART },
                new int[]{R.id.cText, R.id.yText},
                0);
        myViewBinderMethod();
        list.setAdapter(listAdapter);
    }
    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {
            updateAdapter();
            setPrefBackground(view);

        }
    }

}
