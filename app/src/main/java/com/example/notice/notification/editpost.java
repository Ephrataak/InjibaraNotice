package com.example.notice.notification;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notice.Constants;
import com.example.notice.Homescreen;
import com.example.notice.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.notice.Constants.get_post_by_id;
import static com.example.notice.Constants.server_key;
import static com.example.notice.Constants.update_post;

public class editpost extends AppCompatActivity {


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
        private String date;

        private AlertDialog progressDialog;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_editpost);

            //getting the id that is passed to the activity from the recycler view
            id = getIntent().getStringExtra("id");

            ch = (CheckBox)findViewById(R.id.studentCheckBox);
            ch1=(CheckBox)findViewById(R.id.staffmemberCheckBox);
            ch2=(CheckBox)findViewById(R.id.publicCheckbox);
            //
            Toolbar toolbar = findViewById(R.id.editpostToolbar);
            toolbar.setTitle(R.string.edit_post);
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_icon);
            toolbar.setNavigationOnClickListener(new View.OnClickListener(){

                public void onClick(View v) {
                    final Intent intent = new Intent(v.getContext(), Homescreen.class);
                    //getParent().finish();
                    startActivity(intent);

                }});



            requestQueue = Volley.newRequestQueue(this);



            notificationmanager = NotificationManagerCompat.from(this);
            editTextTitle = findViewById(R.id.edit_text_subject);
            editTextMessage = findViewById(R.id.edit_text_message);




            //getting posts to be edited
            StringRequest requestAPIGETBYID = new StringRequest(Request.Method.GET,get_post_by_id+ "&id=" + id
                    ,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Toast.makeText(editpost.this, "getting post from DB", Toast.LENGTH_SHORT).show();
                    JSONObject obj;
                    //Toast.makeText(MainActivity.this, "Post added successfully to DB", Toast.LENGTH_SHORT).show();

                    try {
                        obj = new JSONObject(response);



                        JSONObject post = obj.getJSONObject("post");
                        String subject = post.getString("subject");
                        String message = post.getString("message");
                        String date = post.getString("date");
                        String userType = post.getString("userType");
                        String id = post.getString("id");


                        editTextTitle.setText(subject);
                        editTextMessage.setText(message);
                        //toolbar.setTitle(subject);
                        if (userType.contains("Student"))
                            ch.setChecked(true);


                        if (userType.contains("Staff Member"))
                            ch1.setChecked(true);


                        if (userType.contains("Public") )
                            ch2.setChecked(true);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.println(Log.ASSERT,"tag","Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(editpost.this, "Error in editing the post in DB", Toast.LENGTH_SHORT).show();

                    //Log.println(1,"xxx", error.getMessage());
                }
            })
            {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> hashMap=new HashMap<String, String>();
                    hashMap.put("subject",title);
                    hashMap.put("message",message);
                    hashMap.put("date",date);
                    hashMap.put("userType", userType);
                    return  hashMap;
                }
            };
            requestQueue.add(requestAPIGETBYID);
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
        public void editNoticeAndSendNotification(View v) throws JSONException {

            AlertDialog.Builder progress = new AlertDialog.Builder(this);
            progress.setMessage("Editing the post...");
            progressDialog  = progress.show();

            String dateTimeDisplay;
            Calendar calendar;
            SimpleDateFormat dateFormat;
            String date;

            title = editTextTitle.getText().toString();
            message = editTextMessage.getText().toString();
            calendar = Calendar.getInstance();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            determineUserType();


            date = dateFormat.format(calendar.getTime());

            ////////////////////////////////////////////////////////////////////////////////////////////////////////
            StringRequest requestAPI = new StringRequest(Request.Method.POST,update_post + "&id=" + id
                    ,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Toast.makeText(editpost.this, "Post edited successfully on the DB", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(editpost.this, "Error editing on the to DB", Toast.LENGTH_SHORT).show();

                    //Log.println(1,"xxx", error.getMessage());
                }
            })
            {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String,String> hashMap=new HashMap<String, String>();
                    hashMap.put("subject",title);
                    hashMap.put("message",message);
                    hashMap.put("date",date);
                    hashMap.put("userType", userType);
                    return  hashMap;
                }
            };

            requestQueue.add(requestAPI);


//

        }

       private void sendFireBaseNotification()
        {
            //Send notification with firebase
            JSONObject mainObj = new JSONObject();
            try {
                mainObj.put("to", "/topics/InjibaraUniversity");
                JSONObject notificationObject = new JSONObject();
                notificationObject.put("title", title);
                notificationObject.put("body", message);
                mainObj.put("notification", notificationObject);
                JSONObject obj = new JSONObject();
                obj.put("id",id);
                mainObj.put("data", obj);
                final Intent i = new Intent(this, Homescreen.class);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.getFirebase_Api_Url(),
                        mainObj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                //Toast.makeText(this, "Notification Sent", Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                               AlertDialog.Builder alert = new AlertDialog.Builder(editpost.this);
                                alert.setTitle("post edited");
                                alert.setMessage("The post is edited successfully!!");
                                alert.setIcon(R.drawable.ic_message);


                                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        startActivity(i);

                                    }
                                });

                                alert.show();

                                Log.println(Log.ASSERT,"tag","Success");
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.println(1,"xxx", error.getMessage());
                    }
                }) {
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

