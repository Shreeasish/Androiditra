package com.itra.itraremotemedicationapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private TextView mInformationTextView;
    private TextView mPhoneNumberTextView;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhoneNumberTextView = (TextView) findViewById(R.id.phoneTextView);
        radioGroup = (RadioGroup) findViewById(R.id.userTypeRadioGroup);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {

                } else {

                }
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void launchNotification(View view)
        {
            Context context = getApplicationContext();
            Intent notificationIntent = new Intent(context, PrescriptionActivity.class);
            notificationIntent.putExtra("PATIENT_STATUS","Intent Status")
                    .putExtra("PATIENT_ID","12334566");
            PendingIntent contentIntent = PendingIntent.getActivity(context,
                    0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);


            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Resources res = context.getResources();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentIntent(contentIntent)
                    .setSmallIcon(R.drawable.ic_doctor_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_doctor_notification))
//                    .setTicker(payload)
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setContentTitle("Medical Alert");
//                    .setContentText(payload);
            Notification n = builder.build();

            n.defaults |= Notification.DEFAULT_ALL;
            nm.notify(0, n);

        }
//Stores the value in sharedPreferecnes and starts registraion.
    public void RegisterUser(View view)
        {
            radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

            String PhoneNumber =  mPhoneNumberTextView.getText().toString();
            if(!PhoneNumber.matches("") && radioButton!=null)
            {
                String userType = (String) radioButton.getText();

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferenceFileName),Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.phone_number),PhoneNumber);
                editor.putString(getString(R.string.user_type),userType);
                editor.commit();



                if (checkPlayServices()) {
                    // Start IntentService to register this application with GCM.
                    Intent intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);
                }
            }
            else
            {

            }
        }





    public void SendTestRequest(View view) {

        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject.put("Condition","120");
            jsonObject.put("name"," 9938991036");
        }
        catch (JSONException e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://monitor-shreeasish.rhcloud.com/sensors/test-notification/";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url,jsonObject,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d("SHREEASISH","Works");
                        Toast.makeText(getBaseContext(), "This worked",Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d("SHREEASISH",error.toString());
                        Toast.makeText(getBaseContext(), error.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);




    }

    public void LaunchRequestAidActivity(View view) {
        Intent intent = new Intent(this,RequestAidActivity.class);
        startActivity(intent);
    }

    public void LaunchPrescriptionActivity(View view) {
        Context context = getApplicationContext();
        Intent notificationIntent = new Intent(context, PrescriptionActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Resources res = context.getResources();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_doctor_notification)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_doctor_notification))
//                    .setTicker(payload)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle("Medical Alert");
//                    .setContentText(payload);
        Notification n = builder.build();

        n.defaults |= Notification.DEFAULT_ALL;
        nm.notify(0, n);
    }
}