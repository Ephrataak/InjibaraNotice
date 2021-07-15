package com.example.notice;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notice.notification.Item;
import com.example.notice.notification.dialog_reset;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.notice.Constants.create_post;
import static com.example.notice.Constants.server_key;

public class MainActivity extends AppCompatActivity {
    String userType = "";
    CheckBox ch, ch1, ch2;


    private NotificationManagerCompat notificationmanager;
    public dialog_reset dialogReset;
    public EditText editTextTitle;
    public EditText editTextMessage;
    private RequestQueue requestQueue;

    public String subject;
    public String msg;

    private String title;
    private String message;

    private String id;

    private String from;

    private ProgressDialog progressDialog;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /************************************************************************/
        ch = (CheckBox)findViewById(R.id.studentCheckBox);
        ch1 = (CheckBox)findViewById(R.id.staffmemberCheckBox);
        ch2 = (CheckBox)findViewById(R.id.publicCheckbox);

        editTextTitle = findViewById(R.id.edit_text_subject);
        editTextMessage = findViewById(R.id.edit_text_message);

        from = getIntent().getStringExtra("from");

        Toolbar toolbar = findViewById(R.id.addPostToolbar);
        toolbar.setTitle(R.string.add_post);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                final Intent intent;
                if (from.equals("admin"))
                    intent = new Intent(v.getContext(),Homescreen.class);
                else
                    intent = new Intent(v.getContext(),Home_user.class);
                //getParent().finish();
                startActivity(intent);

            }});
        /**************************************************************************/

        requestQueue = Volley.newRequestQueue(this);

        notificationmanager = NotificationManagerCompat.from(this);

    }



    private void determineUserType(){
        if (ch.isChecked()) {
            userType = userType + "Student";
            if (ch1.isChecked()){
                userType = userType + ", Staff Member";
            }
            if (ch2.isChecked()){
                userType = userType + ", Public";
            }
        }
        else if (ch1.isChecked()){
            userType = userType + "Staff Member";
            if (ch2.isChecked()){
                userType = userType + ", Public";
            }
        }
        else if (ch2.isChecked()){
            userType = userType + "Public";
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void saveNoticeAndSendNotification(View v) throws JSONException {

//        AlertDialog.Builder progress = new AlertDialog.Builder(MainActivity.this);
//        progress.setMessage("Adding the post...");
//        progressDialog  = progress.show();
        progressDialog = new ProgressDialog(v.getContext());
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        //Gets date from the system
        String dateTimeDisplay;
        Calendar calendar;
        SimpleDateFormat dateFormat;
        String date;
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(calendar.getTime());

        //Collects post information from the interface
        title = editTextTitle.getText().toString();
        message = editTextMessage.getText().toString();
        determineUserType();





        StringRequest requestAPI = new StringRequest(Request.Method.POST,create_post
                ,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(MainActivity.this, "Post added successfully to DB", Toast.LENGTH_SHORT).show();
                        JSONObject obj;
                        //Toast.makeText(MainActivity.this, "Post added successfully to DB", Toast.LENGTH_SHORT).show();

                        try {
                            obj = new JSONObject(response);

                            JSONArray posts = obj.getJSONArray("post");
                            JSONObject post = posts.getJSONObject(0);
                            id = post.getString("id");
                            Log.println(Log.ASSERT,"tag",id);
                            sendFireBaseNotification();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.println(Log.ASSERT,"tag","Success");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Error adding to DB", Toast.LENGTH_SHORT).show();


                        //Log.println(1,"xxx", error.getMessage());
                    }
        })
        //extra information to the request
        {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String>hashMap=new HashMap<String, String>();
                hashMap.put("subject",title);
                hashMap.put("message",message);
                hashMap.put("date",date);
                hashMap.put("userType", userType);
                return  hashMap;
            }
        };


        requestQueue.add(requestAPI);




    }

    private void sendFireBaseNotification() {
        //Send notification with firebase
        JSONObject mainObj = new JSONObject(); //creates a new JSON object called mainObj..{}
        try {
            mainObj.put("to", "/topics/InjibaraUniversity");
            JSONObject notificationObject = new JSONObject(); //creates a new JSON object called notificationObject..{}
            notificationObject.put("title", title);
            notificationObject.put("body", message);
            mainObj.put("notification", notificationObject);//embed notificationObject to mainObj
            JSONObject obj = new JSONObject();
            obj.put("id",id);
            mainObj.put("data", obj);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.getFirebase_Api_Url(),
                    mainObj,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            Toast.makeText(MainActivity.this, "Notification Sent", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("post added");
                            alert.setMessage("The post is added successfully!!");
                            alert.setIcon(R.drawable.ic_message);

                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    editTextTitle.setText("");
                                    editTextMessage.setText("");
                                    ch.setChecked(true); //set Student checkbox checked by default
                                    ch1.setChecked(false);
                                    ch2.setChecked(false);
                                    userType = "";
                                    editTextTitle.requestFocus();


                                }
                            });

                            alert.show();

                            Log.println(Log.ASSERT,"tag","Success");
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    //Log.println(1,"xxx", error.getMessage());
                }
            })
            //extra information
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type", "application/json");
                    header.put("Authorization", server_key);
                    return header;
                }

            };

            requestQueue.add(request);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}