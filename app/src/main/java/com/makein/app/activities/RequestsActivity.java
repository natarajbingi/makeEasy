package com.makein.app.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

public class RequestsActivity extends AppCompatActivity {

//    Toolbar toolbar;
    Context context;
    EditText first_name, last_name, dateofbirth, email_id, mobile_no, addr_one, addr_two, landmark, pincode;
    Spinner gender_list;
    ImageView triggImgGet, selectedImg;
    Button btn_submit;
    private ProgressDialog dialog;
    Uri selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        context = RequestsActivity.this;
//        toolbar = findViewById(R.id.request_toolbar);
//        toolbar.setTitle("Requests");
//        toolbar.setTitleTextColor(Color.WHITE);
//        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        final ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
        dialog = new ProgressDialog(context);
        dialog.setMessage("in Progress, please wait.");


//        TextView toolarHead = (TextView) findViewById(R.id.toolarHead);
//        toolarHead.setText("Requests");

        init();
    }

    private void init() {
        first_name = (EditText) findViewById(R.id.first_name);
        last_name = (EditText) findViewById(R.id.last_name);
        dateofbirth = (EditText) findViewById(R.id.dateofbirth);
        email_id = (EditText) findViewById(R.id.email_id);
        mobile_no = (EditText) findViewById(R.id.mobile_no);
        addr_one = (EditText) findViewById(R.id.addr_one);
        addr_two = (EditText) findViewById(R.id.addr_two);
        landmark = (EditText) findViewById(R.id.landmark);
        pincode = (EditText) findViewById(R.id.pincode);
        gender_list = (Spinner) findViewById(R.id.gender_list);
        triggImgGet = (ImageView) findViewById(R.id.triggImgGet);
        selectedImg = (ImageView) findViewById(R.id.selectedImg);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        triggImgGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //opening file chooser
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateSetCall();
            }
        });
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

    private void validateSetCall() {
        String first_nameStr = "", last_nameStr = "", genderStr = "", dobStr = "", email_idStr = "",
                mobile_noStr = "", addr_oneStr = "", addr_twoStr = "", landmarkStr = "", pincodeStr = "",
                created_byStr = "";
        first_nameStr = first_name.getText().toString().trim();
        last_nameStr = last_name.getText().toString().trim();
        genderStr = gender_list.getSelectedItem().toString().trim();
        dobStr = dateofbirth.getText().toString().trim();
        email_idStr = email_id.getText().toString().trim();
        mobile_noStr = mobile_no.getText().toString().trim();
        addr_oneStr = addr_one.getText().toString().trim();
        addr_twoStr = addr_two.getText().toString().trim();
        landmarkStr = landmark.getText().toString().trim();
        pincodeStr = pincode.getText().toString().trim();
        created_byStr = "Self";//Sessions.getUserString(context,Controller.userID);
        if (first_nameStr.isEmpty() && !isValidEmail(email_idStr) && !isValidMobile(mobile_noStr) && genderStr.isEmpty()) {
            return;
        }
//        if()

        RequestBody requestFile = null;
        File file = null;
        //creating a file
        if (selectedImage != null) {
            file = new File(getRealPathFromURI(selectedImage));
            requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(selectedImage)), file);
            Log.d("requestFile", file.toString());
        }
        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), first_nameStr);
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), last_nameStr);
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), genderStr);
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), dobStr);
        RequestBody email_id = RequestBody.create(MediaType.parse("text/plain"), email_idStr);
        RequestBody passwd = RequestBody.create(MediaType.parse("text/plain"), "makeEasy123");
        RequestBody address_one = RequestBody.create(MediaType.parse("text/plain"), addr_oneStr);
        RequestBody address_two = RequestBody.create(MediaType.parse("text/plain"), addr_twoStr);
        RequestBody Landmark = RequestBody.create(MediaType.parse("text/plain"), landmarkStr);
        RequestBody mobile_no = RequestBody.create(MediaType.parse("text/plain"), mobile_noStr);
        RequestBody pincode = RequestBody.create(MediaType.parse("text/plain"), pincodeStr);
        RequestBody created_by = RequestBody.create(MediaType.parse("text/plain"), created_byStr);

        userRegister(requestFile, first_name, last_name, gender, dob, email_id,
                passwd, address_one, address_two, Landmark, mobile_no, pincode, created_by);
    }

    private void userRegister(RequestBody requestFile, RequestBody first_name, RequestBody last_name,
                              RequestBody gender, RequestBody dob, RequestBody email_id, RequestBody passwd,
                              RequestBody address_one, RequestBody address_two, RequestBody Landmark,
                              RequestBody mobile_no, RequestBody pincode, RequestBody created_by) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<MyResponse> call = api.userregister(requestFile, first_name, last_name, gender, dob, email_id,
                passwd, address_one, address_two, Landmark, mobile_no, pincode, created_by);
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

                        btn_submit.setEnabled(true);
                        resetMe();
                    } else {
                        Controller.Toasty(context, response.body().message);
                        btn_submit.setEnabled(true);
                    }
                } else {
                    Controller.Toasty(context, response.body().message);
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
                Controller.Toasty(context, "Something went wrong. please try again.");
            }
        });
    }

    public static boolean isValidMobile(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length() > 10);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    void resetMe() {
        first_name.setText("");
        last_name.setText("");
        dateofbirth.setText("");
        email_id.setText("");
        mobile_no.setText("");
        addr_one.setText("");
        addr_two.setText("");
        landmark.setText("");
        pincode.setText("");
        btn_submit.setEnabled(true);
        gender_list.setSelection(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            selectedImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_person, null));
        } else
            selectedImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_person));
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
