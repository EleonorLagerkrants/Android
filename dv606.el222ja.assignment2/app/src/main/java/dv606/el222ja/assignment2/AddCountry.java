package dv606.el222ja.assignment2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCountry extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText givenCountry = (EditText) findViewById(R.id.addCountry);
                String country = givenCountry.getText().toString();
                EditText givenYear = (EditText) findViewById(R.id.addYear);
                String year = givenYear.getText().toString();

                if (country.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter a country", Toast.LENGTH_SHORT).show();
                    return;
                } else if (year.matches("")) {
                    Toast.makeText(getApplicationContext(), "You did not enter a year", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        int num = Integer.parseInt(year);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "The year needs to be a number", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent result = new Intent();
                    result.putExtra("country", country);
                    result.putExtra("year", year);
                    setResult(AddCountry.RESULT_OK, result);
                    finish();
                }
            }
        });

    }

}
