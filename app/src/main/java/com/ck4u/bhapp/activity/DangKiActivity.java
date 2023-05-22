package com.ck4u.bhapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.ck4u.bhapp.R;
import com.ck4u.bhapp.retrofit.ApiBanHang;
import com.ck4u.bhapp.retrofit.RetrofitClient;
import com.ck4u.bhapp.utils.Utils;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangKiActivity extends AppCompatActivity {
    EditText email, password, repassword, username, mobile;

    TextView backLogin;
    AppCompatButton button;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_ki);
        initView();
        initControll();
    }

    private void initControll() {
        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent backtoLogin = new Intent(getApplicationContext(), DangNhapActivity.class);
                startActivity(backtoLogin);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dangKi();
            }
        });
    }

    private void dangKi() {
        String str_email = email.getText().toString().trim();
        String str_username = username.getText().toString().trim();
        String str_pass = password.getText().toString().trim();
        String str_repass = repassword.getText().toString().trim();
        String str_mobile = mobile.getText().toString().trim();

        if(TextUtils.isEmpty(str_email)) {
            Toast.makeText(getApplicationContext(), "You have not entered email", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_pass)) {
            Toast.makeText(getApplicationContext(), "You have not entered password", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_repass)) {
            Toast.makeText(getApplicationContext(), "You have not entered confirm password", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_mobile)) {
            Toast.makeText(getApplicationContext(), "You have not entered phone number", Toast.LENGTH_SHORT).show();
        } else if(TextUtils.isEmpty(str_username)) {
            Toast.makeText(getApplicationContext(), "You have not entered username", Toast.LENGTH_SHORT).show();
        }else {
            if(str_pass.equals(str_repass)) {
                // post data
                compositeDisposable.add(apiBanHang.dangKi(str_email,str_pass,str_mobile,str_username)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userModel -> {
                                    if(userModel.isSuccess()) {
                                        Utils.user_current.setEmail(str_email);
                                        Utils.user_current.setPass(str_pass);
                                        Intent intent = new Intent(getApplicationContext(), DangNhapActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), userModel.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                },
                                throwable -> {
                                    Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                        ));

            } else {
                Toast.makeText(getApplicationContext(), "Password does not match, please re-enter", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initView() {
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        email = findViewById(R.id.email);
        username = findViewById(R.id.usernamedangky);
        password = findViewById(R.id.pass);
        repassword = findViewById(R.id.repass);
        mobile = findViewById(R.id.mobiledangky);
        button = findViewById(R.id.btndangki);
        backLogin = findViewById(R.id.backlogin);
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}