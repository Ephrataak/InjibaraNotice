package com.example.notice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notice.notification.Adapter;
import com.example.notice.notification.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.notice.Constants.get_post;
import static com.example.notice.Constants.get_post_by_id;

public class Noticeboard<requestAPI> extends AppCompatActivity {
    private RequestQueue requestQueue;

    private TextView txtPostDate;
    private TextView txtSubject;
    private TextView txtUserType;
    private TextView txtMessage;

    private   Toolbar toolbar;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noticeboard);

        requestQueue = Volley.newRequestQueue(this);

        toolbar = findViewById(R.id.noticeToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_icon);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                final Intent intent = new Intent(v.getContext(),Homescreen.class);
                //getParent().finish();
                startActivity(intent);

            }});

        txtPostDate = findViewById(R.id.post_time);
        txtSubject = findViewById(R.id.subject);
        txtUserType = findViewById(R.id.userType);
        txtMessage = findViewById(R.id.txtmsg);

        id = getIntent().getStringExtra("id");

        getPostByID();

        //The following lines used to get title, message, and date from the values passed from MyFirebaseMessaginService
        //We should remove it
        String title = getIntent().getStringExtra("title");
        String message = getIntent().getStringExtra("message");

        Long time = getIntent().getLongExtra("time", 0);
        String longV = String.valueOf(time);
        long millisecond = Long.parseLong(longV);

        // or you already have long value of date, use this instead of milliseconds variable.

        String dateString = DateFormat.format("yyyy/MM/dd", new Date(millisecond)).toString();

        txtPostDate.setText("Posted on: " + dateString);
        txtMessage.setText(message);
        ////////////////////////////////////////////////////////////////////////////////////

    }


    private void getPostByID() {
        StringRequest requestAPI = new StringRequest(Request.Method.GET, get_post_by_id + "&id=" + id
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject obj;
                try {
                    obj = new JSONObject(response);

                    JSONObject post = obj.getJSONObject("post");
                    String subject = post.getString("subject");
                    String message = post.getString("message");
                    String date = post.getString("date");
                    String userType = post.getString("userType");
                    String id = post.getString("id");

                    txtPostDate.setText("Posted on: " + date);
                    txtSubject.setText(subject);
                    txtUserType.setText(userType);
                    txtMessage.setText(message);
                    toolbar.setTitle(subject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Log.println(Log.ASSERT, "tag", "Success");
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(MainActivity.this, "Error adding to DB", Toast.LENGTH_SHORT).show();

                Log.println(1, "xxx", error.getMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }


        };
        requestQueue.add(requestAPI);

    }
}