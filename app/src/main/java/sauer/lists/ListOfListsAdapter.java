package sauer.lists;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ListOfListsAdapter extends ArrayAdapter<NamedList> implements ChildEventListener {

    static final String TAG = ListOfListsAdapter.class.getName();


    public ListOfListsAdapter(Context context, int resource, DatabaseReference databaseReference) {
        super(context, resource, R.id.list_name);

        databaseReference.addChildEventListener(this);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.named_list, null);

        TextView listNameTextView = (TextView) view.findViewById(R.id.list_name);
        String listName = getItem(position).toString();
        listNameTextView.setText(listName);

        TextView itemCountTextView = (TextView) view.findViewById(R.id.item_count);
        String countText = "(" + getItem(position).getCount() + ")";
        itemCountTextView.setText(countText);

        return view;
    }

    private NamedList getNamedList(DatabaseReference ref) {
        for (int i = 0; i < getCount(); i++) {
            NamedList namedList = getItem(i);
            if (namedList.databaseReference.equals(ref)) {
                return namedList;
            }
        }
        throw new RuntimeException("NamedList not found: " + ref.toString());
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        add(new NamedList(dataSnapshot.getRef(), dataSnapshot.getValue()));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        getNamedList(dataSnapshot.getRef()).value = dataSnapshot.getValue();
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        remove(getNamedList(dataSnapshot.getRef()));
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
