package com.limosys.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.limosys.linedisp.R;

/**
 * Created by Andrii on 4/7/2016.
 */
public class MapAndListButtonsPanel extends LinearLayout {

    public enum MapOrListPanelAction {
        MAP, LIST;
    }

    final Drawable mapActive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_map_black_24dp, null);
    final Drawable mapInactive= ResourcesCompat.getDrawable(getResources(), R.drawable.ic_map_white_24dp, null);
    final Drawable listActive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_black_24dp, null);
    final Drawable listInactive = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list_white_24dp, null);

    private MapOrListPanelAction lastAction = MapOrListPanelAction.MAP;

    public MapAndListOnClickCallback getCallbackl() {
        if(callback == null) return DEFAULT_CALLBACK;
        return callback;
    }

    public void setCallback(MapAndListOnClickCallback callbackl) {
        this.callback = callbackl;
    }

    private MapAndListOnClickCallback callback;

    private LinearLayout rootView;

    private Button mapButton;
    private Button listButton;

    public MapAndListButtonsPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = (LinearLayout) inflater.inflate(R.layout.map_and_list_buttons_panel, this);



        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                (int) displayMetrics.widthPixels/2,
                LayoutParams.MATCH_PARENT );


        mapButton = (Button) rootView.findViewById(R.id.driver_show_map_button);
        mapButton.setLayoutParams(p);

        listButton = (Button) rootView.findViewById(R.id.driver_show_list_button);
        listButton.setLayoutParams(p);

        mapButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastAction == MapOrListPanelAction.MAP) return;
                getCallbackl().MapAndListOnClick(MapOrListPanelAction.MAP);
                setMapActive();
            }
        });

        listButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastAction == MapOrListPanelAction.LIST) return;
                getCallbackl().MapAndListOnClick(MapOrListPanelAction.LIST);
                setListActive();
            }
        });

        setMapActive();

    }

    private void setListActive() {
        listButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_button_map_list_active));
        listButton.setCompoundDrawablesWithIntrinsicBounds(null, listActive, null, null);
        listButton.setTextColor(Color.parseColor("#000000"));
        mapButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_button_map_list_inactive));
        mapButton.setCompoundDrawablesWithIntrinsicBounds(null, mapInactive, null, null);
        mapButton.setTextColor(Color.parseColor("#FFFFFF"));
        lastAction = MapOrListPanelAction.LIST;
    }


    private void setMapActive() {
        mapButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_button_map_list_active));
        mapButton.setCompoundDrawablesWithIntrinsicBounds(null, mapActive, null, null);
        mapButton.setTextColor(Color.parseColor("#000000"));
        listButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_button_map_list_inactive));
        listButton.setCompoundDrawablesWithIntrinsicBounds(null, listInactive, null, null);
        listButton.setTextColor(Color.parseColor("#FFFFFF"));
        lastAction = MapOrListPanelAction.MAP;
    }

    public interface MapAndListOnClickCallback {
       void MapAndListOnClick(MapOrListPanelAction action);
    }

    private MapAndListOnClickCallback DEFAULT_CALLBACK = new MapAndListOnClickCallback(){
        @Override
        public void MapAndListOnClick(MapOrListPanelAction action) {

        }
    };

}
