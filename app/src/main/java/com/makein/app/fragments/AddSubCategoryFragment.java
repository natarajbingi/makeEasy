package com.makein.app.fragments;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makein.app.Models.GetAllSubsRes;
import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.adapters.HomeListAdapter;
import com.makein.app.adapters.SubCatListAdapter;
import com.makein.app.controler.BitmapTransform;
import com.makein.app.controler.Controller;
import com.makein.app.controler.ImageClickLIstener;
import com.makein.app.controler.Sessions;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddSubCategoryFragment extends Fragment implements View.OnClickListener, SubCatListAdapter.ItemClickListener {

    private Context context;
    private MyResponse myResponse;
    private Map<String, String> categoryListArr = null;
    private Spinner categry_list;
    private ImageView triggImgGet;
    private LinearLayout selectedImgHolder;
    private EditText sub_name, sub_desc, sub_pur_cost, sub_sell_cost;
    private Button btn_submit;
    private int outI = 0;

    private int PICK_IMAGE_MULTIPLE = 1;
    private String imageEncoded;
    private List<String> imagesEncodedList;
    private ArrayList<Uri> mArrayUri;
    private ProgressDialog dialog;

    RecyclerView show_items_recycle;
    FloatingActionButton floatingActionButton;
    CardView add_layout_holder;
    boolean showingEdit = true;

    public static AddSubCategoryFragment newInstance() {
        AddSubCategoryFragment fragment = new AddSubCategoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AddSubCategoryFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_add_sub_category, container, false);
        context = container.getContext();

        init(rootView);
        rootView.findViewById(R.id.sub_catigory_toolbar_header).setVisibility(View.GONE);


        return rootView;
    }

