package sauer.lists;

import android.content.Context;
import android.os.Debug;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SauerListAdapter extends ArrayAdapter<Lijst> implements ChildEventListener {

    static final String TAG  = SauerListAdapter.class.getName();

    FirebaseDatabase database;

    public SauerListAdapter(Context context, int resource) {
        super(context, resource, R.id.list_name);

        database = FirebaseDatabase.getInstance();
        DatabaseReference lists = database.getReference("lists");
        lists.addChildEventListener(this);

        lists.child("Groceries").setValue("");
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.lists_list_item_text_view, null);
        TextView listNameTextView = (TextView) view.findViewById(R.id.list_name);
        String listName = getItem(position).toString();
        listNameTextView.setText(listName);
        return view;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        add(new Lijst(dataSnapshot.getRef(), s));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        // ignore
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        remove(GetLijst(dataSnapshot.getRef()));
    }

    private Lijst GetLijst(DatabaseReference ref) {
        for (int i = 0; i < getCount(); i++) {
            Lijst l = getItem(i);
            if (l.databaseReference.equals(ref)) {
                return l;
            }
        }
        throw new RuntimeException("Lijst not found: " + ref.toString());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        // ignore

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, databaseError.toString(),databaseError.toException());
    }
}
