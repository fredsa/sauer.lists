package sauer.lists;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ListsActivity extends AppCompatActivity implements ChildEventListener {

    private static final String TAG = ListsActivity.class.getName();

    private ListsRecyclerViewAdapter adapter;
    private DatabaseReference listKeys;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lists);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listKeys = Store.getUserListKeys(uid);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setRecycleChildrenOnDetach(true);

        adapter = new ListsRecyclerViewAdapter(listKeys);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference list = makeList();
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), list, getString(R.string.list_name));
            }
        });

        valueEventListener = new LoggingValueEventListener(getApplicationContext(), listKeys) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.no_lists).setVisibility(dataSnapshot.exists() ? View.GONE : View.VISIBLE);
            }
        };

        showLoadingSpinner();

    }

    private void showLoadingSpinner() {
        final TextView statusTextView = (TextView) findViewById(R.id.status_text);
        statusTextView.setText("Loading…");

        listKeys.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String statusText = "Successfully retrieved " + dataSnapshot.getChildrenCount() + " references to my lists.";
                statusTextView.setText(statusText);
                Log.d(getClass().getName(), statusText);

                findViewById(R.id.loading_layout).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                String errorMessage = "Failed to retrieve initial data:\n" + databaseError.toString();
                Log.d(getClass().getName(), errorMessage, databaseError.toException());
                statusTextView.setText(errorMessage);
                statusTextView.setTextColor(Color.RED);
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        listKeys.removeEventListener(valueEventListener);
        listKeys.removeEventListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        listKeys.addValueEventListener(valueEventListener);
        listKeys.addChildEventListener(this);
    }

    private DatabaseReference makeList() {
        DatabaseReference listPointer = listKeys.push();
        listPointer.setValue("OWNER");

        return Store.getList(listPointer.getKey());
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        adapter.add(Store.getList(dataSnapshot.getKey()));
//        notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
//        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
//        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        adapter.remove(Store.getList(dataSnapshot.getKey()));
//        notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildMoved() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
//        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String msg = getClass().getName() + " failed: " + databaseError.toString() + " details: " + databaseError.getDetails();
        Log.d(TAG, msg, databaseError.toException());
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
