package com.khiancode.traveltrang;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    public static String BASE_URL = "http://khiancode.commsk.com/public/api/";
    public static String BASE_URL_PICTURE = "http://khiancode.commsk.com/public";

//    public static String BASE_URL = "http://192.168.1.51:8989/api/";
//    public static String BASE_URL_PICTURE = "http://192.168.1.51:8989";

    public static String AUTH = "กำลังเข้าสู่ระบบ...";
    public static String REGIS = "กำลังสมัครสมาชิก...";
    public static String LOAD = "กำลังโหลดข้อมูล...";
    public static String VERIFY = "กำลังตรวจสอบ...";


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this, R.style.AppTheme_Dark_Dialog);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    public void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    public void invisibleView(View... views) {
        for (View v : views) {
            v.setVisibility(View.INVISIBLE);
        }
    }

    public void visibleView(View... views) {
        for (View v : views) {
            v.setVisibility(View.VISIBLE);
        }
    }

    public void dialogTM(String title, String message) {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("ตกลง", null)
                .setCancelable(false)
                .show();
    }

    public void dialogTM(String title, String message, String btn1, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(btn1, listener)
                .setCancelable(false)
                .show();
    }

    public void dialogResultError() {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle("Alert")
                .setMessage("ไม่สามารถเข้าใช้งานได้ กรุณาลองใหม่อีกครั้ง")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public void dialogResultError2() {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle("Alert")
                .setMessage("ไม่สามารถเข้าใช้งานได้ กรุณาลองใหม่อีกครั้ง")
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    public void dialogResultError(String string) {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle("Alert")
                .setMessage("ไม่สามารถเข้าใช้งานได้ กรุณาลองใหม่ภายหลัง error code = " + string)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }



    public void dialogResultNull() {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle("Alert")
                .setMessage("ไม่พบข้อมูล")
                .setNegativeButton("OK", null)
                .setCancelable(false)
                .show();
    }

    public void dialogResultNull(String message) {
        new AlertDialog.Builder(this, R.style.AppTheme_Dark_Dialog)
                .setTitle("Alert")
                .setMessage(message)
                .setNegativeButton("OK", null)
                .setCancelable(false)
                .show();
    }

    protected void LogoutApp() {
        SharedPreferences sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("login", false);
        editor.putInt("id", 0);
        editor.putString("name", "");
        editor.putString("email", "");
        editor.commit();

        startActivity(new Intent(this, SplashActivity.class));
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
