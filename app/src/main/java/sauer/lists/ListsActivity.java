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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ListsActivity extends AppCompatActivity {

    private static final String TAG = ListsActivity.class.getName();

    private RecyclerView recyclerView;
    private ListsRecyclerViewAdapter adapter;
    private DatabaseReference listKeys;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lists);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        listKeys = Store.getUserListKeys(uid);

        final TextView statusTextView = (TextView) findViewById(R.id.status_text);
        statusTextView.setText("Loading…");

        listKeys.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String statusText = "Successfully retrieved " + dataSnapshot.getChildrenCount() + " references to my lists.";
                statusTextView.setText(statusText);
                Log.d(getClass().getName(), statusText);

                findViewById(R.id.loading_layout).setVisibility(View.GONE);
                dataLoaded(listKeys);
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

    private void dataLoaded(final DatabaseReference listKeys) {
        recyclerView = (RecyclerView) findViewById(R.id.list_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListsRecyclerViewAdapter(getApplicationContext(), listKeys);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setVisibility(View.VISIBLE);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), makeList(), getString(R.string.list_name));
            }
        });
    }

    private DatabaseReference makeList() {
        DatabaseReference listPointer = listKeys.push();
        listPointer.setValue("OWNER");

        return Store.getList(listPointer.getKey());
    }

}
