
package com.example.notice;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notice.notification.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.notice.Constants.get_post;

public class login extends AppCompatActivity {
    private EditText username, password;
    private Button btn_login;
    private ProgressDialog progressDialog;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = Volley.newRequestQueue(this);

        username = (EditText)findViewById(R.id.editText_username);
        password = (EditText)findViewById(R.id.editText_password);


        btn_login = (Button)findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setMessage("please wait...");
                progressDialog.show();
                login(username.getText().toString(), password.getText().toString());
            }
        });



    }

    private void login(String username, String password) {
        try {
            StringRequest requestAPI = new StringRequest(Request.Method.POST, Constants.login
                    , new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject obj;
                    //Toast.makeText(MainActivity.this, "Post added successfully to DB", Toast.LENGTH_SHORT).show();

                    try {
                        obj = new JSONObject(response);

                        if (obj.getString("error").equals("false"))
                        {
                            progressDialog.dismiss();
                            final Intent intent = new Intent(login.this, Homescreen.class);  //create the instance of the activity that we want to open
                            intent.putExtra("from", "admin");
                            startActivity(intent);//open the activity
                            Toast.makeText(login.this, "Logged in", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(login.this, "Login failed", Toast.LENGTH_SHORT).show();
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.println(Log.ASSERT, "tag", "Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    progressDialog.dismiss();
                    Toast.makeText(login.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    Log.println(1, "xxx", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> hashMap=new HashMap<String, String>();
                    hashMap.put("username",username);
                    hashMap.put("password",password);
                    return  hashMap;
                }
            };
            requestQueue.add(requestAPI);

        } catch (Exception e) {
            Log.println(1, "xxx", e.getMessage());
        }

    }
}