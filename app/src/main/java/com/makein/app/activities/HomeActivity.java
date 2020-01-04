package com.makein.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.makein.app.Models.MyResponse;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.controler.BitmapTransform;
import com.makein.app.fragments.AddCategoryFragment;
import com.makein.app.fragments.AddSubCategoryFragment;
import com.makein.app.fragments.ChangePwsFragment;
import com.makein.app.fragments.HomeFragment;
import com.makein.app.R;
import com.makein.app.controler.Controller;
import com.makein.app.controler.Sessions;
import com.makein.app.fragments.RequestsFragment;
import com.makein.app.fragments.SubCategoryFragment;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Context context;
    String From = "";
    public static Class fragmentClass;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        context = HomeActivity.this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);

        TextView leftMenuName = (TextView) hView.findViewById(R.id.leftMenuName);
        TextView leftMenuDesc = (TextView) hView.findViewById(R.id.leftMenuDesc);
        ImageView imageView = (ImageView) hView.findViewById(R.id.imageView);

        leftMenuName.setText(Sessions.getUserObject(context, Controller.name));
        leftMenuDesc.setText(Sessions.getUserObject(context, Controller.emailID));


        MyResponse myResponse = (MyResponse) Sessions.getUserObj(context, Controller.Categories, MyResponse.class);
        if (myResponse.data.get(0).name == null) {
            String userId = Sessions.getUserObject(context, Controller.userID);
            GetAllProdSubs(userId);
        }
        try {
            int size = (int) Math.ceil(Math.sqrt(800 * 600));
            // Loads given image
            Picasso.get()
                    .load(Sessions.getUserObject(context, Controller.profile_img))
                    .transform(new BitmapTransform(800, 600))
                    .resize(size, size)
                    .centerInside()
                    // .noPlaceholder()
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.load_failed)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
        callHomeFrag();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Sessions.removeUserKey(context, Controller.Categories);
            callHomeFrag();
            return true;
        } else if (id == R.id.action_notify) {
//            fragmentClass = SubCategoryFragment.class;
//            SetFrag(fragmentClass);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void callHomeFrag() {

        toolbar.setTitle("Home");
//        fragmentClass = HomeFragment.class;
        fragmentClass = RequestsFragment.class;
        SetFrag(fragmentClass);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_home: {
                callHomeFrag();
            }
            break;
            case R.id.nav_category: {
                if (fragmentClass != AddCategoryFragment.class) {
                    toolbar.setTitle("Add Category");
                    fragmentClass = AddCategoryFragment.class;
                    SetFrag(fragmentClass);
                }
            }
            break;
            case R.id.nav_subCategory: {
                toolbar.setTitle("Add Sub Category");
                if (fragmentClass != AddSubCategoryFragment.class) {
                    fragmentClass = AddSubCategoryFragment.class;
                    SetFrag(fragmentClass);
                }
            }
            break;
            case R.id.nav_requests: {
                toolbar.setTitle("Categories");
                if (fragmentClass != HomeFragment.class) {
                    fragmentClass = HomeFragment.class;
                    SetFrag(fragmentClass);
                }
            }
            break;
            case R.id.nav_chng_pass: {
                toolbar.setTitle("Profile");
                if (fragmentClass != ChangePwsFragment.class) {
                    fragmentClass = ChangePwsFragment.class;
                    SetFrag(fragmentClass);
                }
//                Intent intent = new Intent(HomeActivity.this, ChangePassword.class);
//                startActivity(intent);

            }
            break;
            case R.id.nav_logout:
                new AlertDialog.Builder(this)
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.logout_confirm)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onLogOut();
                            }

                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .show();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentClass != RequestsFragment.class) {
                callHomeFrag();
            } else {
                finish();
            }
        }
    }

    void onLogOut() {
        Sessions.setUserString(HomeActivity.this, "FALSE", Controller.keepMeSignedStr);
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private void SetFrag(Class fragmentClass) {
        String backStateName = fragmentClass.getClass().getName();
        String fragmentTag = backStateName;

        Bundle bundle = new Bundle();
        bundle.putString("From", From);
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragment.setArguments(bundle);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentParentViewGroup, fragment, fragmentTag)
                .addToBackStack(backStateName)
                .commit();


    }


    private void GetAllProdSubs(String created_by) {
        //creating request body for file
        RequestBody created_byB = RequestBody.create(MediaType.parse("text/plain"), created_by);
        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<MyResponse> call = api.getallprodsubs(created_byB);
        //finally performing the call
        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                assert response.body() != null;
                if (!response.body().error) {
                    Controller.Toasty(context, "Categories updated...");
                    Sessions.setUserObj(context, response.body(), Controller.Categories);
                } else {
                    Controller.Toasty(context, "Some error occurred...");
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Controller.Toasty(context, t.getMessage());
                Log.d("Err", t.getMessage());
            }
        });
    }
}
