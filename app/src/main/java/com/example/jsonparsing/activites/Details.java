package com.example.jsonparsing.activites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jsonparsing.R;
import com.example.jsonparsing.util.AppConstants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

public class Details extends Activity implements AppConstants {
    String id;
    ProgressDialog progressDialog;
    String profileUrl = "Default:";
    TextView firstNameTv;
    TextView lastNameTv;
    TextView genderTv;

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        id = getIntent().getStringExtra("id");
        firstNameTv = (TextView) findViewById(R.id.first_name_tv);
        lastNameTv = (TextView) findViewById(R.id.last_name_tv);
        genderTv = (TextView) findViewById(R.id.gender_tv);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Details...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        getDetails();


    }

    public void getDetails() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, "https://graph.facebook.com/" + id + "?access_token=" + ACCESS_TOKEN, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String s = new String(bytes);

                try {
                    JSONObject jsonObject = new JSONObject(s);


                    profileUrl = jsonObject.getString("link");
                    firstNameTv.setText(jsonObject.getString("first_name"));
                    lastNameTv.setText(jsonObject.getString("last_name"));
                    genderTv.setText(jsonObject.getString("gender"));
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }


        });
    }

    public void onClick(View view) {
        Intent chooser = null;
        Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
        myWebLink.setData(Uri.parse(profileUrl));
        chooser = Intent.createChooser(myWebLink, "Select Browser");
        startActivity(chooser);
    }

}

