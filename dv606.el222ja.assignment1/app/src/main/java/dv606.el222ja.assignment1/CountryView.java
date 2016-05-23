package dv606.el222ja.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class CountryView extends AppCompatActivity {

    public static String countryString;
    ArrayList<String> countryList=new ArrayList<String>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView list = (ListView)findViewById(R.id.countryList);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, countryList);
        list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.countrymenu, menu);
        return true;
    }

    /*
    Method that starts the AddCountry class when the user press "Add"
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add) {
            startActivityForResult(new Intent(CountryView.this, AddCountry.class), 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    Gets the information from AddCountry and add the information to the list.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String listCountry = data.getStringExtra("country");
            String listYear = data.getStringExtra("year");
            countryString = listYear +": " + listCountry ;
            adapter.add(countryString);
        }
    }
}
