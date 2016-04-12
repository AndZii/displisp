package com.limosys.linedisp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.limosys.ws.obj.displine.Ws_DispLineInfo;
import com.limosys.ws.obj.displine.Ws_GetLineInfoDispatcherResult;

/**
 * Created by Andrii on 4/7/2016.
 */
public class LineObject {

    private Marker marker;

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void removeMarker(){
        getMarker().remove();
    }

    public Marker getMarker() {
        return marker;
    }

    public void createAndShowMarkerOnMap(GoogleMap map) {
        this.marker = map.addMarker(getMarkerOptions());
    }

    private MarkerOptions markerOptions;

    public MarkerOptions getMarkerOptions() {
        return markerOptions;
    }

    public void setMarkerOptions(MarkerOptions markerOptions){
        this.markerOptions = markerOptions;
    }

    public Ws_DispLineInfo getLine() {
        return line;
    }

    public void setLine(Ws_DispLineInfo line) {
        this.line = line;
    }

    private Ws_DispLineInfo line;

    public LatLng getPosition() {
        if(position == null) position = new LatLng(getLine().getAddress().getLat(), getLine().getAddress().getLon());
        return position;
    }


    public LineObject (Ws_DispLineInfo line) {
        this.line = line;
    }

    private LatLng position;

    public boolean isLineAttrEqualseTo(Ws_DispLineInfo otherLine){
        if(line.getCarReqCount() != otherLine.getCarReqCount()) return false;
        if(line.getIncentiveAmount() != otherLine.getIncentiveAmount()) return false;
        if(!line.getDesc().equals(otherLine.getDesc())) return false;
        if(!line.getStatus().equals(otherLine.getStatus())) return false;
        return true;
    }
}
