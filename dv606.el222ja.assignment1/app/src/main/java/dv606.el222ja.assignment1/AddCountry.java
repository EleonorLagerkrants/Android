package dv606.el222ja.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddCountry extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_country);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Button addButton = (Button)findViewById(R.id.addbBtton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText givenCountry = (EditText)findViewById(R.id.editCountry);
                String country = givenCountry.getText().toString();
                EditText givenYear = (EditText)findViewById(R.id.editYear);
                String year = givenYear.getText().toString();

                Intent result = new Intent();
                result.putExtra("country", country);
                result.putExtra("year", year);
                setResult(AddCountry.RESULT_OK, result);
                finish();
            }
        });

    }

}
