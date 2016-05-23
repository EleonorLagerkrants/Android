package dv606.el222ja.assignment1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class BMIActivity extends AppCompatActivity {

    private EditText givenHeight;
    private EditText givenWeight;
    private TextView BMIText;
    private double weight = 0;
    private double height = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button computeButton = (Button)findViewById(R.id.ComputeB);
        computeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                givenWeight = (EditText)findViewById(R.id.weightText);
                givenHeight = (EditText)findViewById(R.id.heightText);
                weight = Double.parseDouble(givenWeight.getText().toString());
                height = Double.parseDouble(givenHeight.getText().toString());
                double BMI = (weight / (height*height));
                BMIText = (TextView)findViewById(R.id.BMIText);
                String BMIString = String.format("%.2f",BMI);
                BMIText.setText(String.format(BMIString));
            }
        });

        Button resetButton = (Button)findViewById(R.id.ResetB);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                givenWeight.setText("");
                givenHeight.setText("");
                BMIText.setText("");
            }
        });

    }


}
