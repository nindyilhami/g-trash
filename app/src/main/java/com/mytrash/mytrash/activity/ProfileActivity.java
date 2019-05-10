package com.mytrash.mytrash.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class ProfileActivity extends AppCompatActivity {

    Button btnLogout;
    TextView txtName, txtTelephone, txtEmail, txtSticker, txtCode;
    ImageButton btnMaps, btnSticker;
    String name,telephone,email,user_id,sticker;
    SharedPreferences sharedpreferences;
    boolean success;
    Boolean session = false;

    private String url = "http://192.168.43.227/suroboyobus/api/penumpang/profile/";
    String tag_json_obj = "json_obj_req";

    public final static String TAG_ID = "user_id";
    public final static String TAG_STICKER = "sticker_penumpang";
    public static final String TAG_NAME = "nama_helper";
    public static final String TAG_TELEPHONE = "telp_helper";
    public static final String TAG_EMAIL = "email";

    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    private static final String TAG_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtSticker = (TextView) findViewById(R.id.txt_profileSticker);
        txtCode = (TextView) findViewById(R.id.txt_profileCode);
        txtName = (TextView) findViewById(R.id.txt_profileName);
        txtTelephone = (TextView) findViewById(R.id.txt_profileTelephone);
        txtEmail = (TextView) findViewById(R.id.txt_profileEmail);

        btnMaps = (ImageButton) findViewById(R.id.btn_maps);
        btnSticker = (ImageButton) findViewById(R.id.btn_sticker);
        btnLogout = (Button) findViewById(R.id.btn_logout);

        btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ProfileActivity.this, MapsActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent (ProfileActivity.this, StickerActivity.class);
                startActivity(i);
                finish();
            }
        });

        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        user_id = sharedpreferences.getString(TAG_ID, null);
        sticker = sharedpreferences.getString(TAG_STICKER, null);
        name = sharedpreferences.getString(TAG_NAME,null);
        telephone = sharedpreferences.getString(TAG_TELEPHONE,null);
        email = sharedpreferences.getString(TAG_EMAIL, null);

        getData();

        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // update login session ke FALSE dan mengosongkan nilai id dan username
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(LoginActivity.session_status, false);
                editor.putString(TAG_STICKER,null);
                editor.putString(TAG_NAME, null);
                editor.putString(TAG_TELEPHONE, null);
                editor.putString(TAG_EMAIL, null);
                editor.commit();

                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                finish();
                startActivity(intent);
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
                        String user_id = jObj.getJSONObject("data").getJSONObject("penumpang").getString("user_id_penumpang");
                        String sticker = jObj.getJSONObject("data").getJSONObject("penumpang").getString("sticker_penumpang");
                        String name = jObj.getJSONObject("data").getJSONObject("penumpang").getString("nama_penumpang");
                        String telephone = jObj.getJSONObject("data").getJSONObject("penumpang").getString("telp_penumpang");
                        String email = jObj.getJSONObject("data").getJSONObject("user").getString("email");

                        Log.e("Successfully", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        txtSticker.setText(sticker);
                        txtCode.setText(user_id);
                        txtName.setText(name);
                        txtTelephone.setText(telephone);
                        txtEmail.setText(email);
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

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
                Toast.makeText(ProfileActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }
}

