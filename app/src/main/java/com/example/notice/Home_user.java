package com.example.notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notice.notification.Adapter;
import com.example.notice.notification.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import static com.example.notice.Constants.get_post;

public class Home_user extends AppCompatActivity implements Adapter.OnItemClickListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    Toolbar toolbar;
    public Menu menu;
    FloatingActionButton fab;
    private RequestQueue requestQueue;


    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Item> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //The following code fragment makes the user to subscribe to InjibaraUniversity topic in firebase so that
        //the user receives notification
        FirebaseMessaging.getInstance().subscribeToTopic("InjibaraUniversity")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Success";
                        if (!task.isSuccessful()) {
                            msg = "Failed";
                        }
                        Log.d("TAG", msg);
                        Toast.makeText(Home_user.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //creates a new request queue
        requestQueue = Volley.newRequestQueue(this);

        toolbar = findViewById(R.id.homeToolbar);
        toolbar.setTitle(R.string.home);

        fab = findViewById(R.id.floating_btn);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        final Intent intent = new Intent(v.getContext(), MainActivity.class);
                        //getParent().finish();
                        startActivity(intent);

                    }
                });

        /////////////////////////////////////////////////////////////////////
        //initialize the recyclerview
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new Adapter(list,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ///////////////////////////////////////////////////////////

        //gets posts and displays from the database
        getPosts();

    }

    //used to add menu on the tool bar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }


    private void getPosts() {
        try {
            StringRequest requestAPI = new StringRequest(Request.Method.GET, get_post
                    ,new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    JSONObject obj;
                    //Toast.makeText(MainActivity.this, "Post added successfully to DB", Toast.LENGTH_SHORT).show();

                    try {
                        obj = new JSONObject(response);

                        JSONArray posts = obj.getJSONArray("post");
                        for (int i = 0; i < posts.length(); i++) {
                            JSONObject post = posts.getJSONObject(i);
                            String subject = post.getString("subject");
                            //String message = post.getString("message");
                            String date = post.getString("date");
                            String userType = post.getString("userType");
                            String id = post.getString("id");
                            list.add(new Item(subject, userType, date, id));
                        }
                        mAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.println(Log.ASSERT, "tag", "Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Toast.makeText(MainActivity.this, "Error adding to DB", Toast.LENGTH_SHORT).show();
                    Log.println(1, "xxx", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

            };
            requestQueue.add(requestAPI);

        } catch (Exception e) {
            Log.println(1, "xxx", e.getMessage());
        }



    }


    @Override
    public void onItemClick(int position) {
        final Intent intent = new Intent(this, Noticeboard.class);  //create the instance of the activity that we want to open
        intent.putExtra("id",list.get(position).getText4()); //attach the data that we want to pass to the activity (id)
        startActivity(intent);//open the activity
    }

    @Override
    public void onEditButtonClick(int position) {

    }
}
