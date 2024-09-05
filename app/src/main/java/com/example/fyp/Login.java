package com.example.fyp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Login extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE=1000;
    CheckBox check_rememberMe;
    SharedPreferences sharedPreferences;
    private Button btnLogin;
    private EditText txtPassword, txtUserName;
    private TextView txtMessage;
    String userName = "admin";
    String userPasword = "admin";
    int Counter = 5;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_NETWORK_STATE,
//                        Manifest.permission.INTERNET},
//                MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        sharedPreferences = getSharedPreferences(Utilities.MY_PREF_NAME, MODE_PRIVATE);
        initilizeComponents();
        isAlreadyExist();
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, MainActivity.class);
                Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_LONG).show();
//                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                startActivity(i);
                //findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                //callServer();
                /*if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant

                    return;
                }else{
                    callServer();
                }*/


            }
        });


    }
    private void callServer(){
        RequestParams rp = new RequestParams();
        rp.add("username",txtUserName.getText().toString());
        rp.add("password", txtPassword.getText().toString());
        TelephonyManager  tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String s="";
        try {
            s = tm.getImei();
            rp.add("imei",s);


            HttpUtils.post("api/Users/Post", rp, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    try {
                        JSONObject obj= new JSONObject(response.toString());
                        if(obj.get("Status").toString().contentEquals("ok")) {
                            rememberMe(obj.get("UserName").toString(), obj.get("Password").toString(),obj.get("EmpCode").toString());
                            Intent i = new Intent(Login.this, MainActivity.class);
                            Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_LONG).show();
//                                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            startActivity(i);
                        }
                        else{
                            Counter--;
                            String text =obj.get("Message").toString();// "Invalid Credentials.Try again please...! , you have remaining " + Counter + " attempts";
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            //txtMessage.setText("Invalid Credentials.Try again please...! , you have remainig" + Counter + " attempts");
                            if (Counter <= 0) {
                                btnLogin.setEnabled(false);
                            }


                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {
                    // Pull out the first event on the public timeline
                    try {
                        JSONObject obj= timeline.getJSONObject(0);
                        if(obj.get("Status").toString().contentEquals("ok")) {
                            rememberMe(obj.get("UserName").toString(), obj.get("Password").toString(),obj.get("EmpCode").toString());
                            Intent i = new Intent(Login.this, MainActivity.class);
                            Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_LONG).show();

                            startActivity(i);
                        }
                        else{
                            Counter--;
                            String text = "Invalid Credentials.Try again please...! , you have remaining " + Counter + " attempts";
                            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                            //txtMessage.setText("Invalid Credentials.Try again please...! , you have remainig" + Counter + " attempts");
                            if (Counter <= 0) {
                                btnLogin.setEnabled(false);
                            }


                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(

                    ),"Login Successful",Toast.LENGTH_SHORT).show();

                }




                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(getApplicationContext(),"Error Connecting to Server!" + responseString,Toast.LENGTH_SHORT).show();
                    Log.d("error" , responseString);
                }

                //   @Override
                //   public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                //       super.onFailure(statusCode, headers, throwable, errorResponse);
                //   }

                //   @Override
                //   public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //       super.onFailure(statusCode, headers, throwable, errorResponse);

                // }
            });
            // Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        }catch(SecurityException ex)
        {
            ex.printStackTrace();
        }


    }
    private void validate(String Name, String Password) {
        if (Name.contentEquals((userName)) && (Password.contentEquals(userPasword))) {
            rememberMe(userName, Password,"");
            Intent i = new Intent(Login.this, MainActivity.class);
            Toast.makeText(this, "Successfully Login", Toast.LENGTH_LONG).show();
            startActivity(i);
        } else {
            Counter--;
            String text = "Invalid Credentials.Try again please...! , you have remaining " + Counter + " attempts";
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            //txtMessage.setText("Invalid Credentials.Try again please...! , you have remainig" + Counter + " attempts");
            if (Counter <= 0) {
                btnLogin.setEnabled(false);
            }
        }
    }


    private void rememberMe(String userName, String password,String empcode) {
        //if (check_rememberMe.isChecked())
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Utilities.KEY_USER_NAME, userName);
            editor.putString(Utilities.KEY_PASSWORD, password);
            editor.putString(Utilities.KEY_EMPCODE, empcode);

            editor.commit();
        }

    }

    private void isAlreadyExist() {
        String UserName = sharedPreferences.getString(Utilities.KEY_USER_NAME, Utilities.INVALID_DATA);
        String Password = sharedPreferences.getString(Utilities.KEY_PASSWORD, Utilities.INVALID_DATA);
        if (!UserName.contentEquals(Utilities.INVALID_DATA) | !Password.contentEquals(Utilities.INVALID_DATA)) {
            txtUserName.setText(UserName);
            txtPassword.setText(Password);
            check_rememberMe.setChecked(true);
        }
    }

    private void initilizeComponents() {
        btnLogin = findViewById(R.id.btnLogin);
        txtUserName = (EditText) findViewById(R.id.txtUserName);
        txtPassword = findViewById(R.id.txtPassword);
        txtMessage = findViewById(R.id.textView2);
        check_rememberMe = (CheckBox) findViewById(R.id.ch_rememberMe);
    }

   /* @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  //  Toast.makeText(this, "Permission Guranted", Toast.LENGTH_LONG).show();
callServer();
                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }*/

}
