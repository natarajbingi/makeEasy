package com.makein.app.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.controler.Controller;
import com.makein.app.controler.Sessions;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddCategories extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    EditText category_name, category_desc;
    ImageView triggImgGet, selectedImg;
    Button btn_add;
    Uri selectedImage = null;
    Context context;
    TextView toolarHead;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categories);
        context = AddCategories.this;
        toolbar = (Toolbar) findViewById(R.id.catigory_toolbar);
        toolbar.setTitle("Add Category");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        dialog = new ProgressDialog(context);
        dialog.setMessage("in Progress, please wait.");


        toolarHead = (TextView) findViewById(R.id.toolarHead);
        toolarHead.setText("Add Category");

        category_name = (EditText) findViewById(R.id.category_name);
        category_desc = (EditText) findViewById(R.id.category_desc);
        triggImgGet = (ImageView) findViewById(R.id.triggImgGet);
        selectedImg = (ImageView) findViewById(R.id.selectedImg);
        btn_add = (Button) findViewById(R.id.btn_add);


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    }

    void setDataSave(Uri fileUri, String name, String desc) {
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
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(proj[0]);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_none, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) // API 5+ solution
        {
            finish();//  onBackPressed();
        }
        return true;
    }

}
