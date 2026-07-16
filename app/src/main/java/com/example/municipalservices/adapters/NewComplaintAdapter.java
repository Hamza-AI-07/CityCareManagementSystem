package com.example.municipalservices.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.municipalservices.R;

public class NewComplaintAdapter extends ArrayAdapter {


    int newComplaintIcon[];
    String newComplaintName[];


    public NewComplaintAdapter(@NonNull Context context, int[] newComplaintIcon, String[] newComplaintName) {
        super(context, R.layout.custom_listview_new_complaint, newComplaintName);

        this.newComplaintIcon = newComplaintIcon;
        this.newComplaintName = newComplaintName;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.custom_listview_new_complaint, parent, false);

        ImageView newComplaintImage = view.findViewById(R.id.iv_new_complaint_icon);
        TextView newComplaintTitle = view.findViewById(R.id.tv_new_complaint_name);

        newComplaintImage.setImageResource(newComplaintIcon[position]);
        newComplaintTitle.setText(""+newComplaintName[position]);

        return view;
    }



}
