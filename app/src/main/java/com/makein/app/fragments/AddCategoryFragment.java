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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.controler.BitmapTransform;
import com.makein.app.controler.Controller;
import com.makein.app.controler.Sessions;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class AddCategoryFragment extends Fragment implements View.OnClickListener {

    Context context;
    EditText category_name, category_desc;
    ImageView triggImgGet, selectedImg;
    Button btn_add;
    Uri selectedImage = null;
    TextView toolarHead;
    private ProgressDialog dialog;

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


        return rootView;
    }

//    RecyclerView sub_items_recycle;

    private void init(View rootView) {

        category_name = (EditText) rootView.findViewById(R.id.category_name);
        category_desc = (EditText) rootView.findViewById(R.id.category_desc);
        triggImgGet = (ImageView) rootView.findViewById(R.id.triggImgGet);
        selectedImg = (ImageView) rootView.findViewById(R.id.selectedImg);
        btn_add = (Button) rootView.findViewById(R.id.btn_add);


        selectedImg.setOnClickListener(this);
        triggImgGet.setOnClickListener(this);
        btn_add.setOnClickListener(this);
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


}
