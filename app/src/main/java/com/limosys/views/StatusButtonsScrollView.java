package com.limosys.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.limosys.linedisp.DeviceUtils;
import com.limosys.linedisp.DispLineUtils;
import com.limosys.linedisp.R;
import com.limosys.ws.obj.displine.Ws_DispLineStatusInfo;
import com.limosys.ws.obj.displine.Ws_GetLineStatusListResult;

import java.util.HashMap;

/**
 * Created by Andrii on 3/28/2016.
 */
public class StatusButtonsScrollView extends HorizontalScrollView implements Button.OnClickListener {

    private HorizontalScrollView rootView;

    private StatusButton lastTouchedButton;

    private StatusButtonDialogCallback callback;

    private LinearLayout horizontalButtonsPanel;

    private HashMap<String, StatusButton> statusButtons;

    public String getCurrentStatus() {
        return currentStatus;
    }

    private boolean isWaitingForServerResponseForStatusButtons = false;

    public void setCurrentStatus(String currentStatus) {
        if(statusButtons != null && statusButtons.size() > 0) {
            if(statusButtons.containsKey(currentStatus)) lastTouchedButton = statusButtons.get(currentStatus).setActive();
        }
        this.currentStatus = currentStatus;
    }

    private String currentStatus;
    public StatusButtonDialogCallback getCallback() {
        if(callback == null) return DEFAULT_CALLBACK;
        return callback;
    }

    public void setCallback(StatusButtonDialogCallback callback) {
        this.callback = callback;
    }

    public StatusButtonsScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = (HorizontalScrollView) inflater.inflate(R.layout.status_buttons_scroll_view, this);
        horizontalButtonsPanel = (LinearLayout) rootView.findViewById(R.id.buttons_horizontal_panel);
        getButtonsFromServer();
    }

    private StatusButton addtatusButton(String title, String color, String statusCode) {
        StatusButton button = new StatusButton(getContext(), color, statusCode);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        LayoutParams p = new LayoutParams(
                (int) displayMetrics.widthPixels/4,
                (int)(60.0 * displayMetrics.density));
        button.setTextColor(Color.parseColor(color));
        button.setOnClickListener(this);
        button.setTextSize(12);
        button.setLayoutParams(p);
        button.setBackgroundColor(Color.parseColor("#f2f2f2"));
        button.setText(title);
        horizontalButtonsPanel.addView(button);

        return button;
    }

    @Override
    public void onClick(final View view) {

        if ((lastTouchedButton != null && view.equals(lastTouchedButton)))
            return;

        new AlertDialog.Builder(getContext())
                .setTitle("Confirmation")
                .setMessage(!"C".equals(((StatusButton)view).getStatusCode()) ? "Do You want to change status of the line to " + ((StatusButton)view).getText().toString().toLowerCase() +"?" : "Do You want to close the line?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (view instanceof StatusButton) {

                            if (lastTouchedButton != null) {
                                lastTouchedButton.setInActive();
                            }

                            ((StatusButton) view).setActive();
                            getCallback().onStatusButtonClicked(((StatusButton) view).getStatusCode(), ((StatusButton) view).statusColor);

                            lastTouchedButton = ((StatusButton) view);
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public interface StatusButtonDialogCallback {
        void onStatusButtonClicked(String statusCode, String color);
    }

    private StatusButtonDialogCallback DEFAULT_CALLBACK = new StatusButtonDialogCallback() {
        @Override
        public void onStatusButtonClicked(String statusCode, String color) {
        }
    };

    public void getButtonsFromServer(){

        if((isWaitingForServerResponseForStatusButtons || (statusButtons != null && statusButtons.size() > 0))) return;
        isWaitingForServerResponseForStatusButtons = true;

        DispLineUtils.getLineStatusList(Integer.valueOf(DeviceUtils.getEmployeeId(getContext())),
                DeviceUtils.getDeviceId(getContext()), Integer.valueOf(DeviceUtils.getLineId(getContext())), new DispLineUtils.getLineStatusListCallback() {
                    @Override
                    public void onSuccess(Ws_GetLineStatusListResult res) {
                        isWaitingForServerResponseForStatusButtons = false;

                        statusButtons = new HashMap<String, StatusButton>();

                        for (Ws_DispLineStatusInfo lineStatusInfo : res.getStatusList()) {
                            StatusButton button = addtatusButton(lineStatusInfo.getStatusDesc(),lineStatusInfo.getColorRGB(), lineStatusInfo.getStatusCode());
                            statusButtons.put(button.statusCode, button);
                        }


                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
                        isWaitingForServerResponseForStatusButtons = false;

                    }
                });
    }

}
