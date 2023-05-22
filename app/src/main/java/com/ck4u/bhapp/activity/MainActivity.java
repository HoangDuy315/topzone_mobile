package com.ck4u.bhapp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ck4u.bhapp.R;
import com.ck4u.bhapp.adapter.LoaiSpAdapter;
import com.ck4u.bhapp.adapter.SanPhamMoiAdapter;
import com.ck4u.bhapp.model.LoaiSp;
import com.ck4u.bhapp.model.SanPhamMoi;
import com.ck4u.bhapp.retrofit.ApiBanHang;
import com.ck4u.bhapp.retrofit.RetrofitClient;
import com.ck4u.bhapp.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewFlipper viewFlipper;
    RecyclerView recyclerViewManHinhChinh;
    NavigationView navigationView;
    ListView listViewManHinhChinh;
    DrawerLayout drawerLayout;
    LoaiSpAdapter loaiSpAdapter;
    List<LoaiSp> mangloaisp;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;
    List<SanPhamMoi> mangSpMoi;
    SanPhamMoiAdapter spAdapter;
    NotificationBadge badge;
    FrameLayout frameLayout;
    ImageView imgsearch, imgmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        Anhxa();
        ActionBar();

        if(isConnected(this)) {
            ActionViewFlipper();
            getLoaiSanPham();
            getSpMoi();
            getEvenClick();
        } else {
            Toast.makeText(getApplicationContext(), "Không có internet, Vui lòng kết nối mạng", Toast.LENGTH_SHORT).show();
        }
    }



    private void getEvenClick() {
        listViewManHinhChinh.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        Intent trangchu = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(trangchu);
                        break;

                    case 1:
                        Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(profile);
                        break;
                    case 2:
                        Intent dienthoai = new Intent(getApplicationContext(), DienthoaiActivity.class);
                        dienthoai.putExtra("loai",1);
                        startActivity(dienthoai);
                        break;
                    case 3:
                        Intent laptop = new Intent(getApplicationContext(), DienthoaiActivity.class);
                        laptop.putExtra("loai",2);
                        startActivity(laptop);
                        break;
                    case 4:
                        Intent amthanh = new Intent(getApplicationContext(), DienthoaiActivity.class);
                        amthanh.putExtra("loai",3);
                        startActivity(amthanh);
                        break;
                    case 5:
                        Intent ipad = new Intent(getApplicationContext(), DienthoaiActivity.class);
                        ipad.putExtra("loai",4);
                        startActivity(ipad);
                        break;
                    case 6:
                        Intent watch = new Intent(getApplicationContext(), DienthoaiActivity.class);
                        watch.putExtra("loai",5);
                        startActivity(watch);
                        break;
                    case 7:
                        Intent accessories = new Intent(getApplicationContext(), DienthoaiActivity.class);
                        accessories.putExtra("loai",6);
                        startActivity(accessories);
                        break;
                    case 8:
                        Intent donhang = new Intent(getApplicationContext(), XemDonActivity.class);
                        startActivity(donhang);
                        break;
                    case 9:
                        Intent logout = new Intent(getApplicationContext(), DangNhapActivity.class);
                        startActivity(logout);
                        break;

                }
            }
        });
    }

    private void getSpMoi() {
        compositeDisposable.add(apiBanHang.getSpMoi()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sanPhamMoiModel -> {
                            if(sanPhamMoiModel.isSuccess()) {
                                mangSpMoi = sanPhamMoiModel.getResult();
                                spAdapter = new SanPhamMoiAdapter(getApplicationContext(), mangSpMoi);
                                recyclerViewManHinhChinh.setAdapter(spAdapter);
                            }
                        }, throwable -> {
                            Toast.makeText(getApplicationContext(), "Không kết nối được với server"+throwable.getMessage(), Toast.LENGTH_LONG).show();
                        }
                ));

    }

    private void getLoaiSanPham() {
        compositeDisposable.add(apiBanHang.getLoaiSp()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        loaiSpModel -> {
                            if(loaiSpModel.isSuccess()) {
                                mangloaisp = loaiSpModel.getResult();
                                loaiSpAdapter = new LoaiSpAdapter(getApplicationContext(), mangloaisp);
                                listViewManHinhChinh.setAdapter(loaiSpAdapter);
                            }
                        }
                ));
    }


    // ham chay banner quang cao
    private void ActionViewFlipper() {
        List<String> mangquangcao = new ArrayList<>();
        mangquangcao.add("https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_100,s_1920x533/https://cdn.tgdd.vn/2023/04/banner/iPhone-14-Pro-Max-2880-800-1920x533.png");
        mangquangcao.add("https://www.erajaya.com/files/uploads/newseventattachment/uri/2021/Jan/15/600188b518f4a/available-web-banner-iphone-12-pro-dekstop-1091x_.jpg?token=3063ae63cef065bef572f6acc6e2787c");
        mangquangcao.add("https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_100,s_1920x533/https://cdn.tgdd.vn/2023/04/banner/GTN2880-800-1920x533.png");
        mangquangcao.add("https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_100,s_1920x533/https://cdn.tgdd.vn/2023/05/banner/2880-800-1920x533.png");
        mangquangcao.add("https://img.tgdd.vn/imgt/f_webp,fit_outside,quality_100,s_1920x533/https://cdn.tgdd.vn/2023/04/banner/iPhone-2880-800-1920x533.png");
        mangquangcao.add("https://media.istockphoto.com/id/1178423206/vector/cyber-monday-sale-banner-template-for-business-promotion-vector-illustration.jpg?s=612x612&w=0&k=20&c=ApQ8cW6N7iEKd18nzklHg7BwqVVEQd5Rv1gMLMYbZsk=");
        mangquangcao.add("https://cdn.tgdd.vn//News/1503953//apple-chinh-thuc-cho-ra-mat-macbook-m2-pro-va-m2-6-845x475.png");
        for(int i = 0; i<mangquangcao.size(); i++) {
            ImageView imageView = new ImageView(getApplicationContext());
            Glide.with(getApplicationContext()).load(mangquangcao.get(i)).into(imageView);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        Animation slide_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_right);
        Animation slide_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        viewFlipper.setInAnimation(slide_in);
        viewFlipper.setOutAnimation(slide_out);
    }

    private void ActionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void Anhxa() {
        imgmessage = findViewById(R.id.image_mess);
        imgsearch = findViewById(R.id.imgsearch);
        toolbar = findViewById(R.id.toolbarmanhinhchinh);
        viewFlipper = findViewById(R.id.viewlipper);
        recyclerViewManHinhChinh = findViewById(R.id.recycleview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewManHinhChinh.setLayoutManager(layoutManager);
        recyclerViewManHinhChinh.setHasFixedSize(true);
        listViewManHinhChinh = findViewById(R.id.listviewmanhinhchinh);
        navigationView = findViewById(R.id.navigationview);
        drawerLayout = findViewById(R.id.drawerlayout);
        badge = findViewById(R.id.menu_sl);
        frameLayout = findViewById(R.id.framegiohang);

        // khoi tao list
        mangloaisp = new ArrayList<>();
        mangSpMoi = new ArrayList<>();
        if(Utils.manggiohang == null) {
            Utils.manggiohang = new ArrayList<>();
        } else {
            int totalItem = 0;
            for(int i =0; i<Utils.manggiohang.size(); i++){
                totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
            }
            badge.setText(String.valueOf(totalItem));
        }
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent giohang = new Intent(getApplicationContext(), GioHangActivity.class);
                startActivity(giohang);
            }
        });

        imgsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(search);
            }
        });

        imgmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messpage = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(messpage);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int totalItem = 0;
        for(int i =0; i<Utils.manggiohang.size(); i++){
            totalItem = totalItem+ Utils.manggiohang.get(i).getSoluong();
        }
        badge.setText(String.valueOf(totalItem));
    }

    private  boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi != null && wifi.isConnected()) || (mobile != null && mobile.isConnected())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }
}