package sauer.lists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ListOfItemsAdapter extends ArrayAdapter<NamedItem> implements ChildEventListener {

    static final String TAG = ListOfItemsAdapter.class.getName();

    public ListOfItemsAdapter(Context context, int resource, DatabaseReference databaseReference) {
        super(context, resource, R.id.list_name);

        databaseReference.addChildEventListener(this);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.named_item, null);
        TextView itemNumberTextView = (TextView) view.findViewById(R.id.item_number);
        TextView listNameTextView = (TextView) view.findViewById(R.id.item_name);
        String itemNumber = getItem(position).getItemNumber() + ".";
        String itemName = getItem(position).toString();
        itemNumberTextView.setText(itemNumber);
        listNameTextView.setText(itemName);
        return view;
    }

    private NamedItem getNamedItem(DatabaseReference ref) {
        for (int i = 0; i < getCount(); i++) {
            NamedItem namedItem = getItem(i);
            if (namedItem.databaseReference.equals(ref)) {
                return namedItem;
            }
        }
        throw new RuntimeException("NamedItem not found: " + ref.toString());
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        add(new NamedItem(dataSnapshot.getRef(), dataSnapshot.getValue()));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        getNamedItem(dataSnapshot.getRef()).value = dataSnapshot.getValue();
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        remove(getNamedItem(dataSnapshot.getRef()));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
        // ignore
        Log.d(TAG, "onChildMoved() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, databaseError.toString(), databaseError.toException());
    }
}
