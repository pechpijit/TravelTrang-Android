package com.khiancode.traveltrang.okhttp;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiClient{
    public static class POST extends AsyncTask<Void, Void, String> {
        private Context context;
        private RequestBody requestBody;
        private CallServiceListener callServiceListener = null;
        private String URL = null;
        private ProgressDialog mProgressDialog;

        public POST(Context context) {
            this.context = context;

        }

        public void setListenerCallService(CallServiceListener callService) {
            this.callServiceListener = callService;
        }

        public void setURL(String URL) {

            this.URL = URL;
        }

        public void setRequestBody(RequestBody requestBody) {
            this.requestBody = requestBody;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .cacheControl(new CacheControl.Builder().noCache().build())
                    .url(URL)
                    .post(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Not Success - code : " + response.code();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Log.d("ApiClientPOST : ", "onPostExecute:" + string);
            String[] temp = string.split(" ");
            if (temp[0].equals("Error") || temp[0].equals("Not")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultError(string);
                }
            } else if (string.equals("[]") || string.equals("{}")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultNull(string);
                }
            } else {
                if (callServiceListener != null) {
                    callServiceListener.ResultData(string);
                }
            }
        }
    }

    public static class GET extends AsyncTask<Void, Void, String> {
        private Context context;
        private CallServiceListener callServiceListener = null;
        private String URL = null;

        public GET(Context context) {
            this.context = context;

        }

        public void setListenerCallService(CallServiceListener callService) {
            this.callServiceListener = callService;
        }

        public void setURL(String URL) {

            this.URL = URL;
        }

        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .cacheControl(new CacheControl.Builder().noCache().build())
                    .url(URL)
                    .get()
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                } else {
                    return "Not Success - code : " + response.code();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "Error - " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Log.d("ApiClientGET : ", "onPostExecute:" + string);
            String[] temp = string.split(" ");
            if (temp[0].equals("Error") || temp[0].equals("Not")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultError(string);
                }
            } else if (string.equals("[]") || string.equals("{}")) {
                if (callServiceListener != null) {
                    callServiceListener.ResultNull(string);
                }
            } else {
                if (callServiceListener != null) {
                    callServiceListener.ResultData(string);
                }
            }
        }
    }
}