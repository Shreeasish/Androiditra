package com.itra.itraremotemedicationapplication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.System;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegistrationIntentService extends IntentService
    {

        private static final String TAG = "RegIntentService";
        private static final String[] TOPICS = {"global"};

        public RegistrationIntentService()
            {
                super(TAG);
            }

        @Override
        protected void onHandleIntent(Intent intent)
            {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                try
                {
                    // [START register_for_gcm]
                    // Initially this call goes out to the network to retrieve the token, subsequent calls
                    // are local.
                    // [START get_token]
                    InstanceID instanceID = InstanceID.getInstance(this);
                    String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    // [END get_token]
                    Log.i(TAG, "GCM Registration Token: " + token);

                    // TODO: Implement this method to send any registration to your app's servers.
                    sendRegistrationToServer(token);

                    // Subscribe to topic channels
                    subscribeTopics(token);

                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                    // [END register_for_gcm]
                }
                catch (Exception e)
                {
                    Log.d(TAG, "Failed to complete token refresh", e);
                    // If an exception happens while fetching the new token or updating our registration data
                    // on a third-party server, this ensures that we'll attempt the update at a later time.
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
                }
                // Notify UI that registration has completed, so the progress indicator can be hidden.
                Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
                LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
            }

        /**
         * Persist registration to third-party servers.
         * <p/>
         * Modify this method to associate the user's GCM registration token with any server-side account
         * maintained by your application.
         *
         * @param token The new token.
         */
        private void sendRegistrationToServer(String token)
            {

                String androidID = System.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preferenceFileName),Context.MODE_PRIVATE);
                String name = sharedPref.getString(getString(R.string.phone_number),"NA");
                String userType = sharedPref.getString(getString(R.string.user_type),"Patient");

                JSONObject jsonObject = new JSONObject();
                try
                {
                    jsonObject.put("registration_id",token);
                    jsonObject.put("device_id",androidID);
                    jsonObject.put("name",name);
                    jsonObject.put("UserType",userType);
                    Log.d(TAG, "sendRegistrationToServer: " + jsonObject.toString());
                }
                catch (JSONException e)
                {
                    Toast.makeText(RegistrationIntentService.this, e.toString(), Toast.LENGTH_SHORT).show();
                }



                RequestQueue queue = Volley.newRequestQueue(this);
                Log.d("TEST","Request Queue");
                String url = "http://monitor-shreeasish.rhcloud.com/sensors/register-device-android/";
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
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

                // Add custom implementation, as needed.
            }

        /**
         * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
         *
         * @param token GCM token
         * @throws IOException if unable to reach the GCM PubSub service
         */
        // [START subscribe_topics]
        private void subscribeTopics(String token) throws IOException
            {
                GcmPubSub pubSub = GcmPubSub.getInstance(this);
                for (String topic : TOPICS)
                {
                    pubSub.subscribe(token, "/topics/" + topic, null);
                }
            }
        // [END subscribe_topics]

    }