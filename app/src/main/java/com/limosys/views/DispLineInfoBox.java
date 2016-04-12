package com.limosys.views;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.limosys.linedisp.R;
import com.limosys.ws.obj.displine.Ws_GetLineInfoDispatcherResult;

/**
 * Created by Andrii on 4/7/2016.
 */
public class DispLineInfoBox implements GoogleMap.InfoWindowAdapter {

    private View rootView;
    private LinearLayout mainLayout;

    private Marker marker = null;
    private String statusColor;
    private String title;
    private int lineId;
    private String description;
    private int reqCars;
    private int incentive;
    private String status;

    private TextView carTV;
    private TextView commentText;
    private TextView moneyTV;

    private Ws_GetLineInfoDispatcherResult line;

    public DispLineInfoBox(Activity activity, Ws_GetLineInfoDispatcherResult line) {
        setLine(line, false);
        rootView = activity.getLayoutInflater().inflate(R.layout.info_box_line, null);
    }

    public void setLine(Ws_GetLineInfoDispatcherResult line, boolean refresh) {
        this.lineId = line.getId();
        this.statusColor = line.getColorRgb();
        this.title = line.getName();
        this.description = line.getDesc();
        this.reqCars = line.getReqCarsCounter();
        this.incentive = (int) line.getIncentiveAmount();
        this.status = line.getStatus();
        this.line = line;

        if (refresh) {
            getInfoWindow(marker);
            refreshInfoBox();
        }
    }

    public double getLineIncentive() {
        return line.getIncentiveAmount();
    }

    public int getLineCarsRequired() {
        return line.getReqCarsCounter();
    }

    public String getLineDescription() {
        return line.getDesc();
    }

    public String getLineStatus() {
        return line.getStatus();
    }

    private String getStatusColor() {
        if (statusColor == null) statusColor = "#ffffff";
        return statusColor;
    }

    private void changeColor(String colorRGB) {
        GradientDrawable shape = (GradientDrawable) mainLayout.getBackground();
        shape.setColor(Color.parseColor(colorRGB));
        moneyTV.setTextColor(Color.parseColor(getStatusColor()));
        carTV.setTextColor(Color.parseColor(getStatusColor()));
    }
    
    @Override
    public View getInfoWindow(Marker marker) {
        this.marker = marker;

        mainLayout = (LinearLayout) rootView.findViewById(R.id.line_info_box_layout);
        RelativeLayout carCircleLayout = (RelativeLayout) rootView.findViewById(R.id.car_line_circle);
        RelativeLayout moneyCircleLayout = (RelativeLayout) rootView.findViewById(R.id.money_line_circle);

        TextView titleTV = (TextView) rootView.findViewById(R.id.title_text);
        moneyTV = (TextView) rootView.findViewById(R.id.money_text_view);
        carTV = (TextView) rootView.findViewById(R.id.car_text_view);
        commentText = (TextView) rootView.findViewById(R.id.comment_text);

        changeColor(getStatusColor());

        if (incentive > 0 && !"C".equals(line.getStatus())) {
            moneyTV.setText("$" + incentive);
            moneyCircleLayout.setVisibility(View.VISIBLE);
        } else {
            moneyCircleLayout.setVisibility(View.GONE);
        }

        if (reqCars > 0 && !"C".equals(line.getStatus())) {
            carTV.setText(String.valueOf(reqCars));
            carCircleLayout.setVisibility(View.VISIBLE);
        } else {
            carCircleLayout.setVisibility(View.GONE);
        }


        if (description != null) {
            commentText.setAlpha((float) 0.63);
            commentText.setText(description);
        } else {
            commentText.setVisibility(View.GONE);
        }

        titleTV.setText(title);

        return rootView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void refreshInfoBox() {
        if (marker != null && marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
            marker.showInfoWindow();
        }
    }
}

