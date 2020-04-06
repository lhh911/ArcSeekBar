package com.example.arcseekbar.weight.productinterface;

import androidx.annotation.IntRange;

import com.example.arcseekbar.R;

public class DogProductFactory extends ProductFactory {

    private String [] names = {"分红犬1","分红犬2","分红犬3","分红犬4","分红犬5"};
    private int [] resIds = {R.mipmap.app_logo,R.mipmap.app_logo,R.mipmap.app_logo,R.mipmap.app_logo,R.mipmap.app_logo};
    private String [] profit = {"100","200","300","400","500"};


    @Override
    public DogProduct create(@IntRange(from = 0,to = 10) int level) {
        return new DogProduct(names[level],level,profit[level],resIds[level]);
    }
}
