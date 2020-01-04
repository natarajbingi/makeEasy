package com.makein.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.activities.HomeActivity;
import com.makein.app.adapters.HomeListAdapter;
import com.makein.app.controler.Controller;
import com.makein.app.controler.Sessions;

import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements HomeListAdapter.ItemClickListener {

    Context context;
    RecyclerView show_items_recycle;
    private ProgressDialog dialog;
    MyResponse myResponse;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = container.getContext();
        init(rootView);


        return rootView;
    }

    private void init(View rootView) {

        dialog = new ProgressDialog(context);
        dialog.setMessage("in Progress, please wait.");
        show_items_recycle = (RecyclerView) rootView.findViewById(R.id.show_items_recycle);

//        myResponse = (MyResponse) Sessions.getUserObj(context, Controller.Categories, MyResponse.class);
//        if (myResponse == null) {
            String userId = Sessions.getUserObject(context, Controller.userID);
            GetAllProdSubs(userId);
        /*} else {
            RecyClPatch(myResponse);
        }*/
    }

    private void RecyClPatch(MyResponse response) {

        HomeListAdapter adapter = new HomeListAdapter(context, response.data);
        adapter.setClickListener(this);
        show_items_recycle.setAdapter(adapter);
        show_items_recycle.setLayoutManager(new LinearLayoutManager(context));
        show_items_recycle.setItemAnimator(new DefaultItemAnimator());
    }

    private void GetAllProdSubs(String created_by) {
        dialog.show();
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
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                assert response.body() != null;
                if (!response.body().error) {
                    Sessions.setUserObj(context, response.body(), Controller.Categories);
                    RecyClPatch(response.body());
                } else {
                    Controller.Toasty(context, "Some error occurred...");
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Controller.Toasty(context, t.getMessage());

                Log.d("Err", t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(View view, MyResponse.Data data) {
        Controller.Toasty(context, data.name + ": " + data.description);

        Bundle bundle = new Bundle();
        bundle.putString("From", data.name + "_" + data.id);
        bundle.putSerializable("data_cls", (Serializable) data);
        SetFrag(SubCategoryFragment.class, bundle);
    }

    private void SetFrag(Class fragmentClass, Bundle bundle) {
        String backStateName = fragmentClass.getClass().getName();
        String fragmentTag = backStateName;
        HomeActivity.fragmentClass = fragmentClass;
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fragment.setArguments(bundle);
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.fragmentParentViewGroup, fragment, fragmentTag)
                .addToBackStack(backStateName)
                .commit();
    }


}
