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
import com.google.firebase.database.FirebaseDatabase;

public class SauerListAdapter extends ArrayAdapter<SauerList> implements ChildEventListener {

    static final String TAG = SauerListAdapter.class.getName();

    FirebaseDatabase database;

    public SauerListAdapter(Context context, int resource) {
        super(context, resource, R.id.list_name);

        database = FirebaseDatabase.getInstance();
        DatabaseReference lists = database.getReference("lists");
        lists.addChildEventListener(this);

        lists.child("Groceries").setValue("");
        lists.child("Answer").setValue("forty-two");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.sauer_list_entry, null);
        TextView listNameTextView = (TextView) view.findViewById(R.id.list_name);
        TextView itemCountTextView = (TextView) view.findViewById(R.id.item_count);
        String listName = getItem(position).toString();
        String countText = "(" + getItem(position).getCount() + ")";
        listNameTextView.setText(listName);
        itemCountTextView.setText(countText);
        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        add(new SauerList(dataSnapshot.getRef(), dataSnapshot.getValue()));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        GetLijst(dataSnapshot.getRef()).value = dataSnapshot.getValue();
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        remove(GetLijst(dataSnapshot.getRef()));
    }

    private SauerList GetLijst(DatabaseReference ref) {
        for (int i = 0; i < getCount(); i++) {
            SauerList l = getItem(i);
            if (l.databaseReference.equals(ref)) {
                return l;
            }
        }
        throw new RuntimeException("SauerList not found: " + ref.toString());
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
