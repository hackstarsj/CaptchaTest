package com.supercoders.captchatest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {


    EditText username;
    EditText password;
    Button login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        login_btn=findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProcessLogin();
            }
        });

    }

    private void ProcessLogin() {
        SafetyNet.getClient(MainActivity.this).verifyWithRecaptcha("YOUR_CLIENT_KEY")
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                            String captchaToken=recaptchaTokenResponse.getTokenResult();

                            if(captchaToken!=null){
                                if(!captchaToken.isEmpty()){
                                    processLoginStep(captchaToken,username.getText().toString(),password.getText().toString());
                                }
                                else{
                                    Toast.makeText(MainActivity.this, "Invalid Captcha Response", Toast.LENGTH_SHORT).show();
                                }
                            }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to Load Captcha", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processLoginStep(String token,String username,String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://192.168.43.152/captcha_check.php?captcha="+token+"&username="+username+"&password="+password;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(MainActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

}