package com.limosys.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.limosys.linedisp.R;

/**
 * Created by Andrii on 3/29/2016.
 */
public class ActionDialogView extends View {

    public enum InfoDialogType {
        REQ_CAR, AMOUNT_OF_MONEY, SEND_MESSAGE, DESCRIPTION;
    }
    private static int MAX_REQ_CAR_NUMBER = 999;
    private static double MAX_AMOUNT_OF_MONEY_NUMBER = 999;
    private OnInfoDialogCallback callback;

    private LinearLayout rootView;
    private TextView dialogTitle;
    private Dialog dialog;

    private EditText messageEditText;
    private EditText moneyEditText;
    private EditText reqCarTextEdit;
    private EditText descriptionTextEdit;

    private OnInfoDialogCallback DEFAULT_CALLBACK = new OnInfoDialogCallback() {
        @Override
        public void onUpdateButtonClicked(InfoDialogType type, String value) {

        }
        @Override
        public void onCancelInfoDialog() {

        }
    };

    public OnInfoDialogCallback getCallback() {
        if(callback == null) return DEFAULT_CALLBACK;
        return callback;
    }

    public void setCallback(OnInfoDialogCallback callback) {
        this.callback = callback;
    }

    public ActionDialogView(Context context, InfoDialogType dialogViewType, String carsRequired, String incentive, String msg) {
        super(context);
        init(dialogViewType,carsRequired,incentive,msg);
    }

    private void init(final InfoDialogType type, String numberOfCars, String amountOfMoney, String msg) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        rootView = (LinearLayout) inflater.inflate(R.layout.action_dialog_view, null);
        dialogTitle = (TextView) rootView.findViewById(R.id.action_dialog_title);

        RelativeLayout actionDialogMessageLayout = (RelativeLayout) rootView.findViewById(R.id.action_dialog_message_layout);
        RelativeLayout actionDialogDescriptionLayout = (RelativeLayout) rootView.findViewById(R.id.action_dialog_description_layout);
        RelativeLayout actionDialogCarsRequiredLayout = (RelativeLayout) rootView.findViewById(R.id.action_dialog_cars_required_layout);
        RelativeLayout actionDialogIncentiveLayout = (RelativeLayout) rootView.findViewById(R.id.action_dialog_incentive_layout);



        messageEditText = (EditText) rootView.findViewById(R.id.message_edit_text);
        moneyEditText = (EditText) rootView.findViewById(R.id.incentive_edit_text);
        reqCarTextEdit = (EditText) rootView.findViewById(R.id.cars_required_edit_text);
        descriptionTextEdit = (EditText) rootView.findViewById(R.id.description_edit_text);

        if (type == InfoDialogType.SEND_MESSAGE) {
            actionDialogMessageLayout.setVisibility(View.VISIBLE);
            dialogTitle.setText("Message for drivers");
        }

        if (type == InfoDialogType.DESCRIPTION) {
            actionDialogDescriptionLayout.setVisibility(View.VISIBLE);
            descriptionTextEdit.setText(msg);
            dialogTitle.setText("Line description");
        }

        if (type == InfoDialogType.AMOUNT_OF_MONEY) {
            moneyEditText.setText(amountOfMoney);
            actionDialogIncentiveLayout.setVisibility(View.VISIBLE);
            dialogTitle.setText("Set incentive");
        }

        if (type == InfoDialogType.REQ_CAR) {
            reqCarTextEdit.setText(numberOfCars);
            actionDialogCarsRequiredLayout.setVisibility(View.VISIBLE);
            dialogTitle.setText("Cars required");
        }

        Button updateButton = (Button) rootView.findViewById(R.id.action_dialog_update_button);
        updateButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                String error = validateField(type);

                if (error != null) {

                    Snackbar.make(rootView, error, Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_light))
                            .show();

                    return;
                }

                if(type == InfoDialogType.SEND_MESSAGE){
                        getCallback().onUpdateButtonClicked(type, messageEditText.getText().toString());
                }

                if(type == InfoDialogType.DESCRIPTION){
                        getCallback().onUpdateButtonClicked(type, descriptionTextEdit.getText().toString());
                }

                if(type == InfoDialogType.REQ_CAR){
                     getCallback().onUpdateButtonClicked(type, reqCarTextEdit.getText().toString());
                }

                if(type == InfoDialogType.AMOUNT_OF_MONEY){
                    getCallback().onUpdateButtonClicked(type, moneyEditText.getText().toString());
                }

                dismissActivationDialog();
            }
        });


        Button cancelButton = (Button) rootView.findViewById(R.id.message_dialog_cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissActivationDialog();
            }
        });

        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(rootView);


        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getCallback().onCancelInfoDialog();
            }
        });
    }

    private String validateField(InfoDialogType type) {

        // Message validations
        if(type == InfoDialogType.SEND_MESSAGE){
            if(messageEditText != null && messageEditText.getText() != null && messageEditText.getText().toString().length() == 0){
               return "Message can not be empty.";
            }
        }

        // Description Validation
        if(type == InfoDialogType.DESCRIPTION){
            if(descriptionTextEdit != null && descriptionTextEdit.getText() != null && descriptionTextEdit.getText().toString().length() == 0) {
                return "Description can not be empty.";
            }
        }

        // Require Cars Validation
        if(type == InfoDialogType.REQ_CAR){
            if(reqCarTextEdit != null && reqCarTextEdit.getText() != null) {
                if(reqCarTextEdit.getText().toString().length() <= 0)
                    return "Required cars can not be empty";

                if(reqCarTextEdit.getText().toString().contains("[^0-9]"))
                    return "You can enter only numbers";

                if(reqCarTextEdit.getText().toString().length() > String.valueOf(MAX_REQ_CAR_NUMBER).length() ||
                        Integer.valueOf(reqCarTextEdit.getText().toString()) > MAX_REQ_CAR_NUMBER)
                    return "Max number is " + MAX_REQ_CAR_NUMBER;

            }
        }

        // Amount of money Validation
        if(type == InfoDialogType.AMOUNT_OF_MONEY){
            if(moneyEditText != null && moneyEditText.getText() != null) {
                if(moneyEditText.getText().toString().length() <= 0)
                    return "Amount of money can not be empty";

                if(moneyEditText.getText().toString().contains("[^0-9]"))
                    return "You can enter only numbers";

                if(moneyEditText.getText().toString().length() > String.valueOf(MAX_AMOUNT_OF_MONEY_NUMBER).length() ||
                        Double.valueOf(moneyEditText.getText().toString()) > MAX_AMOUNT_OF_MONEY_NUMBER)
                    return "Max number is " + MAX_AMOUNT_OF_MONEY_NUMBER;

            }
        }

        return null;
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismissActivationDialog() {
        dialog.dismiss();
    }




    public interface OnInfoDialogCallback {
        void onUpdateButtonClicked(InfoDialogType type, String value);
        void onCancelInfoDialog();
    }



}
