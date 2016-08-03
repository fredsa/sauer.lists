package sauer.lists;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ListsRecyclerViewAdapter extends RecyclerView.Adapter<ListsRecyclerViewAdapter.ViewHolder> implements ChildEventListener {

    static final String TAG = ListsRecyclerViewAdapter.class.getName();

    private final Context activityContext;
    private final DatabaseReference listKeys;

    private ArrayList<DatabaseReference> lists = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView listNameTextView;
        public TextView itemCountTextView;
        public DatabaseReference list;
        public ValueEventListener valueEventListener;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.listNameTextView = (TextView) view.findViewById(R.id.list_name);
            this.itemCountTextView = (TextView) view.findViewById(R.id.item_count);
        }

        public void cleanUpListeners() {
            if (valueEventListener != null) {
                list.removeEventListener(valueEventListener);
                valueEventListener = null;
            }
        }
    }

    public ListsRecyclerViewAdapter(Context activityContext, DatabaseReference listKeys) {
        this.activityContext = activityContext;
        this.listKeys = listKeys;
        listKeys.addChildEventListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cleanUpListeners();
        holder.listNameTextView.setVisibility(View.INVISIBLE);
        holder.itemCountTextView.setVisibility(View.INVISIBLE);

        holder.list = lists.get(position);
        holder.valueEventListener = holder.list.addValueEventListener(
                new LoggingValueEventListener(activityContext, holder.list) {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.listNameTextView.setVisibility(View.VISIBLE);
                        holder.listNameTextView.setText("" + dataSnapshot.child("name").getValue());

                        holder.itemCountTextView.setVisibility(View.VISIBLE);
                        HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.child("items").getValue();
                        int itemCount = map == null ? 0 : map.size();
                        String countText = "(" + itemCount + ")";
                        holder.itemCountTextView.setText(countText);
                    }
                });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference list = lists.get(position);
                Intent intent = new Intent(activityContext, ItemsActivity.class);
                intent.putExtra(ItemsActivity.INTENT_EXTRA_LIST_KEY, list.getKey().toString());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(intent);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final DatabaseReference list = lists.get(position);
                list.removeValue();
                listKeys.child(list.getKey()).removeValue();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        lists.add(Store.getList(dataSnapshot.getKey()));
        notifyDataSetChanged();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildChanged() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        notifyDataSetChanged();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
//        Log.d(TAG, "onChildRemoved() " + dataSnapshot.getRef().toString());
        lists.remove(Store.getList(dataSnapshot.getKey()));
        notifyDataSetChanged();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildMoved() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        String msg = getClass().getName() + " failed: " + databaseError.toString() + " details: " + databaseError.getDetails();
        Log.d(TAG, msg, databaseError.toException());
        Toast.makeText(activityContext, msg, Toast.LENGTH_SHORT).show();
    }

}
