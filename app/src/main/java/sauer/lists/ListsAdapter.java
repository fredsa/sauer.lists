package sauer.lists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class ListsAdapter extends ArrayAdapter<DatabaseReference> implements ChildEventListener {

    static final String TAG = ListsAdapter.class.getName();

    public ListsAdapter(Context context, int resource, DatabaseReference listKeys) {
        super(context, resource, R.id.list_name);

        listKeys.addChildEventListener(this);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list, null);

        final DatabaseReference list = getItem(position);

        final TextView listNameTextView = (TextView) view.findViewById(R.id.list_name);
        listNameTextView.setVisibility(View.INVISIBLE);
        final LoggingValueEventListener nameListener = new LoggingValueEventListener(getContext(), list.child("name").toString()) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listNameTextView.setVisibility(View.VISIBLE);
                listNameTextView.setText("" + dataSnapshot.getValue());
            }
        };
        list.child("name").addValueEventListener(nameListener);

        final TextView itemCountTextView = (TextView) view.findViewById(R.id.item_count);
        itemCountTextView.setVisibility(View.INVISIBLE);
        final LoggingValueEventListener itemsListener = new LoggingValueEventListener(getContext(), list.child("items").toString()) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemCountTextView.setVisibility(View.VISIBLE);
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                int itemCount = map == null ? 0 : map.size();
                String countText = "(" + itemCount + ")";
                itemCountTextView.setText(countText);
            }
        };
        list.child("items").addValueEventListener(itemsListener);

        list.addValueEventListener(new LoggingValueEventListener(getContext(), list.toString()) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    list.child("name").removeEventListener(nameListener);
                    list.child("items").removeEventListener(itemsListener);
                    list.removeEventListener(this);
                }
            }
        });

        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        add(Store.getList(dataSnapshot.getKey()));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
//        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        remove(Store.getList(dataSnapshot.getKey()));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildMoved() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String msg = getClass().getName() + " failed: " + databaseError.toString() + " details: " + databaseError.getDetails();
        Log.d(TAG, msg, databaseError.toException());
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

}
