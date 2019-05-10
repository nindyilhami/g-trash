package com.mytrash.mytrash.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mytrash.mytrash.R;
import com.mytrash.mytrash.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

public class StickerActivity extends AppCompatActivity {

    ImageButton btnProfile,btnMaps;
    TextView txtSticker, txtName;

    String name,sticker, user_id;
    SharedPreferences sharedpreferences;
    boolean success;
    Boolean session = false;

    private String url = "http://192.168.43.227/suroboyobus/api/penumpang/profile/";
    String tag_json_obj = "json_obj_req";

    public final static String TAG_ID = "user_id";
    public final static String TAG_STICKER = "sticker_penumpang";
    public static final String TAG_NAME = "nama_penumpang";

    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    private static final String TAG_MESSAGE = "message";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker);

        //button menu
        btnProfile = (ImageButton) findViewById(R.id.btn_profile);
        btnMaps = (ImageButton) findViewById(R.id.btn_maps);

        txtSticker = (TextView) findViewById(R.id.txt_stickerSticker);
        txtName = (TextView) findViewById(R.id.txt_stickerName);

        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        user_id = sharedpreferences.getString(TAG_ID, null);
        sticker = sharedpreferences.getString(TAG_STICKER, null);
        name = sharedpreferences.getString(TAG_NAME,null);

        getData();

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StickerActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StickerActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    // Fungsi get JSON data
    private void getData() {
        StringRequest strReq = new StringRequest(Request.Method.GET,url+"?user_id="+user_id, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("Response: ", response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getBoolean("success");

                    if (success) {

                        String sticker = jObj.getJSONObject("data").getJSONObject("penumpang").getString("sticker_penumpang");
                        String name = jObj.getJSONObject("data").getJSONObject("penumpang").getString("nama_penumpang");

                        Log.e("Successfully", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txtSticker.setText(sticker);
                        txtName.setText(name);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String err = (error.getMessage()==null)?"Profile failed":error.getMessage();
                Log.e("Error: ", err);
                Toast.makeText(StickerActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}
