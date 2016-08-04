package sauer.lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ItemsActivity extends AppCompatActivity implements ChildEventListener {

    private static final String TAG = ItemsActivity.class.getName();

    static final String INTENT_EXTRA_LIST_KEY = "list_key";

    private TextView emptyListTextView;
    private TextView listNameTextView;
    private ItemsRecyclerViewAdapter adapter;
    private DatabaseReference list;
    private LoggingValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String listKey = intent.getStringExtra(INTENT_EXTRA_LIST_KEY);
        assert listKey !=null;
        list = Store.getList(listKey);

        setContentView(R.layout.activity_items);

        listNameTextView = (TextView) findViewById(R.id.list_name);
        emptyListTextView = (TextView) findViewById(R.id.empty_list);

        valueEventListener = new LoggingValueEventListener(getApplicationContext(), list) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                emptyListTextView.setVisibility(dataSnapshot.child("items").exists() ? View.GONE : View.VISIBLE);
                listNameTextView.setText(Utils.GetListNameFromSnapshot(dataSnapshot));
            }
        };

        listNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), list, getString(R.string.list_name));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setRecycleChildrenOnDetach(true);

        adapter = new ItemsRecyclerViewAdapter(getFragmentManager());
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), list.child("items").push(), getString(R.string.item_name));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        list.removeEventListener(valueEventListener);
        list.child("items").removeEventListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.addValueEventListener(valueEventListener);
        list.child("items").addChildEventListener(this);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        adapter.add(dataSnapshot.getRef());
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
        adapter.remove(dataSnapshot.getRef());
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
