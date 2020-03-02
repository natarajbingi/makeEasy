package com.makein.app.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.activities.HomeActivity;
import com.makein.app.adapters.HomeListAdapter;
import com.makein.app.controler.BitmapTransform;
import com.makein.app.controler.Controller;
import com.makein.app.controler.Sessions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddCategoryFragment extends Fragment implements View.OnClickListener, HomeListAdapter.ItemClickListener {

    Context context;
    EditText category_name, category_desc;
    ImageView triggImgGet, selectedImg;
    Button btn_add;
    Uri selectedImage = null;
    TextView toolarHead;
    private ProgressDialog dialog;
    RecyclerView show_items_recycle;
    FloatingActionButton floatingActionButton;
    CardView add_layout_holder;
    boolean showingEdit = true;

    public static AddCategoryFragment newInstance() {
        AddCategoryFragment fragment = new AddCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddCategoryFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_categories, container, false);
        context = container.getContext();
        dialog = new ProgressDialog(context);
        dialog.setMessage("in Progress, please wait.");


        init(rootView);
        rootView.findViewById(R.id.catigory_toolbar_header).setVisibility(View.GONE);


        return rootView;
    }

    //    RecyclerView sub_items_recycle;
    private void init(View rootView) {

        show_items_recycle = (RecyclerView) rootView.findViewById(R.id.show_items_recycle);

        category_name = (EditText) rootView.findViewById(R.id.category_name);
        category_desc = (EditText) rootView.findViewById(R.id.category_desc);
        triggImgGet = (ImageView) rootView.findViewById(R.id.triggImgGet);
        selectedImg = (ImageView) rootView.findViewById(R.id.selectedImg);
        add_layout_holder = (CardView) rootView.findViewById(R.id.add_layout_holder);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        btn_add = (Button) rootView.findViewById(R.id.btn_add);


        selectedImg.setOnClickListener(this);
        triggImgGet.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        String userId = Sessions.getUserObject(context, Controller.userID);
        GetAllProdSubs(userId);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                //calling the upload file method after choosing the file
                String category_nameStr = category_name.getText().toString();
                String category_descStr = category_desc.getText().toString();
                setDataSave(selectedImage, category_nameStr, category_descStr);
                break;
            case R.id.triggImgGet:
                //opening file chooser
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
                break;
            case R.id.selectedImg:
                if (selectedImage != null) {
                    Controller.popUpImg(context, selectedImage, "Selected Image", null, null, "URI");
                }
                break;
            case R.id.floatingActionButton:
                if (showingEdit) {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.arrow_back));
                    show_items_recycle.setVisibility(View.GONE);
                    add_layout_holder.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white));
                    show_items_recycle.setVisibility(View.VISIBLE);
                    add_layout_holder.setVisibility(View.GONE);
                }
                showingEdit = !showingEdit;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            //the image URI
            selectedImage = data.getData();
            selectedImg.setImageURI(selectedImage);
        }
    }

    void reset() {
        selectedImage = null;
        category_name.setText("");
        category_desc.setText("");
        selectedImg.setImageDrawable(getResources().getDrawable(R.drawable.add_a_photo));

    }

    private void setDataSave(Uri fileUri, String name, String desc) {
        RequestBody requestFile = null;
        File file = null;
        //creating a file
        if (fileUri != null) {
            file = new File(getRealPathFromURI(fileUri));
            requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);
            Log.d("requestFile", file.toString());
        }
        String nameUser = Sessions.getUserObject(context, Controller.userID);
        //creating request body for file
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody descBody = RequestBody.create(MediaType.parse("text/plain"), desc);
        RequestBody nameUserBody = RequestBody.create(MediaType.parse("text/plain"), nameUser);
        addCategory(requestFile, nameBody, descBody, nameUserBody);
    }

    private void addCategory(RequestBody requestFile, RequestBody nameBody, RequestBody descBody, RequestBody nameUserBody) {
        dialog.show();
        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<MyResponse> call = api.addcategory(requestFile, nameBody, descBody, nameUserBody);
        //finally performing the call
        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.body() != null) {
                    Controller.logPrint(call.request().toString(), null, response.body());
                    assert response.body() != null;
                    if (!response.body().error) {
                        Controller.Toasty(context, response.body().message);
                        reset();
                    } else {
                        Controller.Toasty(context, "Some error occurred...");
                    }
                } else {
                    Controller.Toasty(context, "Full error occurred...");
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.d("Err", t.getMessage());
                Controller.Toasty(context, t.getMessage());
            }
        });
    }

    private void RecyClPatch(MyResponse response) {

        HomeListAdapter adapter = new HomeListAdapter(context, response.data);
       // adapter.setClickListener(this);
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

    /*
     * This method is fetching the absolute path of the image file
     * if you want to upload other kind of files like .pdf, .docx
     * you need to make changes on this method only
     * Rest part will be the same
     * */
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(proj[0]);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
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
