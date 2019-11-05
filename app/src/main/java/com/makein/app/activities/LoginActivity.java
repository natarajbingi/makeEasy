package com.makein.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.makein.app.Models.LoginRes;
import com.makein.app.R;
import com.makein.app.ServerHit.Api;
import com.makein.app.ServerHit.RetroCall;
import com.makein.app.controler.Controller;
import com.makein.app.controler.Sessions;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    Context context;
    EditText userId, password;
    AppCompatButton login;
    AppCompatCheckBox remember;
    String userIdStr, passwordStr;
    private ProgressDialog dialog;
    boolean keepMeSignedStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("in Progress, please wait.");

        String LogInDirect = Sessions.getUserObject(context, Controller.keepMeSignedStr);
        if (LogInDirect != null) {
            if (LogInDirect.equals("TRUE")) {
                LoginSuccess();
            }
        }


        userId = findViewById(R.id.input_user);
        password = findViewById(R.id.input_password);
        login = findViewById(R.id.btn_login);
        remember = findViewById(R.id.remember_me);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public boolean Validation() {
        boolean valid = true;
        userIdStr = userId.getText().toString();
        passwordStr = password.getText().toString();
        if (userIdStr.isEmpty()) {
            userId.setError("enter a valid User id");
            valid = false;
        } else {
            userId.setError(null);
            if (remember.isChecked()) {
                Sessions.setUserObject(context, userIdStr, Controller.userID);
            }
        }

        if (passwordStr.isEmpty() || password.length() < 3) {
            password.setError("enter between 3 alphanumeric characters");
            valid = false;
        } else {
            password.setError(null);
            if (remember.isChecked()) {
                Sessions.setUserObject(context, passwordStr, Controller.password);
            }
        }


        return valid;
    }

    public void login() {
        if (!Validation()) {
            Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
            login.setEnabled(true);
            return;
        } else {
            keepMeSignedStr = remember.isChecked();
            Login(userIdStr, passwordStr);

        }

        login.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }


    private void Login(String username, String pwd) {
        dialog.show();

        //creating request body for file
        RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody pwdBody = RequestBody.create(MediaType.parse("text/plain"), pwd);

        //creating our api
        Api api = RetroCall.getClient();
        //creating a call and calling the upload image method
        Call<LoginRes> call = api.login(usernameBody, pwdBody);

        //finally performing the call
        call.enqueue(new Callback<LoginRes>() {
            @Override
            public void onResponse(Call<LoginRes> call, Response<LoginRes> response) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                login.setEnabled(true);
                Controller.logPrint(call.request().toString(), null, response.body());
                assert response.body() != null;
                if (!response.body().error) {
                    if (response.body().data.get(0).createdby.equals("ADMIN")) {
                        Sessions.setUserObject(context, response.body().data.get(0).id + "", Controller.userID);
                        Sessions.setUserObject(context, response.body().data.get(0).email_id + "", Controller.emailID);
                        Sessions.setUserObject(context, response.body().data.get(0).profile_img + "", Controller.profile_img);
                        Sessions.setUserObject(context, response.body().data.get(0).first_name + " " + response.body().data.get(0).last_name + "", Controller.name);
                        Sessions.setUserObject(context, response.body().data.get(0).address_one + ", " + response.body().data.get(0).address_two + ", " + response.body().data.get(0).Landmark + "" + response.body().data.get(0).pincode, Controller.Address);
                        Sessions.setUserObj(context, response.body(), Controller.LoginRes);

                        LoginSuccess();
                        if (keepMeSignedStr) {
                            Sessions.setUserObject(context, "TRUE", Controller.keepMeSignedStr);
                        } else {
                            Sessions.setUserObject(context, "FALSE", Controller.keepMeSignedStr);
                        }
                    } else {
                        Controller.Toasty(context, "Admin Credentials are wrong, Please try again.");
                    }
                } else {
                    Controller.Toasty(context, "Something went wrong server side...");
                }
            }

            @Override
            public void onFailure(Call<LoginRes> call, Throwable t) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                Controller.Toasty(context, "Something went wrong , Please check network connection.");
                Log.d("Error", t.getMessage());
                login.setEnabled(true);
            }
        });
    }

    void LoginSuccess() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
