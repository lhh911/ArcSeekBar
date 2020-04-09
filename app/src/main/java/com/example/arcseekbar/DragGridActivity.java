package com.example.arcseekbar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import com.example.arcseekbar.weight.DragGridView;
import com.example.arcseekbar.weight.DragGridView2;
import com.example.arcseekbar.weight.productinterface.DogProduct;
import com.example.arcseekbar.weight.productinterface.DogProductFactory;
import com.example.arcseekbar.weight.productinterface.ProductItemView;
import com.example.arcseekbar.weight.productinterface.ProductItemView2;
import com.example.arcseekbar.weight.productinterface.ProductView;

import java.util.ArrayList;
import java.util.Random;

public class DragGridActivity extends AppCompatActivity {

    static Random random = new Random();
    static String[] words = "我 是 一 只 大 笨 猪".split(" ");
    DragGridView2 mDragGridView;
    Button mAddBtn, mViewBtn;
    ArrayList<String> poem = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_grid);

        mDragGridView = ((DragGridView2) findViewById(R.id.vgv));
        mAddBtn = ((Button) findViewById(R.id.add_item_btn));
        mViewBtn = ((Button) findViewById(R.id.view_poem_item));

        setListeners();
    }

    private void setListeners() {

        mDragGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
//                mDragGridView.removeViewAt(arg2);
//                poem.remove(arg2);
            }
        });
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
//                String word = words[random.nextInt(words.length)];
//                ImageView view = new ImageView(DragGridActivity.this);
//                view.setImageBitmap(getThumb(word));
//                mDragGridView.addProductView(view);
                mDragGridView.addProductView(createProduct(random.nextInt(9)+1));
//                poem.add(word);
            }
        });
        mViewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String finishedPoem = "";
                for (String s : poem)
                    finishedPoem += s + " ";
                new AlertDialog.Builder(DragGridActivity.this).setTitle("这是你选择的")
                        .setMessage(finishedPoem).show();
            }
        });
    }

    private Bitmap getThumb(String s) {
        Bitmap bmp = Bitmap.createBitmap(150, 150, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();

        paint.setColor(Color.rgb(random.nextInt(128), random.nextInt(128),
                random.nextInt(128)));
        paint.setTextSize(24);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        canvas.drawRect(new Rect(0, 0, 150, 150), paint);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(s, 75, 75, paint);

        return bmp;
    }

    public View createProduct(int type){
        ProductItemView2 productView = new ProductItemView2(this);
//        ProductView productView = new ProductView(this);
        DogProductFactory factory = new DogProductFactory();
        DogProduct product = factory.create(type);
        productView.setProduct(product);
        return productView;
    }
}
