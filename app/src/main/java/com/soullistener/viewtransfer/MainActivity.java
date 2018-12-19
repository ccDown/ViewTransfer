package com.soullistener.viewtransfer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author kuan
 * Created on 2018/12/19.
 * @description
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DragView dragview = findViewById(R.id.dragview_main);
        dragview.canMoved(false);

    }
}
