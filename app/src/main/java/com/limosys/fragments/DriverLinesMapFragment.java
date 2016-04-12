package com.limosys.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.limosys.activities.DriverLineActivity;
import com.limosys.linedisp.DeviceUtils;
import com.limosys.linedisp.DriverLinesUtils;
import com.limosys.linedisp.LineObject;
import com.limosys.linedisp.LinesListAdapter;
import com.limosys.linedisp.R;
import com.limosys.views.MapAndListButtonsPanel;
import com.limosys.ws.obj.car.Ws_CarGps;
import com.limosys.ws.obj.displine.Ws_DispLineInfo;
import com.limosys.ws.obj.displine.Ws_GetDriverDispLinesResult;

import java.util.ArrayList;
import java.util.List;



public class DriverLinesMapFragment extends Fragment implements OnMapReadyCallback {

    private View rootView;

    private GoogleMap map;

    private MapView mapView;

    private Handler refreshLinesHandler;

    private boolean isFirstLoadOrAfterPause = true;

    private LinesListAdapter listAdapter;

    private List<LineObject> listOfLines;

    private FloatingActionButton fab;

    private LayoutInflater inflater;

    private ListView linesListView;


    public LinesListAdapter getListAdapter() {
        return listAdapter;
    }

    public void setListAdapter(LinesListAdapter listAdapter) {
        this.listAdapter = listAdapter;
        if(listAdapter != null && listAdapter.getCount() > 0) {
            linesListView.setAdapter(getListAdapter());
        }
    }


    public List<LineObject> getListOfLines() {
        return listOfLines;
    }

    public void setListOfLines(List<LineObject> listOfLines) {
        this.listOfLines = listOfLines;
    }

