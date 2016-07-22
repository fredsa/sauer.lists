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

public class ItemsAdapter extends ArrayAdapter<DatabaseReference> implements ChildEventListener {

    static final String TAG = ItemsAdapter.class.getName();

    public ItemsAdapter(Context context, int resource, DatabaseReference list) {
        super(context, resource, R.id.list_name);

        list.child("items").addChildEventListener(this);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, null);

        TextView itemNumberTextView = (TextView) view.findViewById(R.id.item_number);
        String itemNumber = (position + 1) + ".";
        itemNumberTextView.setText(itemNumber);

        final TextView itemNameTextView = (TextView) view.findViewById(R.id.item_name);
        final DatabaseReference item = getItem(position);
        item.child("name").addValueEventListener(new LoggingValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemNameTextView.setText("" + dataSnapshot.getValue());
            }
        });
        itemNameTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (position > getCount() - 1) {
                    return;
                }
                if (!hasFocus) {
                    item.child("name").setValue(itemNameTextView.getText().toString());
                }
            }
        });

        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        add(dataSnapshot.getRef());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
//        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        remove(dataSnapshot.getRef());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildMoved() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, databaseError.toString(), databaseError.toException());
    }
}
