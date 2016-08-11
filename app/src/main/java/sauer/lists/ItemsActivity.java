package sauer.lists;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

public class ItemsActivity extends AppCompatActivity implements ChildEventListener {

    private static final String TAG = ItemsActivity.class.getName();

    private static final int REQUEST_INVITE = 42;

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
        assert listKey != null;
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

        FloatingActionButton shareButton = (FloatingActionButton) findViewById(R.id.share_button);
        shareButton.setVisibility(View.VISIBLE);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvite();
            }
        });

        list.addValueEventListener(valueEventListener);
        list.child("items").addChildEventListener(this);
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

        Uri uri = Uri.parse(getString(R.string.invitation_deep_link, list.getKey(), invite.getKey()));
        Log.d(TAG, "Sending invitation to deep link " + uri.toString());
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title, listName))
                .setMessage(getString(R.string.invitation_message, listName))
                .setDeepLink(uri)
                .setEmailSubject(getString(R.string.invitation_subject, listName))
                .setEmailHtmlContent(getString(R.string.invitation_html_content))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + ", resultCode=" + resultCode + " data=" + data);
        if (requestCode == REQUEST_INVITE) {
            switch (resultCode) {
                case RESULT_OK:
                    // Get the invitation IDs of all sent messages
                    String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                    for (String id : ids) {
                        Log.d(TAG, "REQUEST_INVITE RESULT_OK: sent invitation " + id);
                    }
                    break;
                case RESULT_CANCELED:
                    // invitation cancelled
                    Log.d(TAG, "REQUEST_INVITE RESULT_CANCELED");
//                    Toast.makeText(getApplicationContext(), "Invitation canceled.", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    String msg = "Request invite unhandled result code: " + resultCode;
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
