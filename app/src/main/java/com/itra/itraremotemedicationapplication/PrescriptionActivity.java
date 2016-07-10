package com.itra.itraremotemedicationapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;



//Doctor gives the prescription here AKA write prescriptionActivity
public class PrescriptionActivity extends AppCompatActivity
    {

        String condition;
        private String name;


        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_prescription);
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);




                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                if (fab != null) {
                    fab.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View view)
                                {
                                    SendPrescription(view);
                                    Snackbar.make(view, "Prescription Sent", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                        });
                }
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//Get Data from Intent
                TextView textView = (TextView) findViewById(R.id.textViewSymptoms);

                if (savedInstanceState == null)
                {
                    Bundle extras = getIntent().getExtras();
                    if (extras == null)
                    {
                        condition = null;
                        name= null;
                    }
                    else
                    {
                        name= extras.getString("PatientName");
                        condition = extras.getString("Condition");
                        if (textView != null) {

                            textView.setText(getString(R.string.StatusPrefix) + condition + "\nPatient Number: " + name);
                        }
                    }
                }
            }
        //End


        public void SendPrescription(View view)
            {

                EditText editText = (EditText) findViewById(R.id.textPrescription);
                String pres = editText != null ? editText.getText().toString() : null;


                    JSONObject jsonObject = new JSONObject();
                    try
                    {
                        jsonObject.put("Prescription",pres);
                        jsonObject.put("PatientNumber",name);
                        jsonObject.put("Condition",condition);
                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url = "http://monitor-shreeasish.rhcloud.com/sensors/send-prescription/";
                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                            new Response.Listener<JSONObject>()
                            {
                                @Override
                                public void onResponse(JSONObject response)
                                {
                                    Toast.makeText(getBaseContext(), "Prescription Sent",Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse(VolleyError error)
                                {
                                    Toast.makeText(getBaseContext(), "Response Error: Postman",Toast.LENGTH_SHORT).show();
                                }
                            });
                queue.add(request);
            }
    }
