package com.example.notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
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
import com.example.notice.notification.user_adapter;
import com.example.notice.notification.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import static com.example.notice.Constants.get_post;

public class Home_user extends AppCompatActivity implements user_adapter.OnItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    public Menu menu;
    FloatingActionButton fab;
    private RequestQueue requestQueue;


    private RecyclerView mRecyclerView;
    private user_adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog progressDialog;

    ArrayList<Item> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_user);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //creates a new request queue
        requestQueue = Volley.newRequestQueue(this);

        toolbar = findViewById(R.id.home_userToolbar);
        toolbar.setTitle(R.string.home);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.nav_home);



        /////////////////////////////////////////////////////////////////////
        //initialize the recyclerview
        mRecyclerView = findViewById(R.id.user_recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new user_adapter(list,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ///////////////////////////////////////////////////////////

        //gets posts and displays from the database
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading notices...");
        progressDialog.show();



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

        //The following call is to get the notices and load them
        getPosts();

        if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equals("logout")){
            Toast.makeText(Home_user.this, "logged out", Toast.LENGTH_SHORT).show();
        }

    }




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
                        mAdapter.notifyDataSetChanged(); //to refersh the recyclerview with the new list
                        progressDialog.dismiss();

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
        intent.putExtra("from", "user");
        startActivity(intent);//open the activity
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.nav_admin_login:
                this.closeContextMenu();
                final Intent intent = new Intent(this, login.class);
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}
