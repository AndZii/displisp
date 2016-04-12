package com.limosys.linedisp;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Andrii on 4/7/2016.
 */
public class LinesListAdapter extends BaseAdapter {

    View lastSelectedView;

    private List<LineObject> listOfLines;
    private Context context;

    public OnLineSelectedCallback getCallback() {
        if(callback == null) return DEFAULT_CALLBACK;
        return callback;
    }

    public void setCallback(OnLineSelectedCallback callback) {
        this.callback = callback;
    }

    private OnLineSelectedCallback callback;

    public LinesListAdapter(Context context,
                       List<LineObject> listOfLines) {

        super();
        this.context = context;
        this.listOfLines = listOfLines;

    }

    @Override
    public int getCount() {
        return listOfLines.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return listOfLines.get(position).getLine().getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.line_list_adapter_item, null);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View currentlySelectedView) {

                if(lastSelectedView != null){
                    setItemInactive(lastSelectedView, Color.parseColor(listOfLines.get(position).getLine().getColorRgb()));
                }

                // TODO: Need to check selected or no!!!
                if(lastSelectedView != currentlySelectedView) {
                    setItemActive(currentlySelectedView);
                }

                lastSelectedView = currentlySelectedView;

                getCallback().onLineSelected(listOfLines.get(position).getLine().getId());

            }
        });

        convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        TextView lineName = (TextView) convertView.findViewById(R.id.line_list_item_name);
        TextView lineDescription = (TextView) convertView.findViewById(R.id.line_list_item_description);
        TextView lineBonus = (TextView) convertView.findViewById(R.id.line_list_item_bonus);
        TextView lineCarsRequired = (TextView) convertView.findViewById(R.id.line_list_item_cars_required);
        TextView lineETA = (TextView) convertView.findViewById(R.id.line_list_item_eta);

        lineName.setText(listOfLines.get(position).getLine().getName());
        lineDescription.setText(listOfLines.get(position).getLine().getDesc());
        lineBonus.setText("Bonus $" + (int) listOfLines.get(position).getLine().getIncentiveAmount());
        lineCarsRequired.setText(String.valueOf(listOfLines.get(position).getLine().getCarReqCount()) + " cars required");
        lineETA.setText("ETA: 12 min");

        if(listOfLines.get(position).getLine().getBidCount() != 0){
            lastSelectedView = convertView;
            setItemActive(convertView);
        } else {
            setItemInactive(convertView, Color.parseColor(listOfLines.get(position).getLine().getColorRgb()));
        }
        return convertView;
    }

    private void setItemActive(View itemView){
        setItemColorScheme(itemView, ContextCompat.getColor(context, R.color.white), Color.GRAY);
    }

    private void setItemInactive(View itemView, int color){
        setItemColorScheme(itemView, color, ContextCompat.getColor(context, R.color.white));
    }

    private void setItemColorScheme(View convertView, int textColor, int backgroundColor){
        ((TextView) convertView.findViewById(R.id.line_list_item_name)).setTextColor(textColor);
        ((TextView) convertView.findViewById(R.id.line_list_item_description)).setTextColor(textColor);
        ((TextView) convertView.findViewById(R.id.line_list_item_bonus)).setTextColor(textColor);
        ((TextView) convertView.findViewById(R.id.line_list_item_cars_required)).setTextColor(textColor);
        ((TextView) convertView.findViewById(R.id.line_list_item_eta)).setTextColor(textColor);
        convertView.setBackgroundColor(backgroundColor);
    }

    public interface OnLineSelectedCallback {
        void onLineSelected(int LineId);
    }

    private OnLineSelectedCallback DEFAULT_CALLBACK = new OnLineSelectedCallback() {

        @Override
        public void onLineSelected(int LineId) {

        }
    };
}
