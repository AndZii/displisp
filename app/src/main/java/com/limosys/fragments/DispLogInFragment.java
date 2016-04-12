package com.limosys.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.limosys.linedisp.DeviceUtils;
import com.limosys.activities.DispLineActivity;
import com.limosys.linedisp.DispLineUtils;
import com.limosys.linedisp.R;
import com.limosys.ws.obj.displine.Ws_DispLoginResult;
import com.limosys.ws.obj.displine.Ws_GetLinesByEmployeeResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DispLogInFragment extends Fragment {

    private HashMap<String, Integer> defaultLocation;
    private MaterialSpinner locationSpinner;
    private List<String> lineNamesArray;
    private EditText userId;

    private View rootView;

    public HashMap<String, Integer> getLocationHashMap() {
        return locationHashMap;
    }

    public void setLocationHashMap(HashMap<String, Integer> locationHashMap) {

        if (locationHashMap != null) {
            locationSpinner.setClickable(locationHashMap.size() > 1);
            locationSpinner.setArrowColor(locationHashMap.size() > 1 ? ContextCompat.getColor(getActivity(), R.color.white) : ContextCompat.getColor(getActivity(), R.color.transperent));
        }


        lineNamesArray = new ArrayList<String>();

        for (String name : locationHashMap.keySet()) {
            lineNamesArray.add(name);
        }

        locationSpinner.setItems(lineNamesArray);


        this.locationHashMap = locationHashMap;
    }

    private HashMap<String, Integer> locationHashMap;


    public DispLogInFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_disp_log_in, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DispLineActivity) getActivity()).hideKeyBoard();
            }
        });

        userId = (EditText) rootView.findViewById(R.id.log_in_driver_company_name);

        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                getLinesFromServer(s.toString());

            }
        });



    final EditText password = (EditText) rootView.findViewById(R.id.log_in_driver_plate_number);

    locationSpinner=(MaterialSpinner)rootView.findViewById(R.id.log_in_location_spinner);
    locationSpinner.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.spinner_back_ground));
    locationSpinner.setShadowLayer(0, 0, 0, R.color.transperent);



    defaultLocation=new HashMap<String, Integer>();
    defaultLocation.put("Locations",0);

    setLocationHashMap(defaultLocation);

    Button logInButton = (Button) rootView.findViewById(R.id.log_in_button);
    logInButton.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View v){

        String error = null;

        if (userId.getText() == null || userId.getText().toString().length() <= 0 || !userId.getText().toString().matches("[0-9]+"))
            error = "Wrong user id";
        if (error == null && (password.getText() == null || password.getText().toString().length() <= 0))
            error = "Wrong password";
        if (error == null && (defaultLocation.equals(getLocationHashMap()) || lineNamesArray.get(locationSpinner.getSelectedIndex()) == null || "".equals(lineNamesArray.get(locationSpinner.getSelectedIndex()))))
            error = "No line id";

        if (error != null) {
            Snackbar.make(rootView, error, Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    })
                    .setActionTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_red_light))
                    .show();
            ((DispLineActivity) getActivity()).hideKeyBoard();
            return;
        }

        final int intUserId = Integer.valueOf(userId.getText().toString());
        final String userPassword = password.getText().toString();
        final int intLineId = locationHashMap.get(lineNamesArray.get(locationSpinner.getSelectedIndex()));

        ((DispLineActivity) getActivity()).hideKeyBoard();

        DispLineUtils.dispLineLogin(intUserId, userPassword,
                DeviceUtils.getDeviceId(getActivity()), intLineId, new DispLineUtils.dispLineLoginCallback() {

                    @Override
                    public void onSuccess(Ws_DispLoginResult res) {
                        DeviceUtils.setEmployeeId(getActivity(), String.valueOf(intUserId));
                        DeviceUtils.setLineId(getActivity(), String.valueOf(intLineId));

                        ((DispLineActivity) getActivity()).hideKeyBoard();

                        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter,
                                R.anim.exit, R.anim.pop_enter, R.anim.pop_exit).replace(R.id.fragment_place,
                                new DispLineMapFragment(), new DispLineMapFragment().getTag()).commit();

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

                    }
                });

    }
    }

    );

    return rootView;

}


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

public interface OnFragmentInteractionListener {
    void onFragmentInteraction(Uri uri);

}

    @Override
    public void onResume() {
        super.onResume();
        ((DispLineActivity) getActivity()).hideTollBar();
    }

    private void getLinesFromServer(String s) {
        if (s.toString().length() > 8 || "".equals(userId.getText().toString()) || s.matches(".*[a-zA-Z]+.*")) {
            setLocationHashMap(defaultLocation);
            return;
        }


        DispLineUtils.getLinesByEmployee(Integer.valueOf(s), new DispLineUtils.GetLinesByEmployeeId()

                {
                    @Override
                    public void onSuccess(Ws_GetLinesByEmployeeResult res) {

                        HashMap<String, Integer> tempLocationHashMap = new HashMap<String, Integer>();

                        for (Ws_GetLinesByEmployeeResult.Ws_DispLineShortInfo lineInfo : res.getDispLineInfo()) {
                            tempLocationHashMap.put(lineInfo.getName(), lineInfo.getLineId());
                        }

                        if (tempLocationHashMap.size() > 0) {
                            setLocationHashMap(tempLocationHashMap);
                        } else {
                            setLocationHashMap(defaultLocation);
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                }

        );
    }

}
