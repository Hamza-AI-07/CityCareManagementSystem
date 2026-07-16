package com.example.municipalservices;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.models.UserSignUpModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import androidx.annotation.NonNull;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentUserHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentUserHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserHome extends Fragment {

    int total = 0;
    int open = 0;
    int closed = 0;
    int pendingFeedback = 0;
    int positiveFeedback = 0;
    int negativeFeedBack = 0;
    int droppedFeedBack = 0;

    CardView cvTotalComplaints, cvOpenComplaints, cvClosedComplaints, cvPositiveFeedback, cvNegativeFeedback, cvPendingFeedback, cvDroppedFeedback;

    TextView tvWelcomeUser, totalComplaintsInNumber, openComplaintsInNumber, closedComplaintsInNumber, positiveFeedbackInNumber, negativeFeedBackInNumber, pendingFeedbackInNumber, droppedFeedbackInNumber;


    private OnFragmentInteractionListener mListener;

    public FragmentUserHome() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentUserHome newInstance(String param1, String param2) {
        FragmentUserHome fragment = new FragmentUserHome();
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


        View rootView = inflater.inflate(R.layout.fragment_user_home, container, false);


        cvTotalComplaints = rootView.findViewById(R.id.cv_total_complaints);
        cvOpenComplaints = rootView.findViewById(R.id.cv_open_complaints);
        cvClosedComplaints = rootView.findViewById(R.id.cv_closed_complaints);
        tvWelcomeUser = rootView.findViewById(R.id.tv_welcome_user);
        totalComplaintsInNumber = rootView.findViewById(R.id.tv_total_complaints_in_number);
        openComplaintsInNumber = rootView.findViewById(R.id.tv_open_complaints_in_number);
        closedComplaintsInNumber = rootView.findViewById(R.id.tv_closed_complaints_in_number);
        positiveFeedbackInNumber = rootView.findViewById(R.id.tv_positive_feedback_in_number);
        negativeFeedBackInNumber = rootView.findViewById(R.id.tv_negative_feedback_in_number);
        pendingFeedbackInNumber = rootView.findViewById(R.id.tv_pending_feedback_in_number);
        droppedFeedbackInNumber = rootView.findViewById(R.id.tv_dropped_feedback_in_number);
        cvPositiveFeedback = rootView.findViewById(R.id.cv_positive_feedback);
        cvNegativeFeedback = rootView.findViewById(R.id.cv_negative_feedback);
        cvDroppedFeedback = rootView.findViewById(R.id.cv_dropped_feedback);
        cvPendingFeedback = rootView.findViewById(R.id.cv_pending_feedback);


        setNumbersToViews();
        fetchAndSetUserName();

        FloatingActionButton fab = rootView.findViewById(R.id.fab2);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NewComplaintActivity.class));
            }
        });


        cvTotalComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AllComplaintsListUserActivity.class));
            }
        });


        cvOpenComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), OpenComplaintsActivity.class));
            }
        });

        cvClosedComplaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ClosedComplaintsActivity.class));
            }
        });


        cvPositiveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PositiveFeedbackActivity.class));
            }
        });

        cvNegativeFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), NegativeFeedbackActivity.class));
            }
        });

        cvPendingFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PendingFeedbackActivity.class));
            }
        });

        cvDroppedFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DroppedFeedbackActivity.class));
            }
        });


        return rootView;
    }


    private void fetchAndSetUserName() {
        String email = FirebaseAuth.getInstance().getCurrentUser() != null ? 
                       FirebaseAuth.getInstance().getCurrentUser().getEmail() : "";

        if (email != null && !email.isEmpty()) {
            FirebaseDatabase.getInstance().getReference(Constant.USER).orderByChild("email").equalTo(email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                UserSignUpModel user = child.getValue(UserSignUpModel.class);
                                if (user != null && user.getName() != null) {
                                    tvWelcomeUser.setText("Welcome, " + user.getName());
                                    // Add a simple fade-in animation
                                    tvWelcomeUser.setAlpha(0f);
                                    tvWelcomeUser.animate().alpha(1f).setDuration(1000).start();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        }
    }

    private void setNumbersToViews() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference complaints = database.getReference("Complaints");
        
        String currentUserEmail = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        }

        final String finalEmail = currentUserEmail;

        complaints.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ComplaintModel di = dataSnapshot.getValue(ComplaintModel.class);
                if (di == null) return;
                
                // Filter by current user email
                if (finalEmail != null && !finalEmail.isEmpty()) {
                    if (di.getComplainerEmail() == null || !di.getComplainerEmail().equalsIgnoreCase(finalEmail)) {
                        return;
                    }
                }

                String status = di.getComplainStatus();
                if (status != null) {
                    if (status.toLowerCase().contains("open"))
                        open++;
                    if (status.toLowerCase().contains("close")) {
                        closed++;
                    }
                }

                if (di.getLikes() > di.getDislikes() && di.getDislikes() < (di.getLikes() * 2))
                    positiveFeedback++;

                if (di.getDislikes() > di.getLikes())
                    negativeFeedBack++;

                if (di.getLikes() <= 0 && di.getDislikes() <= 0)
                    pendingFeedback++;

                if (di.getDislikes() >= (di.getLikes() * 2) && di.getLikes() != 0)
                    droppedFeedBack++;

                total++;

                if (totalComplaintsInNumber != null) totalComplaintsInNumber.setText(String.valueOf(total));
                if (openComplaintsInNumber != null) openComplaintsInNumber.setText(String.valueOf(open));
                if (closedComplaintsInNumber != null) closedComplaintsInNumber.setText(String.valueOf(closed));
                if (positiveFeedbackInNumber != null) positiveFeedbackInNumber.setText(String.valueOf(positiveFeedback));
                if (negativeFeedBackInNumber != null) negativeFeedBackInNumber.setText(String.valueOf(negativeFeedBack));
                if (pendingFeedbackInNumber != null) pendingFeedbackInNumber.setText(String.valueOf(pendingFeedback));
                if (droppedFeedbackInNumber != null) droppedFeedbackInNumber.setText(String.valueOf(droppedFeedBack));

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
