package com.itra.itraremotemedicationapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ReadPrescriptionActivity extends AppCompatActivity {

    TextView textView;

            String Prescription;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_prescription);

        textView = (TextView) findViewById(R.id.textViewPrescription);

        Bundle extras = getIntent().getExtras();
        if (extras == null)
        {
             Prescription = "Data Not Available";
        }
        else
        {
            Prescription = extras.getString("Prescription");
            if (textView != null) {
                textView.setText(Prescription);
            }
        }
    }
}
