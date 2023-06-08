package com.ck4u.bhapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.ck4u.bhapp.R;
import com.ck4u.bhapp.retrofit.ApiBanHang;
import com.ck4u.bhapp.retrofit.RetrofitClient;
import com.ck4u.bhapp.utils.Utils;

import io.paperdb.Paper;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DangNhapActivity extends AppCompatActivity {

    ImageView googleBtn;

    TextView txtdangki;
    EditText email, pass;
    AppCompatButton btndangnhap;
    ApiBanHang apiBanHang;
    CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);

        initView();
        initControll();
    }



    private void initControll() {
        txtdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dangki = new Intent(getApplicationContext(), DangKiActivity.class);
                startActivity(dangki);
            }
        });
        btndangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_email = email.getText().toString().trim();
                String str_pass = pass.getText().toString().trim();
                if(TextUtils.isEmpty(str_email)) {
                    Toast.makeText(getApplicationContext(), "You have not entered email", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(str_pass)) {
                    Toast.makeText(getApplicationContext(), "You have not entered password", Toast.LENGTH_SHORT).show();
                } else {
                    // Save
                    Paper.book().write("email", str_email);
                    Paper.book().write("pass", str_pass);
                    compositeDisposable.add(apiBanHang.dangNhap(str_email, str_pass)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    userModel -> {
                                        if(userModel.isSuccess()) {
                                            Utils.user_current = userModel.getResult().get(0);
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    },
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            ));
                }
            }
        });
    }

    private void initView() {
        Paper.init(this);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        txtdangki = findViewById(R.id.txtdangki);
        email = findViewById(R.id.emaildangnhap);
        pass = findViewById(R.id.passdangnhap);
        btndangnhap = findViewById(R.id.btndangnhap);


        // read data
        if(Paper.book().read("email") != null && Paper.book().read("pass") != null) {
            email.setText(Paper.book().read("email") );
            pass.setText(Paper.book().read("pass") );

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.user_current.getEmail() != null && Utils.user_current.getPass() != null) {
            email.setText(Utils.user_current.getEmail());
            pass.setText(Utils.user_current.getPass());
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}