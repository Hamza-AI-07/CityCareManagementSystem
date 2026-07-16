package com.example.municipalservices.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.municipalservices.R;
import com.example.municipalservices.utils.Constant;

import java.util.ArrayList;

public class SortedComplaintsAdapter extends RecyclerView.Adapter<SortedComplaintsAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "SortedComplaintsAdapter";
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<Integer> mIDs = new ArrayList<>();
    private ArrayList<Integer> filteredIDs = new ArrayList<>();
    private ArrayList<String> mStatuses = new ArrayList<>();
    private Context mcontext;
    public complaintlist obj;
    boolean searchInitialized = false;

    public SortedComplaintsAdapter(ArrayList<Integer> mIDs, ArrayList<String> mImageNames, ArrayList<String> mImage,ArrayList<String> mStatuses ,Context context) {
        this.mIDs = mIDs;
        this.mImageNames = mImageNames;
        this.mImage = mImage;
        this.mcontext = context;
        this.mStatuses = mStatuses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sorted_complaints_listitem, parent , false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String name, status, imageUrl;
        int id;

        if (searchInitialized && !filteredIDs.isEmpty()) {
            int index = filteredIDs.get(0) - 1; // Existing logic from your code
            // Note: This logic seems specific to your search implementation
            name = mImageNames.get(index);
            status = mStatuses.get(index);
            imageUrl = mImage.get(index);
            id = mIDs.get(index);
            searchInitialized = false;
        } else {
            name = mImageNames.get(position);
            status = mStatuses.get(position);
            imageUrl = mImage.get(position);
            id = mIDs.get(position);
        }

        holder.imageName.setText(name);
        holder.itemID.setText("ID: #" + id);
        holder.imageStatus.setText(status);

        // Professional status coloring
        if (status != null) {
            String s = status.toLowerCase();
            if (s.contains("pending")) {
                holder.imageStatus.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.colorAccent));
            } else if (s.contains("open")) {
                holder.imageStatus.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.colorPrimary));
            } else if (s.contains("close")) {
                holder.imageStatus.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.colorGreen));
            } else if (s.contains("reject")) {
                holder.imageStatus.setBackgroundTintList(ContextCompat.getColorStateList(mcontext, R.color.colorRed));
            }
        }

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(mcontext).load(imageUrl)
                    .placeholder(R.drawable.icon_upload)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.icon_upload);
        }
    }

    @Override
    public int getItemCount() {
        if (searchInitialized)
            return filteredIDs.size();
        else
            return mIDs.size();
    }

    public void getdata(complaintlist obj) {
        this.obj = obj;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                searchInitialized = true;
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredIDs = mIDs;
                } else {
                    ArrayList<Integer> filteredList = new ArrayList<>();
                    for (Integer row : mIDs) {
                        if (String.valueOf(row).equalsIgnoreCase(String.valueOf(charSequence))) {
                            filteredList.add(row);
                        }
                    }
                    filteredIDs = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredIDs;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredIDs = (ArrayList<Integer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView image;
        TextView imageName, imageStatus, itemID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            imageStatus = itemView.findViewById(R.id.image_status);
            itemID = itemView.findViewById(R.id.tv_item_id);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (obj != null) {
                obj.getclicks(getAdapterPosition(), mImageNames.get(getAdapterPosition()));
            }
        }
    }

    public interface complaintlist {
        void getclicks(int positions, String s);
    }
}
