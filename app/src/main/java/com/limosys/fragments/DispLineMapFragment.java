package com.limosys.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.limosys.linedisp.DeviceUtils;
import com.limosys.activities.DispLineActivity;
import com.limosys.linedisp.DispLineUtils;
import com.limosys.linedisp.R;
import com.limosys.views.ActionDialogView;
import com.limosys.views.DispLineInfoBox;
import com.limosys.views.DispLineFAB;
import com.limosys.views.StatusButtonsScrollView;
import com.limosys.ws.obj.displine.Ws_GetLineInfoDispatcherResult;


public class DispLineMapFragment extends Fragment implements OnMapReadyCallback {


    private View rootView;
    private GoogleMap map;
    private MapView mapView;
    private Marker currentMarker;
    private MaterialSheetFab materialSheetFab;

    public DispLineInfoBox getCurrentInfoBox() {
        return currentInfoBox;
    }

    public void setCurrentInfoBox(Ws_GetLineInfoDispatcherResult currentLine) {
        this.currentInfoBox = showLineOnMap(currentLine);
    }

    private DispLineInfoBox currentInfoBox;
    private StatusButtonsScrollView statusButtonScrollViewPanel;
    private boolean isMapHasOnCameraChangeLiistener = false;

    public DispLineMapFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_disp_line_map, container,
                false);

        statusButtonScrollViewPanel = (StatusButtonsScrollView) rootView.findViewById(R.id.status_buttons_scroll_view);

        DispLineFAB fab = (DispLineFAB) rootView.findViewById(R.id.fab);
        View sheetView = rootView.findViewById(R.id.fab_sheet);
        View overlay = rootView.findViewById(R.id.dim_overlay);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                Color.WHITE, Color.GRAY);

        Button messageButton = (Button) rootView.findViewById(R.id.list_of_actions_message_button);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();
                openActionDialogView(ActionDialogView.InfoDialogType.SEND_MESSAGE);

            }
        });

        Button descriptionButton = (Button) rootView.findViewById(R.id.list_of_actions_description_button);
        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();
                openActionDialogView(ActionDialogView.InfoDialogType.DESCRIPTION);
            }
        });

        Button reqCarButton = (Button) rootView.findViewById(R.id.list_of_actions_cars_required_button);
        reqCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();
                openActionDialogView(ActionDialogView.InfoDialogType.REQ_CAR);
            }
        });

        Button amountOfMoneyButton = (Button) rootView.findViewById(R.id.list_of_actions_incentive_button);
        amountOfMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();
                openActionDialogView(ActionDialogView.InfoDialogType.AMOUNT_OF_MONEY);
            }
        });


        statusButtonScrollViewPanel.setCallback(new StatusButtonsScrollView.StatusButtonDialogCallback() {
            @Override
            public void onStatusButtonClicked(String statusCode, String color) {
                DispLineUtils.setLineParametersDispatcher(Integer.valueOf(DeviceUtils.getEmployeeId(getActivity())),
                        DeviceUtils.getDeviceId(getActivity()), Integer.valueOf(DeviceUtils.getLineId(getActivity())),
                        statusCode, getCurrentInfoBox().getLineCarsRequired(), getCurrentInfoBox().getLineIncentive(), getCurrentInfoBox().getLineDescription(),
                        new DispLineUtils.setLineParametersDispatcherCallback() {
                            @Override
                            public void onSuccess(Ws_GetLineInfoDispatcherResult res) {
                                if (res != null && res.getStatus() != null) {
                                    getCurrentInfoBox().setLine(res, true);
                                    statusButtonScrollViewPanel.setCurrentStatus(res.getStatus());
                                }
                            }

                            @Override
                            public void onError(String error) {
                                Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });


        mapView = (MapView) rootView.findViewById(R.id.mapview);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

        ((DispLineActivity) getActivity()).showToolbar();


        if (statusButtonScrollViewPanel != null) {
            statusButtonScrollViewPanel.getButtonsFromServer();
        }

        if (map != null) {
            getLineInfoFromServer();
        }


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (googleMap == null) return;
        MapsInitializer.initialize(getContext());
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(true);
        map.setBuildingsEnabled(true);
        map.setIndoorEnabled(false);
        map.setTrafficEnabled(false);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarker.showInfoWindow();
                return true;
            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker != null)
                    currentMarker.showInfoWindow();
            }
        });

        getLineInfoFromServer();
    }


    private DispLineInfoBox showLineOnMap(Ws_GetLineInfoDispatcherResult line) {

        if (line == null && line.getName() == null) ((DispLineActivity) getActivity()).logOut();

        map.clear();

        DispLineInfoBox infoBox = new DispLineInfoBox(getActivity(), line);

        final LatLng location = new LatLng(line.getAddress().getLat(), line.getAddress().getLon());

        map.setInfoWindowAdapter(infoBox);
        Marker marker = map.addMarker(new MarkerOptions()
                .position(location).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_24dp)));
        currentMarker = marker;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12));
        marker.showInfoWindow();

        if (!isMapHasOnCameraChangeLiistener) {
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    isMapHasOnCameraChangeLiistener = true;
                    LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
                    if (!bounds.contains(location))
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 12));

                }
            });
        }

        return infoBox;
    }

    private void getLineInfoFromServer() {
        DispLineUtils.getLineInfoDispatcher(Integer.valueOf(DeviceUtils.getEmployeeId(getActivity())),
                DeviceUtils.getDeviceId(getActivity()), Integer.valueOf(DeviceUtils.getLineId(getActivity())), new DispLineUtils.GetLineInfoDispatcher() {
                    @Override
                    public void onSuccess(Ws_GetLineInfoDispatcherResult res) {

                        if (res != null) {
                            setCurrentInfoBox(res);
                            if (statusButtonScrollViewPanel.getCurrentStatus() == null)
                                statusButtonScrollViewPanel.setCurrentStatus(res.getStatus());
                        } else {
                            Toast.makeText(getActivity(), "Error in success", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void openActionDialogView(ActionDialogView.InfoDialogType type) {

        if (getCurrentInfoBox().getLineStatus().equals("C")) {
            Toast.makeText(getActivity(), "The line is curently closed.", Toast.LENGTH_LONG).show();
            return;
        }

        ActionDialogView dialog = new ActionDialogView(getContext(), type, String.valueOf(getCurrentInfoBox().getLineCarsRequired()), String.valueOf(getCurrentInfoBox().getLineIncentive()), getCurrentInfoBox().getLineDescription());

        dialog.setCallback(new ActionDialogView.OnInfoDialogCallback() {
            @Override
            public void onUpdateButtonClicked(ActionDialogView.InfoDialogType type, String value) {


                if (type == ActionDialogView.InfoDialogType.REQ_CAR) {
                    DispLineUtils.setLineParametersDispatcher(Integer.valueOf(DeviceUtils.getEmployeeId(getActivity())),
                            DeviceUtils.getDeviceId(getActivity()), Integer.valueOf(DeviceUtils.getLineId(getActivity())),
                            getCurrentInfoBox().getLineStatus(), Integer.valueOf(value), getCurrentInfoBox().getLineIncentive(), getCurrentInfoBox().getLineDescription(),
                            new DispLineUtils.setLineParametersDispatcherCallback() {
                                @Override
                                public void onSuccess(Ws_GetLineInfoDispatcherResult res) {
                                    if (res != null) {
                                        getCurrentInfoBox().setLine(res, true);
                                    }
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                }

                if (type == ActionDialogView.InfoDialogType.AMOUNT_OF_MONEY) {
                    DispLineUtils.setLineParametersDispatcher(Integer.valueOf(DeviceUtils.getEmployeeId(getActivity())),
                            DeviceUtils.getDeviceId(getActivity()), Integer.valueOf(DeviceUtils.getLineId(getActivity())),
                            getCurrentInfoBox().getLineStatus(), getCurrentInfoBox().getLineCarsRequired(), Double.valueOf(value), getCurrentInfoBox().getLineDescription(),
                            new DispLineUtils.setLineParametersDispatcherCallback() {
                                @Override
                                public void onSuccess(Ws_GetLineInfoDispatcherResult res) {
                                    if (res != null) {
                                        getCurrentInfoBox().setLine(res, true);
                                    }
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                }

                if (type == ActionDialogView.InfoDialogType.DESCRIPTION) {
                    DispLineUtils.setLineParametersDispatcher(Integer.valueOf(DeviceUtils.getEmployeeId(getActivity())),
                            DeviceUtils.getDeviceId(getActivity()), Integer.valueOf(DeviceUtils.getLineId(getActivity())),
                            getCurrentInfoBox().getLineStatus(), getCurrentInfoBox().getLineCarsRequired(), getCurrentInfoBox().getLineIncentive(), value,
                            new DispLineUtils.setLineParametersDispatcherCallback() {
                                @Override
                                public void onSuccess(Ws_GetLineInfoDispatcherResult res) {
                                    if (res != null) {
                                        getCurrentInfoBox().setLine(res, true);
                                    }
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
                }


            }

            @Override
            public void onCancelInfoDialog() {
                ((DispLineActivity) getActivity()).hideKeyBoard();
            }
        });
        dialog.show();


    }
}
