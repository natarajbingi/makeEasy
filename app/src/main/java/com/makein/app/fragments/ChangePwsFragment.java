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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;

import com.makein.app.Models.LoginRes;
import com.makein.app.Models.MyResponse;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.activities.HomeActivity;
import com.makein.app.activities.LoginActivity;
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
import retrofit2.http.Part;

import static android.app.Activity.RESULT_OK;

public class ChangePwsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private EditText first_name, last_name, dateofbirth, email_id, mobile_no, addr_one, addr_two, landmark,
            pincode, oldPass, newPass, reEnterPass;

    private Spinner gender_list;
    private ImageView triggImgGet, selectedImg;
    private Button btn_submit, btn_chng_pass;
    private Uri selectedImage = null;
    LoginRes res;
    String oldPassText, newPassText, reEnterPassText, userData, pasData;

    private ProgressDialog dialog;

    public static ChangePwsFragment newInstance() {
        ChangePwsFragment fragment = new ChangePwsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ChangePwsFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_change_password, container, false);
        context = container.getContext();

        init(rootView);


        return rootView;
    }

    public static boolean isValidMobile(CharSequence target) {
        return (!TextUtils.isEmpty(target) && target.length() > 10);
    }

    void profValid() {
        String first_nameStr = "", last_nameStr = "", genderStr = "", dobStr = "",
                mobile_noStr = "", addr_oneStr = "", addr_twoStr = "", landmarkStr = "", pincodeStr = "";
        first_nameStr = first_name.getText().toString().trim();
        last_nameStr = last_name.getText().toString().trim();
        genderStr = gender_list.getSelectedItem().toString().trim();
        dobStr = dateofbirth.getText().toString().trim();
        mobile_noStr = mobile_no.getText().toString().trim();
        addr_oneStr = addr_one.getText().toString().trim();
        addr_twoStr = addr_two.getText().toString().trim();
        landmarkStr = landmark.getText().toString().trim();
        pincodeStr = pincode.getText().toString().trim();
        if (!isValidMobile(mobile_noStr) && genderStr.equals("SELECT")) {
            Controller.Toasty(context, "Please enter mandatory fields.");
            return;
        }

        String id = Sessions.getUserObject(context, Controller.userID);
        String name = res.data.get(0).createdby;
        //creating request body for file
        RequestBody idq = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody first_name = RequestBody.create(MediaType.parse("text/plain"), first_nameStr);
        RequestBody last_name = RequestBody.create(MediaType.parse("text/plain"), last_nameStr);
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), genderStr);
        RequestBody dob = RequestBody.create(MediaType.parse("text/plain"), dobStr);
        RequestBody address_one = RequestBody.create(MediaType.parse("text/plain"), addr_oneStr);
        RequestBody address_two = RequestBody.create(MediaType.parse("text/plain"), addr_twoStr);
        RequestBody Landmark = RequestBody.create(MediaType.parse("text/plain"), landmarkStr);
        RequestBody mobile_no = RequestBody.create(MediaType.parse("text/plain"), mobile_noStr);
        RequestBody pincode = RequestBody.create(MediaType.parse("text/plain"), pincodeStr);
        RequestBody created_by = RequestBody.create(MediaType.parse("text/plain"), name);


        updateProfDetails(first_name, last_name, gender, idq,
                dob, address_one, address_two, Landmark, mobile_no, pincode, created_by);
    }

    private void init(View rootView) {

        dialog = new ProgressDialog(context);
        dialog.setMessage("in Progress, please wait.");

        oldPass = (EditText) rootView.findViewById(R.id.old_password);
        newPass = (EditText) rootView.findViewById(R.id.new_password);
        reEnterPass = (EditText) rootView.findViewById(R.id.re_password);
        btn_chng_pass = (Button) rootView.findViewById(R.id.btn_chng_pass);


        first_name = (EditText) rootView.findViewById(R.id.first_name);
        last_name = (EditText) rootView.findViewById(R.id.last_name);
        dateofbirth = (EditText) rootView.findViewById(R.id.dateofbirth);
        email_id = (EditText) rootView.findViewById(R.id.email_id);
        mobile_no = (EditText) rootView.findViewById(R.id.mobile_no);
        addr_one = (EditText) rootView.findViewById(R.id.addr_one);
        addr_two = (EditText) rootView.findViewById(R.id.addr_two);
        landmark = (EditText) rootView.findViewById(R.id.landmark);
        pincode = (EditText) rootView.findViewById(R.id.pincode);
        gender_list = (Spinner) rootView.findViewById(R.id.gender_list);
        triggImgGet = (ImageView) rootView.findViewById(R.id.triggImgGet);
        selectedImg = (ImageView) rootView.findViewById(R.id.selectedImg);
        btn_submit = (Button) rootView.findViewById(R.id.btn_submit);
        btn_submit.setText("Update Profile");
        email_id.setEnabled(false);

        res = (LoginRes) Sessions.getUserObj(context, Controller.LoginRes, LoginRes.class);

        first_name.setText(res.data.get(0).first_name);
        last_name.setText(res.data.get(0).last_name);
        dateofbirth.setText(res.data.get(0).dob);
        email_id.setText(res.data.get(0).email_id);
        mobile_no.setText(res.data.get(0).mobile_no);
        addr_one.setText(res.data.get(0).address_one);
        addr_two.setText(res.data.get(0).address_two);
        landmark.setText(res.data.get(0).Landmark);
        pincode.setText(res.data.get(0).pincode);
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
                    .into(selectedImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (res.data.get(0).gender.toUpperCase().equals("MALE")) {
            gender_list.setSelection(1);
        } else if (res.data.get(0).gender.toUpperCase().equals("FEMALE")) {
            gender_list.setSelection(2);
        } else gender_list.setSelection(0);

        selectedImg.setOnClickListener(this);
        triggImgGet.setOnClickListener(this);
        btn_submit.setOnClickListener(this);
        btn_chng_pass.setOnClickListener(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            //the image URI
            selectedImage = data.getData();
            selectedImg.setImageURI(selectedImage);
            if (selectedImage != null)
                if (Controller.isOnline(context))
                    changeProfPic();
                else
                    Controller.Toasty(context, "Please check network connection.");
            else {
                Controller.Toasty(context, "No Image selected to change profile pic.");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_chng_pass:
                changePassFunction();
                break;
            case R.id.btn_submit:
                profValid();
//                Controller.Toasty(context, "In Progress..!");
                break;
            case R.id.triggImgGet:
                //opening file chooser
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, 100);
                break;
            case R.id.selectedImg:
                if (!res.data.get(0).profile_img.isEmpty()) {
                    Bitmap image = ((BitmapDrawable) selectedImg.getDrawable()).getBitmap();
                    Controller.popUpImg(context, null, "Selected Image", "", image, "bitMap");
                }
                break;
        }
    }

    private void changePassFunction() {
        Log.d("TAG", "changePassFunction");
        boolean vv = validate();
        if (vv)
            if (Controller.isOnline(context))
                changePw();
            else
                Controller.Toasty(context, "Please check network connection.");

    }

    private boolean validate() {
        boolean valid = true;
        oldPassText = oldPass.getText().toString();
        newPassText = newPass.getText().toString();
        reEnterPassText = reEnterPass.getText().toString();

        if (oldPassText.isEmpty() || newPassText.isEmpty() || reEnterPassText.isEmpty()) {
            valid = false;
            Controller.Toasty(context, "All Fields are mandatory, Some Text Field are Empty");
        }

        if (valid) {
            pasData = Sessions.getUserObject(context, Controller.password);
            if (oldPassText.equals(pasData)) {
                if (!oldPassText.equals(newPassText)) {
                    if (newPassText.equals(reEnterPassText)) {
                        valid = true;
                    } else {
                        valid = false;
                        Controller.Toasty(context, "New password and Re-Entered password are not similar");
                    }
                } else {
                    valid = false;
                    Controller.Toasty(context, "Old and New passwords are similar, please provide different new password");
                }
            } else {
                valid = false;
                Controller.Toasty(context, "Please provide correct old password");
            }

        }

        return valid;
    }


    private void updateProfDetails(RequestBody first_name, RequestBody last_name, RequestBody gender, RequestBody id,
                                   RequestBody dob, RequestBody address_one, RequestBody address_two,
                                   RequestBody Landmark, RequestBody mobile_no, RequestBody pincode, RequestBody createdby) {
        dialog.show();
        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<LoginRes> call = api.userupdate(first_name, last_name, gender, id,
                dob, address_one, address_two, Landmark, mobile_no, pincode, createdby);
        //finally performing the call
        call.enqueue(new Callback<LoginRes>() {
            @Override
            public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                if (response.body() != null) {
                    Controller.logPrint(call.request().toString(), null, response.body());
                    assert response.body() != null;
                    if (!response.body().error) {
                        Controller.Toasty(context, response.body().message);
                        Sessions.setUserObject(context, response.body().data.get(0).first_name + " " + response.body().data.get(0).last_name + "", Controller.name);
                        Sessions.setUserObject(context, response.body().data.get(0).address_one + ", " + response.body().data.get(0).address_two + ", " + response.body().data.get(0).Landmark + "" + response.body().data.get(0).pincode, Controller.Address);
                        Sessions.setUserObj(context, response.body(), Controller.LoginRes);

                    } else {
                        Controller.Toasty(context, response.body().message);
                    }
                } else {
                    Controller.Toasty(context, "Something went wrong with server. try again");
                }
            }

            @Override
            public void onFailure(Call<LoginRes> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.d("Err", t.getMessage());
                Controller.Toasty(context, "Something went wrong, Please Check network.");
            }
        });
    }

    private void changePw() {
        dialog.show();
        //creating our api
        Api api = RetroCall.getClient();
        String id = Sessions.getUserObject(context, Controller.userID);
        String email_id = Sessions.getUserObject(context, Controller.emailID);
        String name = res.data.get(0).createdby;
        //creating request body for file
        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody pwdBody = RequestBody.create(MediaType.parse("text/plain"), reEnterPassText);
        RequestBody email_idBody = RequestBody.create(MediaType.parse("text/plain"), email_id);
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);

        //creating a call and calling the upload image method
        Call<MyResponse> call = api.updateuserpas(idBody, pwdBody, email_idBody, nameBody);
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
                        Sessions.setUserString(context, "FALSE", Controller.keepMeSignedStr);
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    } else {
                        Controller.Toasty(context, response.body().message);
                    }
                } else {
                    Controller.Toasty(context, "Something went wrong with server. try again");
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Log.d("Err", t.getMessage());
                Controller.Toasty(context, "Something went wrong, Please Check network.");
            }
        });
    }

    private void changeProfPic() {
        dialog.show();
        //creating our api
        Api api = RetroCall.getClient();
        RequestBody requestFile = null;
        File file = null;
        //creating a file
        if (selectedImage != null) {
            file = new File(getRealPathFromURI(selectedImage));
            requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(selectedImage)), file);
            Log.d("requestFile", file.toString());
            String id = Sessions.getUserObject(context, Controller.userID);
            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), id);

            String name = Sessions.getUserObject(context, Controller.name);
            RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);

            //creating a call and calling the upload image method
            Call<MyResponse> call = api.userprofpicupdate(requestFile, idBody, nameBody);
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
                            Controller.Toasty(context, response.body().message.split("-")[0]);
                            Sessions.setUserObject(context, response.body().message.split("-")[1], Controller.profile_img);
                        } else {
                            Controller.Toasty(context, response.body().message);
                        }
                    } else {
                        Controller.Toasty(context, "Something went wrong with server. try again");
                    }
                }

                @Override
                public void onFailure(Call<MyResponse> call, Throwable t) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    Log.d("Err", t.getMessage());
                    Controller.Toasty(context, "Something went wrong, Please Check network.");
                }
            });
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

}