    public DriverLinesMapFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        isFirstLoadOrAfterPause = true;
        if(map != null && refreshLinesHandler != null){
            getLinesInfoFromServer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        if(refreshLinesHandler != null){
            refreshLinesHandler.removeCallbacksAndMessages(null);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshLinesHandler = new Handler();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        this.inflater = inflater;

        rootView = inflater.inflate(R.layout.fragment_driver_lines_map, container, false);

        rootView.setBackgroundColor(Color.WHITE);

        ((DriverLineActivity) getActivity()).showToolbar();

        linesListView = (ListView) rootView.findViewById(R.id.driver_lines_list_view);
        linesListView.setVisibility(View.GONE);


        mapView = (MapView) rootView.findViewById(R.id.driver_map_view);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);


        MapAndListButtonsPanel mapAndListButtonPanel = (MapAndListButtonsPanel) rootView.findViewById(R.id.map_and_list_buttons_panel);
        mapAndListButtonPanel.setCallback(new MapAndListButtonsPanel.MapAndListOnClickCallback() {

            @Override
            public void MapAndListOnClick(MapAndListButtonsPanel.MapOrListPanelAction action) {
                if (action == MapAndListButtonsPanel.MapOrListPanelAction.MAP || getListAdapter() == null || getListAdapter().getCount() == 0) {
                    mapView.setVisibility(View.VISIBLE);
                    linesListView.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                } else {
                    mapView.setVisibility(View.GONE);
                    linesListView.setVisibility(View.VISIBLE);
                    linesListView.setAdapter(listAdapter);
                    fab.setVisibility(View.GONE);
                }
            }
        });


        fab = (FloatingActionButton) rootView.findViewById(R.id.driver_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showAllMarkersOnScreen(true);

            }

        });
        return rootView;
    }

    private void showAllMarkersOnScreen(boolean force) {

        if(force == true || isFirstLoadOrAfterPause == true) {

            isFirstLoadOrAfterPause = false;

            if (map == null || listOfLines == null) return;

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LineObject line : listOfLines) {
                builder.include(line.getMarker().getPosition());
            }
            LatLngBounds bounds = builder.build();

            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.animateCamera(cu);
        }
    }


    private void getLinesInfoFromServer (){
        DriverLinesUtils.getLinesInfoForDriver(getContext(), DeviceUtils.getCompanyName(getContext()), DeviceUtils.getCarId(getContext()), "", new DriverLinesUtils.GetDispLinesForDriverCallback() {
            @Override
            public void onSuccess(Ws_GetDriverDispLinesResult res) {

                if (res != null) {

                    List<LineObject> lineObjects = new ArrayList<LineObject>();
                    List<Ws_CarGps> listOfGPS = new ArrayList<Ws_CarGps>();


                    for (int i = 0; i < res.getLineList().size(); i++) {

                        Ws_DispLineInfo line = res.getLineList().get(i);

                        lineObjects.add(new LineObject(line));

                        listOfGPS.add(new Ws_CarGps(DeviceUtils.getCompanyName(getContext()),String.valueOf(line.getId()), "", line.getAddress().getLat(), line.getAddress().getLon()));

                    }



//                    DriverLinesUtils.getETAToLines();

                    setListAdapter(new LinesListAdapter(getContext(),lineObjects));

                    addMarkersToLines(lineObjects, new OnLinesReadyForDisplayingCallback() {
                        @Override
                        public void onLinesReady(List<LineObject> listOfLinesWithMarkers) {

                            showLinesOnMap(listOfLinesWithMarkers);

                        }
                    });
                }

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

    private void showLinesOnMap(List<LineObject> listOfLinesWithMarkers) {

        for(int i = 0; i < listOfLinesWithMarkers.size(); i++){
            if(listOfLinesWithMarkers.get(i).getMarker() == null)
                listOfLinesWithMarkers.get(i).createAndShowMarkerOnMap(map);
        }

        showAllMarkersOnScreen(false);


        refreshLinesHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLinesInfoFromServer();
            }
        }, 10000);

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
                Toast.makeText(getActivity(), "id: " + marker.getId(), Toast.LENGTH_LONG).show();
                marker.remove();
                return true;
            }
        });

        getLinesInfoFromServer();

    }

    private LineObject getLineByIdFromLineList(int id){
        for(int i = 0; i < getListOfLines().size(); i++){
            LineObject line = getListOfLines().get(i);
           if(line.getLine().getId() == id) return line;
        }

        return null;
    }

    private void addMarkersToLines(List<LineObject> listOfLinesFromServer, OnLinesReadyForDisplayingCallback callback) {

        for(LineObject line : listOfLinesFromServer){

            LineObject drownLine = null;

            if(getListOfLines() != null)
                drownLine = getLineByIdFromLineList(line.getLine().getId());

            if(drownLine == null || !line.isLineAttrEqualseTo(drownLine.getLine())){
                BitmapDescriptor bitmap =  BitmapDescriptorFactory.fromBitmap(createBitmap(line));
                line.setMarkerOptions(new MarkerOptions().position(line.getPosition()).icon(bitmap));
                if(drownLine != null) drownLine.removeMarker();
                Log.i("AZ: ", "One More BITMAP");
            } else {
                Log.i("AZ: ", "REUSE More BITMAP");
                line.setMarker(drownLine.getMarker());
            }

        }

        setListOfLines(listOfLinesFromServer);
        callback.onLinesReady(getListOfLines());

    }

    private Bitmap createBitmap(LineObject lineObject) {
        View cluster = inflater.inflate(R.layout.driver_line_info_box,
                null);
        LinearLayout mainLayout = (LinearLayout) cluster.findViewById(R.id.line_info_box_layout);
        RelativeLayout carCircleLayout = (RelativeLayout) cluster.findViewById(R.id.car_line_circle);
        RelativeLayout moneyCircleLayout = (RelativeLayout) cluster.findViewById(R.id.money_line_circle);

        TextView titleTV = (TextView) cluster.findViewById(R.id.title_text);
        TextView moneyTV = (TextView) cluster.findViewById(R.id.money_text_view);
        TextView carTV = (TextView) cluster.findViewById(R.id.car_text_view);
        TextView commentText = (TextView) cluster.findViewById(R.id.comment_text);

        TextView etaTextView = (TextView) cluster.findViewById(R.id.info_box_eta_text_view);
        etaTextView.setBackgroundColor(Color.parseColor(lineObject.getLine().getColorRgb()));

        GradientDrawable shape = (GradientDrawable) mainLayout.getBackground();
        shape.setColor(Color.parseColor(lineObject.getLine().getColorRgb()));


        String lineDescriptionString = "";
        String[] descWordsArray = lineObject.getLine().getDesc().split(" ");
        if(descWordsArray.length > 3){
            for(String word : descWordsArray){
                if(lineDescriptionString.length() > 15){
                    lineDescriptionString += "...";
                    break;
                }
                if(!lineDescriptionString.equals("")) lineDescriptionString += " ";
                lineDescriptionString += word;
            }
        }

        if (lineObject.getLine().getIncentiveAmount() > 0 && !"C".equals(lineObject.getLine().getStatus())) {
            moneyTV.setText("$" + (int) lineObject.getLine().getIncentiveAmount());
            moneyTV.setTextColor(Color.parseColor(lineObject.getLine().getColorRgb()));
        } else {
            moneyCircleLayout.setVisibility(View.GONE);
        }

        if (lineObject.getLine().getCarReqCount() > 0 && !"C".equals(lineObject.getLine().getStatus())) {
            carTV.setText(String.valueOf(lineObject.getLine().getCarReqCount()));
            carTV.setTextColor(Color.parseColor(lineObject.getLine().getColorRgb()));
        } else {
            carCircleLayout.setVisibility(View.GONE);
        }

        if (lineObject.getLine().getDesc() != null) {
            commentText.setAlpha((float) 0.63);
            commentText.setText(lineDescriptionString);
        } else {
            commentText.setVisibility(View.GONE);
        }

        titleTV.setText(lineObject.getLine().getName());

        cluster.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        cluster.layout(0, 0, cluster.getMeasuredWidth(), cluster.getMeasuredHeight());

        final Bitmap clusterBitmap = Bitmap.createBitmap(cluster.getMeasuredWidth(),
                cluster.getMeasuredHeight(), Bitmap.Config.ARGB_4444);

        Canvas canvas = new Canvas(clusterBitmap);
        cluster.draw(canvas);

        return clusterBitmap;
    }

    private interface OnLinesReadyForDisplayingCallback {
        void onLinesReady(List<LineObject> listOfLinesWithMarkers);
    }

}

