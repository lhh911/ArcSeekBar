package com.example.arcseekbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.arcseekbar.weight.productinterface.DogProduct;
import com.example.arcseekbar.weight.productinterface.DogProductFactory;
import com.example.arcseekbar.weight.productinterface.ProductItemView2;

public class DragLayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_layout);


        ProductItemView2 productItemView2 = findViewById(R.id.productview2);
        DogProductFactory factory2 = new DogProductFactory();
        DogProduct product2 = factory2.create(1);
        productItemView2.setProduct(product2);
        productItemView2.startAnim();
    }
}
