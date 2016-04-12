package com.limosys.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.limosys.linedisp.R;

/**
 * Created by Andrii on 3/28/2016.
 */
public class ListOfActionsDialogView extends View {

    private Dialog dialog;

    public ActionDialogCallback getCallback() {
        if (callback == null) return DEFAULT_CALLBACK;
        return callback;
    }

    public void setCallback(ActionDialogCallback callback) {
        this.callback = callback;
    }

    private ActionDialogCallback callback;


    public ListOfActionsDialogView(Context context, ActionDialogCallback callback) {
        super(context);
        this.callback = callback;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        LinearLayout rootView = (LinearLayout) inflater.inflate(R.layout.list_of_actions_dialog_view, null);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        Button messageButton = (Button) rootView.findViewById(R.id.list_of_actions_message_button);
        messageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getCallback().onActionClicked(ActionDialogView.InfoDialogType.SEND_MESSAGE);
                dismissActivationDialog();
            }
        });

        Button descriptionButton = (Button) rootView.findViewById(R.id.list_of_actions_description_button);
        descriptionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getCallback().onActionClicked(ActionDialogView.InfoDialogType.DESCRIPTION);
                dismissActivationDialog();
            }
        });

        Button reqCarButton = (Button) rootView.findViewById(R.id.list_of_actions_cars_required_button);
        reqCarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getCallback().onActionClicked(ActionDialogView.InfoDialogType.REQ_CAR);
                dismissActivationDialog();
            }
        });

        Button amountOfMoneyButton = (Button) rootView.findViewById(R.id.list_of_actions_incentive_button);
        amountOfMoneyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getCallback().onActionClicked(ActionDialogView.InfoDialogType.AMOUNT_OF_MONEY);
                dismissActivationDialog();
            }
        });



        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(rootView);
        dialog.getWindow().setGravity(Gravity.RIGHT | Gravity.BOTTOM);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.width = (int) (320 * displayMetrics.density);
        wmlp.y = (int) (60 * displayMetrics.density);   //y position

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getCallback().onDialogCanceled();
            }
        });

    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public void dismissActivationDialog() {
        dialog.dismiss();
    }

    private ActionDialogCallback DEFAULT_CALLBACK = new ActionDialogCallback() {
        @Override
        public void onActionClicked(ActionDialogView.InfoDialogType type) {

        }

        @Override
        public void onDialogCanceled() {

        }
    };

    public interface ActionDialogCallback {
        void onActionClicked(ActionDialogView.InfoDialogType type);
        void onDialogCanceled();
    }

}
