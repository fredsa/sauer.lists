package sauer.lists;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class ListsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = ListsActivity.class.getName();

    ListView listView;
    ListsAdapter adapter;
    private DatabaseReference listKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(TAG, "uid: " + uid);

        listKeys = Store.getUserListKeys(uid);

        setContentView(R.layout.activity_lists);

        final TextView statusTextView = (TextView) findViewById(R.id.status_text);
        statusTextView.setText("Loading…");

        listKeys.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String statusText = "Successfully retrieved " + dataSnapshot.getChildrenCount() + " references to my lists.";
                statusTextView.setText(statusText);
                Log.d(getClass().getName(), statusText);

                findViewById(R.id.loading_layout).setVisibility(View.GONE);
                if (!dataSnapshot.exists()) {
                    addGroceriesList();
                    addTodoList();
                }
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
        adapter = new ListsAdapter(getApplicationContext(), R.layout.list, listKeys);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

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

    private void addGroceriesList() {
        DatabaseReference list = makeList();
        list.child("name").setValue("Groceries");
        DatabaseReference items = list.child("items");
        items.push().child("name").setValue("fresh mozarella");
        items.push().child("name").setValue("basil");
        items.push().child("name").setValue("olive oil");
        items.push().child("name").setValue("balsamic vinegar");
    }

    private void addTodoList() {
        DatabaseReference list = makeList();
        list.child("name").setValue("To do list");
        DatabaseReference items = list.child("items");
        items.push().child("name").setValue("take out trash");
        items.push().child("name").setValue("buy HTC Vive");
        items.push().child("name").setValue("change oil");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DatabaseReference list = adapter.getItem(position);
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra(ItemsActivity.INTENT_EXTRA_LIST_KEY, list.getKey().toString());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        final ListsAdapter adapter = (ListsAdapter) adapterView.getAdapter();
        final DatabaseReference list = adapter.getItem(position);
        list.removeValue();
        listKeys.child(list.getKey()).removeValue();
        return true;
    }
}
