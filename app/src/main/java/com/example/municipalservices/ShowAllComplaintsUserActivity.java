package com.example.municipalservices;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.municipalservices.adapters.SortedComplaintsAdapter;
import com.example.municipalservices.models.ComplaintModel;
import com.example.municipalservices.utils.Constant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class ShowAllComplaintsUserActivity extends AppCompatActivity implements SortedComplaintsAdapter.complaintlist{



    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mStatuses = new ArrayList<>();
    private ArrayList<Integer> mIDs = new ArrayList<>();
    public static ArrayList<ComplaintModel> list;
    FirebaseDatabase database;

    private SearchView searchView;
    SortedComplaintsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_complaints_user);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("All Complaints");
        }




        list = new ArrayList<>();


        database = FirebaseDatabase.getInstance();
        final DatabaseReference complaints = database.getReference(Constant.COMPLAINTS);
        
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

                    list.add(di);
                    mNames.add(di.getComplainTitle());
                    mImageUrls.add(di.getComplainImageURL());
                    mStatuses.add(di.getComplainStatus());
                    mIDs.add(di.getComplaintID());

                    initRecylerView();


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


    private void initRecylerView() {
        RecyclerView recyclerView = findViewById(R.id.sd_recycler_view);
        adapter = new SortedComplaintsAdapter(mIDs,mNames, mImageUrls, mStatuses, this);
        adapter.getdata(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void getclicks(int positions, String s) {

        ComplaintDetailsUserActivity.list = list.get(positions);
        startActivity(new Intent(getApplicationContext(), ComplaintDetailsUserActivity.class));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                Log.d("testLog","onQueryTextSubmit");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                Log.d("testLog","onQueryTextChange");

                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}