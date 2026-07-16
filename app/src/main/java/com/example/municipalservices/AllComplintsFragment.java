package com.example.municipalservices;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.municipalservices.adapters.NewComplaintAdapter;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AllComplintsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AllComplintsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AllComplintsFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    ListView newComplaintListView;

    public AllComplintsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AllComplintsFragment newInstance(String param1, String param2) {
        AllComplintsFragment fragment = new AllComplintsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_complints, container, false);


        newComplaintListView = view.findViewById(R.id.lv_all_complaints);

        final String names[] = {
                "Cleanliness",
                "Street Lights",
                "Water Connection",
                "Water Supply Scheme",
                "Illegal Construction",
                "Encroachment",
                "Repair of Street",
                "Graveyards"
        };


        int images[] = {
                R.drawable.icon_cleanliness,
                R.drawable.icon_road,
                R.drawable.icon_water_supply,
                R.drawable.icon_water,
                R.drawable.icon_illegal_construction,
                R.drawable.icon_enchraochment,
                R.drawable.icon_road,
                R.drawable.icon_graveyard
        };






        //set custom adapter to listView
        NewComplaintAdapter newComplaintAdapter = new NewComplaintAdapter(getActivity(), images,names);
        newComplaintListView.setAdapter(newComplaintAdapter);


        //Click listener to listView
        newComplaintListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SortedComplaintsListActivity.class);
                intent.putExtra("category",""+names[i]);
                startActivity(intent);
            }
        });


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
