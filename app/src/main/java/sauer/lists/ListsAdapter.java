package sauer.lists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class ListsAdapter extends ArrayAdapter<DatabaseReference> implements ChildEventListener {

    static final String TAG = ListsAdapter.class.getName();

    private int itemCount= -1;

    public ListsAdapter(Context context, int resource, DatabaseReference lists) {
        super(context, resource, R.id.list_name);

        lists.addChildEventListener(this);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list, null);

        final TextView listNameTextView = (TextView) view.findViewById(R.id.list_name);
        final DatabaseReference list = getItem(position);
        list.child("name").addListenerForSingleValueEvent(new LoggingValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listNameTextView.setText(dataSnapshot.getValue().toString());
            }
        });

        final TextView itemCountTextView = (TextView) view.findViewById(R.id.item_count);

        list.child("items").addListenerForSingleValueEvent(new LoggingValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                itemCount = map == null ? 0 : map.size();
                String countText = itemCount == -1 ? "" : "(" + itemCount + ")";
                itemCountTextView.setText(countText);
            }
        });

        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        add(dataSnapshot.getRef());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        remove(dataSnapshot.getRef());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildMoved() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, databaseError.toString(), databaseError.toException());
    }

    public int getItemCount() {
        return itemCount;
    }
}
