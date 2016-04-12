package com.limosys.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.limosys.activities.DispLineActivity;
import com.limosys.activities.DriverLineActivity;
import com.limosys.linedisp.DeviceUtils;
import com.limosys.linedisp.DriverLinesUtils;
import com.limosys.linedisp.R;
import com.limosys.ws.obj.displine.Ws_GetDispLineParamResult;

public class DriverLogInFragment extends Fragment {

    View rootView;

    public DriverLogInFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((DriverLineActivity) getActivity()).hideTollBar();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_driver_log_in, container, false);

        final EditText companyNameEditText = (EditText) rootView.findViewById(R.id.log_in_driver_company_name);

        final EditText carIdEditText = (EditText) rootView.findViewById(R.id.log_in_driver_plate_number);

        Button loginButton = (Button) rootView.findViewById(R.id.log_in_driver_button);
        loginButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {


                String error = null;

                if (companyNameEditText.getText() == null || companyNameEditText.getText().toString().length() <= 0)
                    error = "Company Name can not be empty";
                if (error == null && (carIdEditText.getText() == null || carIdEditText.getText().toString().length() <= 0))
                    error = "Car id can not be empty";

                if (error != null) {
                    Snackbar.make(rootView, error, Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light))
                            .show();
                    ((DriverLineActivity) getActivity()).hideKeyBoard();
                    return;
                }


                DriverLinesUtils.driverLineLogin(getContext(), companyNameEditText.getText().toString(), carIdEditText.getText().toString(), "", new DriverLinesUtils.GetDispLineParamCallback() {
                    @Override
                    public void onSuccess(Ws_GetDispLineParamResult res) {

                        DeviceUtils.setCompanyName(getContext(),companyNameEditText.getText().toString());
                        DeviceUtils.setCarId(getContext(), carIdEditText.getText().toString());

                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,
                                R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.driver_fragments_place,
                                new DriverLinesMapFragment(), new DriverLinesMapFragment().getTag()).commit();
                        ((DriverLineActivity) getActivity()).hideKeyBoard();
                    }

                    @Override
                    public void onError(String error) {
                        Snackbar.make(rootView, error, Snackbar.LENGTH_LONG)
                                .setAction("CLOSE", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                })
                                .setActionTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light))
                                .show();

                        ((DriverLineActivity) getActivity()).hideKeyBoard();
                    }
                });



            }
        });



        return rootView;
    }
}
