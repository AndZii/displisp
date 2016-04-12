package com.limosys.views;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

/**
 * Created by Andrii on 4/7/2016.
 */
class StatusButton extends Button {

    String statusColor;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    String statusCode;

    public StatusButton(Context context, String statusColor, String statusCode) {
        super(context);
        this.statusCode = statusCode;
        this.statusColor = statusColor;
    }

    public StatusButton setActive(){

        this.setBackgroundColor(Color.parseColor(statusColor));
        this.setTextColor(Color.parseColor("#f2f2f2"));

        return this;
    }

    public void setInActive(){

        this.setBackgroundColor(Color.parseColor("#f2f2f2"));
        this.setTextColor(Color.parseColor(statusColor));
    }
}
