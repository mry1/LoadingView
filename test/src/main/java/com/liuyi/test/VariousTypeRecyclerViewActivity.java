package com.liuyi.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

/**
 * @author liuyi
 * @date 2018/9/19
 * @description
 */
public class VariousTypeRecyclerViewActivity extends AppCompatActivity {

    private RecyclerView mRv_various_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_various_recycler_view);
        mRv_various_type = findViewById(R.id.rv_various_type);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);

        mRv_various_type.setLayoutManager(gridLayoutManager);
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            strings.add("多样式RecyclerView" + i);
        }
        RVAdapter adapter = new RVAdapter(this, strings);
        mRv_various_type.setAdapter(adapter);
    }

}
