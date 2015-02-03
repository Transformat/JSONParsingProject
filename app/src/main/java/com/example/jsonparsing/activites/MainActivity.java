package com.example.jsonparsing.activites;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jsonparsing.R;
import com.example.jsonparsing.util.Data;
import com.example.jsonparsing.util.MainFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends FragmentActivity {
    ListView listView;
    ArrayAdapter arrayAdapter;
    ProgressDialog progressDialog;
    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, mainFragment).commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, Details.class);
                intent.putExtra("id", Data.friendsId.get(position));
                startActivity(intent);
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_name_button:
                progressDialog = new ProgressDialog(this);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Fetching Friends...");
                progressDialog.show();
                getList();
                arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Data.friendsList);
                listView.setAdapter(arrayAdapter);

                break;
            case R.id.clear_button:
                Data.friendsList.clear();
                Data.friendsId.clear();
                arrayAdapter.notifyDataSetChanged();
                break;
        }

    }

    public void getList() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, "https://graph.facebook.com/me/friends?access_token=" + Data.ACCESS_TOKEN, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {

                        String s = new String(bytes);

                        try {

                            JSONObject jsonObject = new JSONObject(s);
                            JSONArray array = jsonObject.getJSONArray("data");
                            for (int j = 0; j < array.length(); j++) {
                                Data.friendsList.add(array.getJSONObject(j).getString("name"));
                                Data.friendsId.add(array.getJSONObject(j).getString("id"));
                                arrayAdapter.notifyDataSetChanged();

                            }
                            progressDialog.dismiss();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Access Token exipired", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


}
