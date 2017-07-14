package com.saran.tictoesampleapplication.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.saran.tictoesampleapplication.R;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LoginButton login_button;
    private CallbackManager callbackManager;
    Button skip_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();
        login_button = (LoginButton) findViewById(R.id.login_button);
        skip_btn = (Button) findViewById(R.id.skip_btn);
        printkeyhash();
        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TictoeGameActivity.class);
                intent.putExtra("name", "user1");
                startActivity(intent);
                finishAffinity();
            }
        });
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AccessToken.getCurrentAccessToken() != null) {
                    SharedPreferences.Editor editor = getSharedPreferences("pref_user_name", MODE_PRIVATE).edit();
                    editor.clear().commit();
                }
            }
        });
        login_button.setReadPermissions(Arrays.asList("email"));
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {

                        if (response.getError() != null) {
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        } else {
                            String jsonresult = String.valueOf(json);
                            Log.d("fb_D", "onCompleted: " + jsonresult);
                            String name = json.optString("name");
                            SharedPreferences.Editor editor = getSharedPreferences("pref_user_name", MODE_PRIVATE).edit();
                            editor.putString("name", name);
                            editor.commit();
                            Intent intent = new Intent(MainActivity.this, TictoeGameActivity.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

                Toast.makeText(MainActivity.this, "onCancel", Toast.LENGTH_SHORT).show();
                Log.d("fb_D", "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {

                Toast.makeText(MainActivity.this, "Error try again", Toast.LENGTH_SHORT).show();
                Log.d("fb_D", "onError: "+error.getMessage());

            }
        });


    }

    public void printkeyhash() {
        PackageInfo packageInfo;
        String key = null;
        try {
//getting application package name, as defined in manifest
            String packageName = getApplicationContext().getPackageName();

//Retriving package info
            packageInfo = getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));
            }
            Log.d("K_D", "printKeyHash: "+key);
        } catch (PackageManager.NameNotFoundException e1) {
        } catch (NoSuchAlgorithmException e) {
        } catch (Exception e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}


