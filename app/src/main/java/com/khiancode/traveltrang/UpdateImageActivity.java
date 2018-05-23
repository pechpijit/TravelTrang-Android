package com.khiancode.traveltrang;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.khiancode.traveltrang.helper.ImageUtils;
import com.khiancode.traveltrang.helper.RealPathUtil;
import com.khiancode.traveltrang.okhttp.ApiClient;
import com.khiancode.traveltrang.okhttp.CallServiceListener;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UpdateImageActivity extends BaseActivity {
    private static final int REQUEST_CAMERA = 1001;
    private static final int REQUEST_GALLERY = 1002;
    private Bitmap mBitmap;
    private Button btn_upload;
    private Button btn_camera;
    private EditText input;
    private ImageView iv_image;
    private Uri selectedImageUri;

    String TAG = "imageUpload";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        iv_image =  findViewById(R.id.iv_image);
        btn_upload =  findViewById(R.id.btn_upload);
        btn_camera =  findViewById(R.id.btn_camera);
        input =  findViewById(R.id.input_detail);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initGalleryIntent();
            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UpdateImageActivity.this, "OK", Toast.LENGTH_SHORT).show();
                try {
                    execMultipartPost();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    String realPath = "";

    @Override
    @SuppressLint("NewApi")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                realPath = selectedImageUri.getPath();
            } else if (requestCode == REQUEST_GALLERY) {
                selectedImageUri = data.getData();
                if (Build.VERSION.SDK_INT < 11) {
                    realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                } else if (Build.VERSION.SDK_INT < 19) {
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                } else {
                    realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                }

                Log.d("ImageUp", "real path: " + realPath);
            }
            mBitmap = ImageUtils.getScaledImage(selectedImageUri, this);
            setImageBitmap(mBitmap);
        }
    }

    private void setImageBitmap(Bitmap bm) {
        iv_image.setImageBitmap(bm);
    }

    private void UpLoad() {
        try {
            String uploadId = UUID.randomUUID().toString();
            new MultipartUploadRequest(this, uploadId, BASE_URL+"")
                    .addFileToUpload(realPath, "image") //Adding file
                    .startUpload(); //Starting the upload

        } catch (Exception exc) {
            Log.d(TAG, "Error: " + exc.getMessage());
        }
    }

    private void execMultipartPost() throws Exception {
        SharedPreferences  sp = getSharedPreferences("Preferences_TravelTrang", Context.MODE_PRIVATE);
        btn_upload.setEnabled(false);
        showProgressDialog(LOAD);
//        Uri uriFromPath = Uri.fromFile(new File(realPath));
        File file = new File(realPath);
        String contentType = file.toURL().openConnection().getContentType();

        Log.d(TAG, "file: " + file.getPath());
        Log.d(TAG, "contentType: " + contentType);

        String uploadId = UUID.randomUUID().toString();
        RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), file);

        final String filename = "file_" + System.currentTimeMillis() / 1000L;

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("detail", input.getText().toString())
                .addFormDataPart("customerId", String.valueOf(sp.getInt("id",0)))
                .addFormDataPart("image", filename + ".jpg", fileBody)
                .build();

        ApiClient.POST post = new ApiClient.POST(this);
        post.setURL(BASE_URL+"user/diary-create");
        post.setRequestBody(requestBody);
        post.execute();
        post.setListenerCallService(new CallServiceListener() {
            @Override
            public void ResultData(String data) {
                hideProgressDialog();
                Toast.makeText(UpdateImageActivity.this, "success", Toast.LENGTH_SHORT).show();
                if (data.equals("success")) {
                   finish();
                } else {
                    btn_upload.setEnabled(true);
                    dialogTM("แจ้งเตือน", "เกิดข้อผิดพลาดบางอย่าง กรุณาลองใหม่อีกครั้ง");
                }
            }

            @Override
            public void ResultError(String data) {
                hideProgressDialog();
                dialogResultError(data);
                btn_upload.setEnabled(true);
            }

            @Override
            public void ResultNull(String data) {
                hideProgressDialog();
                dialogResultNull();
                btn_upload.setEnabled(true);
            }
        });

    }
}
