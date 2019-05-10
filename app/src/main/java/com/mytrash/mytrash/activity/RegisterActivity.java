package com.mytrash.mytrash.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    Button btnLogin, btnRegister;
    ImageButton btnBack;
    TextView txtName, txtEmail, txtTelephone, txtPassword;
    Intent intent;

    boolean success;
    ConnectivityManager conMgr;
    SharedPreferences sharedpreferences;
    Boolean session = false;
    String user_id, name, email, telephone, password;

    private String url = "http://192.168.43.227/suroboyobus/api/user/register";

    private static final String TAG = RegisterActivity.class.getSimpleName();
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";

    public final static String TAG_ID = "user_id";
    public final static String TAG_NAME = "nama_penumpang";
    public final static String TAG_TELEPHONE = "telp_penumpang";
    public final static String TAG_EMAIL = "email";
    public final static String TAG_PASSWORD = "password";

    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (conMgr.getActiveNetworkInfo() != null
                    && conMgr.getActiveNetworkInfo().isAvailable()
                    && conMgr.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnBack = (ImageButton) findViewById(R.id.btn_back);

        txtName = (TextView) findViewById(R.id.txt_registerName);
        txtEmail = (TextView) findViewById(R.id.txt_registerEmail);
        txtTelephone = (TextView) findViewById(R.id.txt_registerTelephone);
        txtPassword = (TextView) findViewById(R.id.txt_registerPassword);

        // Cek session register jika TRUE maka langsung buka MainActivity
        sharedpreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedpreferences.getBoolean(session_status, false);
        user_id = sharedpreferences.getString(TAG_ID, null);
        name = sharedpreferences.getString(TAG_NAME,null);
        email = sharedpreferences.getString(TAG_EMAIL, null);
        telephone = sharedpreferences.getString(TAG_TELEPHONE,null);
        password = sharedpreferences.getString(TAG_PASSWORD,null);

        if (session) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.putExtra(TAG_NAME, name);
            intent.putExtra(TAG_EMAIL, email);
            intent.putExtra(TAG_TELEPHONE, telephone);
            intent.putExtra(TAG_PASSWORD, password);
            finish();
            startActivity(intent);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                 //TODO Auto-generated method stub
                intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //TODO Auto-generated method stub

                String name = txtName.getText().toString();
                String email = txtEmail.getText().toString();
                String telephone = txtTelephone.getText().toString();
                String password = txtPassword.getText().toString();
                if (name.trim().length() > 0 && email.trim().length() > 0 && telephone.trim().length() > 0 && password.trim().length() > 0) {
                    if (conMgr.getActiveNetworkInfo() != null
                            && conMgr.getActiveNetworkInfo().isAvailable()
                            && conMgr.getActiveNetworkInfo().isConnected()) {
                            checkRegister(name,email,telephone,password);
                    } else {
                        Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkRegister(final String name, final String email, final String telephone, final String password) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Register ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getBoolean("success");

                    // Check for error node in json
                    if (success) {

                        String name = jObj.getJSONObject("data").getString("nama_penumpang");
                        String email = jObj.getJSONObject("data").getString("email");
                        String telephone = jObj.getJSONObject("data").getString("telp_penumpang");
                        String password = jObj.getJSONObject("data").getString("password");

                        Log.e("Successfully Register!", jObj.toString());

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(session_status, true);
                        editor.putString(TAG_NAME, name);
                        editor.putString(TAG_EMAIL, email);
                        editor.putString(TAG_TELEPHONE, telephone);
                        editor.putString(TAG_PASSWORD, password);
                        editor.commit();

                        //membuka login activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra(TAG_NAME, name);
                        intent.putExtra(TAG_EMAIL, email);
                        intent.putExtra(TAG_TELEPHONE, telephone);
                        intent.putExtra(TAG_PASSWORD, password);
                        finish();
                        startActivity(intent);
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
                Log.e(TAG, "Register Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nama_penumpang", name);
                params.put("email", email);
                params.put("telp_penumpang", telephone);
                params.put("password", password);
                return params;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(RegisterActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
}
