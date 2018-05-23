package com.khiancode.traveltrang;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.khiancode.traveltrang.model.UserModel;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class EmailSignUpActivity extends BaseActivity {

    final private static String TAG = "EmailSignInActivity";
    EditText inputName, inputEmail, inputPassword,inputConfirmpassword;
    Button btnSignup;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputConfirmpassword = findViewById(R.id.input_confirmpassword);
        btnSignup = findViewById(R.id.btn_signup);

    }

    public void onClickBackLogin(View view) {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    public void onClickConfirmSignUp(View view) {
        Log.d(TAG, "Register");
        btnSignup.setEnabled(false);

        if (!validate()) {
            btnSignup.setEnabled(true);
            return;
        }

        showProgressDialog(REGIS);

        RequestBody requestBody = new FormBody.Builder()
                .add("name", inputName.getText().toString().trim())
                .add("email", inputEmail.getText().toString().trim())
                .add("password", inputPassword.getText().toString().trim())
                .add("type", "email")
                .build();

        ApiClient.POST post = new ApiClient.POST(EmailSignUpActivity.this);
        post.setURL(BASE_URL+"user/register");
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                if (data.equals("AuthUserCollision")) {
                    hideProgressDialog();
                    btnSignup.setEnabled(true);
                    dialogTM("Alert","อีเมล์นี้ถูกใช้งานแล้ว กรุณาลองใหม่อีกครั้ง");
                } else {
                    AddUserData(data);
                }
            }

            @Override
            public void ResultError(String data) {
                hideProgressDialog();
                btnSignup.setEnabled(true);
                dialogResultError(data);
            }

            @Override
            public void ResultNull(String data) {
                hideProgressDialog();
                btnSignup.setEnabled(true);
                dialogResultNull();
            }
        });
    }

    private void AddUserData(String json) {
        Gson gson = new Gson();
        UserModel user = gson.fromJson(json, UserModel.class);

        SharedPreferences sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("login", true);
        editor.putInt("id", user.getId());
        editor.putString("name", user.getName());
        editor.putString("email", user.getEmail());
        editor.commit();

        startActivity(new Intent(EmailSignUpActivity.this, MainActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public boolean validate() {
        boolean valid = true;

        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String confirmpassword = inputConfirmpassword.getText().toString().trim();

        if (name.isEmpty()) {
            inputName.setError("กรุณากรอกชื่อ");
            valid = false;
        } else {
            inputName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("กรุณากรอกอีเมล์");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            inputPassword.setError("กรุณากรอกรหัสมากกว่าหรือเท่ากับ 4 ตัว");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        if (!password.equals(confirmpassword) || confirmpassword.length() < 4) {
            inputConfirmpassword.setError("กรุณากรอกรหัสให้ตรงกัน");
            valid = false;
        } else {
            inputConfirmpassword.setError(null);
        }

        return valid;
    }
}
