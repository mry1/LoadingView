package com.liuyi.customvolumcontrolbar;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private ProgressBarView pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        pb = findViewById(R.id.pb);
    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void onClick(View v) {
//        pb.start();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    public void pause(View v) {
//        pb.setProgressPause(!pb.isProgressPause());
//    }

}
