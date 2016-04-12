package com.limosys.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;

import com.gordonwong.materialsheetfab.AnimatedFab;

/**
 * Created by Andrii on 4/9/16.
 */
public class DispLineFAB extends FloatingActionButton implements AnimatedFab {


    public DispLineFAB(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void show(float translationX, float translationY) {
        setVisibility(View.VISIBLE);

    }

    @Override
    public void hide() {
        setVisibility(View.INVISIBLE);
    }

}