//    RecyclerView sub_items_recycle;

    private void init(View rootView) {

        dialog = new ProgressDialog(context);
        dialog.setMessage("in Progress, please wait.");


        categry_list = (Spinner) rootView.findViewById(R.id.categry_list);
        triggImgGet = (ImageView) rootView.findViewById(R.id.triggImgGet);
        selectedImgHolder = (LinearLayout) rootView.findViewById(R.id.selectedImgHolder);
        sub_name = (EditText) rootView.findViewById(R.id.sub_name);
        sub_desc = (EditText) rootView.findViewById(R.id.sub_desc);
        sub_pur_cost = (EditText) rootView.findViewById(R.id.sub_pur_cost);
        sub_sell_cost = (EditText) rootView.findViewById(R.id.sub_sell_cost);
        btn_submit = (Button) rootView.findViewById(R.id.btn_submit);

        show_items_recycle = (RecyclerView) rootView.findViewById(R.id.show_items_recycle);
        add_layout_holder = (CardView) rootView.findViewById(R.id.add_layout_holder);
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(this);
        getAllSubs("0");
        myResponse = (MyResponse) Sessions.getUserObj(context, Controller.Categories, MyResponse.class);
        if (myResponse != null) {
            categoryListArr = Controller.convertMapArr(myResponse.data);
            setSpinners(categry_list, categoryListArr.keySet().toArray(new String[0]));
            triggImgGet.setOnClickListener(this);
            btn_submit.setOnClickListener(this);
        } else
            Controller.alertDialogShow(context, "Please add categories to proceed.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    getActivity().onBackPressed();
                }
            });
    }


    private void setSpinners(Spinner spr, String[] array) {
        // -----------------------------------------------
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context,
                R.layout.custom_spinner,
                array);
        spinnerArrayAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        // The drop down view
        spr.setAdapter(spinnerArrayAdapter);
    }

    private void startMultiSelectImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                // Get the Image from data
                mArrayUri = new ArrayList<>();
                imagesEncodedList = new ArrayList<>();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                if (data.getData() != null) {
                    Uri mImageUri = data.getData();
                    // Get the cursor
                    /*Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                    // Move to first row
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imageEncoded = cursor.getString(columnIndex);
                    cursor.close();
                    imagesEncodedList.add(imageEncoded); // Get the cursor*/
                    mArrayUri.add(mImageUri); // Get the cursor

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {
                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                mArrayUri.add(uri); // Get the cursor
                                /*Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                                // Move to first row
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                imageEncoded = cursor.getString(columnIndex);
                                imagesEncodedList.add(imageEncoded);
                                cursor.close();*/
                            }
                            Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        }
                    }
                }
                if (mArrayUri.size() > 0) {
                    addImgs();
                }
            } else {
                Controller.Toasty(context, "You haven't picked Image");
            }
        } catch (Exception e) {
            Controller.Toasty(context, "Something went wrong");
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.triggImgGet:
                startMultiSelectImg();
                break;
            case R.id.btn_submit: {
                setDataSave(outI);
                btn_submit.setEnabled(false);
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


    private void setDataSave(int outI) {
        String created_by = Sessions.getUserObject(context, Controller.userID);
        String prod_id = categoryListArr.get(categry_list.getSelectedItem().toString());
        String sub_nameStr = sub_name.getText().toString().trim();
        String sub_descStr = sub_desc.getText().toString().trim();
        String sub_pur_costStr = sub_pur_cost.getText().toString().trim();
        String sub_sell_costStr = sub_sell_cost.getText().toString().trim();

        RequestBody requestFile = null, nameBody, descBody, prod_idBody, sub_pur_costBody, sub_sell_costBody, created_byBody;

        Log.d("HMM-No", outI + "-" + mArrayUri.get(outI));
//        Log.d("HMM-Data", getRealPathFromURI(mArrayUri.get(outI)));
        if (outI < mArrayUri.size()) {
            File file = null;
            //creating a file
            if (mArrayUri.get(outI) != null) {
                file = new File(getRealPathFromURI(mArrayUri.get(outI)));
                requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(mArrayUri.get(outI))), file);
                Log.d("requestFile", file.toString());
            }
            //creating request body for file
            nameBody = RequestBody.create(MediaType.parse("text/plain"), sub_nameStr);
            descBody = RequestBody.create(MediaType.parse("text/plain"), sub_descStr);
            prod_idBody = RequestBody.create(MediaType.parse("text/plain"), prod_id);
            sub_pur_costBody = RequestBody.create(MediaType.parse("text/plain"), sub_pur_costStr);
            sub_sell_costBody = RequestBody.create(MediaType.parse("text/plain"), sub_sell_costStr);
            created_byBody = RequestBody.create(MediaType.parse("text/plain"), created_by);
            addCategory(requestFile, nameBody, descBody, prod_idBody, sub_pur_costBody, sub_sell_costBody, created_byBody);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {

        File file = new File(contentUri.getPath());
        String[] filePath = file.getPath().split(":");
        String image_id = filePath[filePath.length - 1];
        String imagePath = "";
        Cursor cursor = context.getContentResolver().query(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Images.Media._ID + " = ? ", new String[]{image_id}, null);
        if (cursor != null) {
            cursor.moveToFirst();
            imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            // imagePathList.add(imagePath);
            cursor.close();
        }
        return imagePath;
    }

    //    mArrayUri
    private void addCategory(RequestBody requestFile, RequestBody nameBody, RequestBody descBody,
                             RequestBody prod_id, RequestBody pur_cost, RequestBody sell_cost,
                             RequestBody created_by) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<MyResponse> call = api.addsubcategory(requestFile, nameBody, descBody, prod_id, pur_cost, sell_cost, created_by);
        //finally performing the call
        call.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (outI < mArrayUri.size()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                if (response.body() != null) {
                    Controller.logPrint(call.request().toString(), null, response.body());
                    assert response.body() != null;
                    if (!response.body().error) {
                        outI++;
                        if (outI < mArrayUri.size()) {
                            setDataSave(outI);
                        } else {
                            Controller.Toasty(context, response.body().message);
                            reset();
                            btn_submit.setEnabled(true);
                        }
                    } else {
                        Controller.Toasty(context, "Some error occurred...");
                        btn_submit.setEnabled(true);
                    }
                } else {
                    Controller.Toasty(context, "Full error occurred...");
                    btn_submit.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                btn_submit.setEnabled(true);
                Log.d("Err", t.getMessage());
                Controller.Toasty(context, t.getMessage());
            }
        });
    }

    private void getAllSubs(String created_by) {
        dialog.show();
        //creating request body for file
        RequestBody created_byB = RequestBody.create(MediaType.parse("text/plain"), created_by);
        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<GetAllSubsRes> call = api.getallsubs(created_byB);
        //finally performing the call
        call.enqueue(new Callback<GetAllSubsRes>() {
            @Override
            public void onResponse(Call<GetAllSubsRes> call, Response<GetAllSubsRes> response) {
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
            public void onFailure(Call<GetAllSubsRes> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Controller.Toasty(context, t.getMessage());
                Log.d("Err", t.getMessage());
            }
        });
    }

    private void RecyClPatch(GetAllSubsRes response) {

        SubCatListAdapter adapter = new SubCatListAdapter(context, response.data);
        adapter.setClickListener(this);
        show_items_recycle.setAdapter(adapter);
        show_items_recycle.setLayoutManager(new LinearLayoutManager(context));
        show_items_recycle.setItemAnimator(new DefaultItemAnimator());
    }

    private void reset() {

        categry_list.setSelection(0);
        sub_name.setText("");
        sub_desc.setText("");
        sub_pur_cost.setText("");
        sub_sell_cost.setText("");
        selectedImgHolder.removeAllViews();
        outI = 0;
        mArrayUri.clear();
        imagesEncodedList.clear();
    }


    private void addImgs() {
        if (mArrayUri.size() > 0) {
            selectedImgHolder.removeAllViews();
            for (int i = 0; i < mArrayUri.size(); i++) {
                final ViewGroup newView1 = (ViewGroup) LayoutInflater.from(context)
                        .inflate(R.layout.img_layout, selectedImgHolder, false);
                ImageView imgNewScroll = (ImageView) newView1.findViewById(R.id.imgNewScroll);
                imgNewScroll.setImageURI(mArrayUri.get(i));
                imgNewScroll.setOnClickListener(new ImageClickLIstener(context, mArrayUri.get(i)));
                selectedImgHolder.addView(newView1);
            }
        }
    }


    @Override
    public void onItemClick(View view, GetAllSubsRes.Data data) {
        Controller.Toasty(context, data.name + ": " + data.description);

    }
}
