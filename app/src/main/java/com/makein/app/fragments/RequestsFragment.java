package com.makein.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.makein.app.Models.MyReqsResponse;
import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.adapters.ReqListAdapter;
import com.makein.app.controler.Controller;
import com.makein.app.controler.Sessions;


import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RequestsFragment extends Fragment implements ReqListAdapter.ItemClickListener {

    Context context;
    RecyclerView show_items_recycle;
    private ProgressDialog dialog;
    MyResponse myResponse;

    public static RequestsFragment newInstance() {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RequestsFragment() {
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


        getAllProdReqs();

    }

    private void RecyClPatch(MyReqsResponse response) {

        ReqListAdapter adapter = new ReqListAdapter(context, response.data);
        adapter.setClickListener(this);
        show_items_recycle.setAdapter(adapter);
        show_items_recycle.setLayoutManager(new LinearLayoutManager(context));
        show_items_recycle.setItemAnimator(new DefaultItemAnimator());
    }

    private void getAllProdReqs() {
        dialog.show();
        //creating request body for file
        RequestBody useridB = RequestBody.create(MediaType.parse("text/plain"), "0");
        RequestBody delistatB = RequestBody.create(MediaType.parse("text/plain"), "");
        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<MyReqsResponse> call = api.getAllProdReqs(useridB, delistatB);
        //finally performing the call
        call.enqueue(new Callback<MyReqsResponse>() {
            @Override
            public void onResponse(Call<MyReqsResponse> call, Response<MyReqsResponse> response) {
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
            public void onFailure(Call<MyReqsResponse> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Controller.Toasty(context, t.getMessage());

                Log.d("Err", t.getMessage());
            }
        });
    }

    @Override
    public void onItemClick(View view, MyReqsResponse.Data data) {
        Controller.Toasty(context, data.usrName + ": " + data.prodName);
    }

}
