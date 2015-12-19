package com.loc.floatlayout;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SheetLayout.OnFabAnimationEndListener {
    SheetLayout sheetLayout;
    FloatingActionButton fad;
    private static final int REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sheetLayout = (SheetLayout) findViewById(R.id.bottom_sheet);
        fad = (FloatingActionButton) findViewById(R.id.fab);
        sheetLayout.setFab(fad);
        sheetLayout.setFabAnimationEndListener(this);
        fad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetLayout.expandFab();
            }
        });
    }

    @Override
    public void onFabAnimationEnd() {
        Intent intent = new Intent(this, Main2Activity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            sheetLayout.contractFab();
        }
    }
//    private int setColorFilter(int color){
    //        if((color % 4) == 0) {
//            return ContextCompat.getColor(getApplicationContext(), R.color.one_round);
//        }if((color % 3) == 0) {
//            return ContextCompat.getColor(getApplicationContext(), R.color.two_round);
//        }if((color % 2) == 0) {
//            return ContextCompat.getColor(getApplicationContext(), R.color.three_round);
//        }else {
//            return ContextCompat.getColor(getApplicationContext(), R.color.four_round);
//        }
//    }


}

