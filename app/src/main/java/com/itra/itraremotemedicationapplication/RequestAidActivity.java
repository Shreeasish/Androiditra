package com.itra.itraremotemedicationapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestAidActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_aid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAid);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestAid(view);
                Snackbar.make(view, "Prescription Requested", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void RequestAid(View view)
    {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferenceFileName), Context.MODE_PRIVATE);
        String name = sharedPref.getString(getString(R.string.phone_number),"NA");

        EditText editText = (EditText) findViewById(R.id.editTextCondition);
        String condition = editText.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("Condition",condition);
            jsonObject.put("name",name);
        }
        catch (JSONException e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("TEST","Request Queue");
        String url = "http://monitor-shreeasish.rhcloud.com/sensors/request-prescription/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Toast.makeText(getBaseContext(), "Aid Requested",Toast.LENGTH_SHORT).show();
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
