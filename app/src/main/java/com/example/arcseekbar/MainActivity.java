package com.example.arcseekbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.arcseekbar.weight.PageGridAdpater;
import com.example.arcseekbar.weight.pageGridLayout.PagerGridLayoutManager;
import com.example.arcseekbar.weight.pageGridLayout.PagerGridSnapHelper;
import com.example.arcseekbar.weight.productinterface.DogProduct;
import com.example.arcseekbar.weight.productinterface.DogProductFactory;
import com.example.arcseekbar.weight.productinterface.Product;
import com.example.arcseekbar.weight.productinterface.ProductItemView;
import com.example.arcseekbar.weight.productinterface.ProductItemView2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    PageGridAdpater mAdapter;

    private ProductItemView productItemView;
    private ProductItemView2 productItemView2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageView = findViewById(R.id.image);
        imageView.setImageResource(R.mipmap.app_logo);

        recyclerView = findViewById(R.id.recyclerview);
        // 1.水平分页布局管理器
        PagerGridLayoutManager layoutManager = new PagerGridLayoutManager(
                2, 4, PagerGridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        LinearLayoutManager manager= new LinearLayoutManager(this);
        // 2.设置滚动辅助工具
        PagerGridSnapHelper pageSnapHelper = new PagerGridSnapHelper();
        pageSnapHelper.attachToRecyclerView(recyclerView);

        mAdapter = new PageGridAdpater(this,getDatas());
        recyclerView.setAdapter(mAdapter);

        layoutManager.setPageListener(new PagerGridLayoutManager.PageListener() {
            @Override
            public void onPageSizeChanged(int pageSize) {

            }

            @Override
            public void onPageSelect(int pageIndex) {

            }
        });
        int totalPageCount = layoutManager.getTotalPageCount();



        productItemView = findViewById(R.id.productview);
        DogProductFactory factory = new DogProductFactory();
        DogProduct product = factory.create(1);
        productItemView.setProduct(product);

        productItemView2 = findViewById(R.id.productview2);
        DogProductFactory factory2 = new DogProductFactory();
        DogProduct product2 = factory2.create(1);
        productItemView2.setProduct(product2);
        productItemView2.startAnim();
    }

    private List<String> getDatas() {
        List<String> datas = new ArrayList<>();
        for (int i = 0;i< 40;i++){
            datas.add("菜单" + i);
        }
        return datas;
    }

    public void dragview(View view) {
//        startActivity(new Intent(this,DragLayoutActivity.class));
        startActivity(new Intent(this,DragGridActivity.class));
    }
}
