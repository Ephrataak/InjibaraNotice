package com.example.notice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notice.notification.Adapter;
import com.example.notice.notification.Item;
import com.example.notice.notification.editpost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.notice.Constants.delete_post;
import static com.example.notice.Constants.get_post;
import static com.example.notice.Constants.update_post;

public class Homescreen extends AppCompatActivity implements Adapter.OnItemClickListener,NavigationView.OnNavigationItemSelectedListener {

    private RequestQueue requestQueue;

    public String subject;
    String userType = "";
    public String msg;

    private String title;
    private String message;

    private String id;
    private String date;


    Toolbar toolbar;
    public Menu menu;
    FloatingActionButton fab;



    private RecyclerView mRecyclerView;
    private Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    ArrayList<Item> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
                        Toast.makeText(Homescreen.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //creates a new request queue
        requestQueue = Volley.newRequestQueue(this);
        ///////////////////////////////////////////////////////////////


/* Initialize interface elements*****************************************/
        toolbar = findViewById(R.id.homeToolbar);
        toolbar.setTitle(R.string.home);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setCheckedItem(R.id.nav_home);

        /////////////////////////////////////////////////////////////////////
        //initialize the recyclerview
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new Adapter(list,this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        ///////////////////////////////////////////////////////////

        fab = findViewById(R.id.floating_btn);
        fab.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        final Intent intent = new Intent(v.getContext(), MainActivity.class); //MainActivity=add post
                        intent.putExtra("from", "admin");
                        //getParent().finish();
                        startActivity(intent);

                    }
                });
/***********************************************************************************************/





        //gets posts from the database and display them
        getPosts();

    }

    //used to add menu on the tool bar
  //  @Override
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
                    //Toast.makeText(MainActivity.this,const "post return successful", Toast.LENGTH_SHORT).show();

                    try {
                        obj = new JSONObject(response);//converts string response to Json format

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
                        mAdapter.notifyDataSetChanged(); //informs the adapter data is loaded to 'list' var

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.println(Log.ASSERT, "tag", "Success");
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Homescreen.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    Log.println(1, "xxx", error.toString());
                }
            })//The construction of the request is not yet finished. The following lines attach extra information about
                //the request such as content type
            {
                @Override
                public String getBodyContentType() {
                    return "application/x-www-form-urlencoded; charset=UTF-8";
                }

            }; //finish the construction of the request string (designated by the variable requestAPI)

            //We send the request API to volley by the following line
            requestQueue.add(requestAPI);

        } catch (Exception e) {
            Log.println(1, "xxx", e.getMessage());
        }



}


    @Override
    public void onItemClick(int position) {
        final Intent intent = new Intent(this, Noticeboard.class);  //create the instance of the activity that we want to open
        intent.putExtra("id",list.get(position).getText4()); //attach the data that we want to pass to the activity (id)
        intent.putExtra("from", "admin");
        startActivity(intent);//open the activity
    }

    @Override
    public void onEditButtonClick(int position) {
        final Intent intent = new Intent(this, editpost.class);  //create the instance of the activity that we want to open
        intent.putExtra("id",list.get(position).getText4()); //attach the data that we want to pass to the activity (id)
        startActivity(intent);//open the activity
    }

    @Override
    public void onDeleteButtonClick(int position) {
        //TODO 1. Display dialog box, 2. If yes is clicked, call deleteApi, 3. If successfully deleted, display message 4.Refresh home screen
        AlertDialog.Builder builder = new AlertDialog.Builder(Homescreen.this);
        builder.setTitle("DELETE NOTICE?")
                .setMessage("Are you sure you want to delete this notice?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringRequest requestAPI = new StringRequest(Request.Method.POST,delete_post + "&id=" + list.get(position).getText4()
                                ,new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                JSONObject obj;
                                Toast.makeText(Homescreen.this, "Post is removed successfully from DB", Toast.LENGTH_SHORT).show();

                                try {
                                    obj = new JSONObject(response);//converts string response to Json format
                                    list.clear();

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
                                    mAdapter.notifyDataSetChanged(); //informs the adapter data is loaded to 'list' var



                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Log.println(Log.ASSERT,"tag","Success");
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(Homescreen.this, "Error deleting on the DB", Toast.LENGTH_SHORT).show();

                                //Log.println(1,"xxx", error.getMessage());
                            }
                        })
                        {
                            @Override
                            public String getBodyContentType() {
                                return "application/x-www-form-urlencoded; charset=UTF-8";
                            }


                        };

                        requestQueue.add(requestAPI);


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_admin_logout:
                this.closeContextMenu();
                final Intent intent = new Intent(this, Home_user.class);
                intent.putExtra("from", "logout");
                startActivity(intent);
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }
}
