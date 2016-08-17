package sauer.lists;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

public class ItemsActivity extends AppCompatActivity implements ChildEventListener {

    private static final String TAG = ItemsActivity.class.getName();

    private static final int SEND_INVITE_RESULT = 42;

    private TextView emptyListTextView;
    private TextView listNameTextView;
    private ItemsRecyclerViewAdapter adapter;
    private DatabaseReference list;
    private LoggingValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String listKey = intent.getStringExtra(Constants.LIST_KEY);
        assert listKey != null;
        list = Store.getList(listKey);

        setContentView(R.layout.activity_items);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        adapter = new ItemsRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), list.child("items").push(), getString(R.string.item_name));
            }
        });

        list.addValueEventListener(valueEventListener);
        list.child("items").addChildEventListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.items_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, item.toString() + " " + item.getItemId());
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                sendInvite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list.removeEventListener(valueEventListener);
        list.child("items").removeEventListener(this);
    }

    private void sendInvite() {
        // Get list name from TextView as there's no list.child("name").getValue()
        // and the async list.addListenerForSingleValueEvent() is just awkward here
        String listName = listNameTextView.getText().toString();

        DatabaseReference invite = list.child("invites").push();
        invite.setValue(ServerValue.TIMESTAMP);
        Log.d(TAG, "SENDING INVITE FOR " + list.getKey() + " with invite code " + invite.getKey());

        String shareDeepLink = getString(R.string.share_deep_link, list.getKey(), invite.getKey());
        Uri uri = Uri.parse(getString(R.string.invitation_dynamic_link, shareDeepLink));
        Log.d(TAG, "Sending invitation: share_deep_link=" + shareDeepLink + " invitation_dynamic_link=" + uri.toString());

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invitation_message, listName, uri.toString()));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.invitation_chooser_title, listName)), SEND_INVITE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + ", resultCode=" + resultCode + " data=" + data);
        if (requestCode == SEND_INVITE_RESULT) {
            switch (resultCode) {
                case RESULT_OK:
                    Log.d(TAG, "SEND_INVITE_RESULT RESULT_OK");
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG, "SEND_INVITE_RESULT RESULT_CANCELED");
//                    Toast.makeText(getApplicationContext(), "Invitation canceled.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    String msg = "SEND_INVITE_RESULT unhandled result code: " + resultCode;
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, msg);
            }
        }
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
        Log.e(TAG, msg, databaseError.toException());
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
