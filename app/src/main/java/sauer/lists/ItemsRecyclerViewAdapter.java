package sauer.lists;

import android.app.FragmentManager;
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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder> implements ChildEventListener {

    static final String TAG = ItemsRecyclerViewAdapter.class.getName();

    private final Context activityContext;
    private FragmentManager fragmentManager;
    private final DatabaseReference list;

    private ArrayList<DatabaseReference> items = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView itemNumberTextView;
        public TextView itemNameTextView;
        public DatabaseReference item;
        public ValueEventListener valueEventListener;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            this.itemNameTextView = (TextView) view.findViewById(R.id.item_name);
            this.itemNumberTextView = (TextView) view.findViewById(R.id.item_number);
        }

        public void cleanUpListeners() {
            if (valueEventListener != null) {
                item.removeEventListener(valueEventListener);
                valueEventListener = null;
            }
        }
    }

    public ItemsRecyclerViewAdapter(Context activityContext, FragmentManager fragmentManager, DatabaseReference list) {
        this.activityContext = activityContext;
        this.fragmentManager = fragmentManager;
        this.list = list;
        list.child("items").addChildEventListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.cleanUpListeners();
        holder.itemNumberTextView.setText("" + (position+1) +".");
        holder.itemNameTextView.setVisibility(View.INVISIBLE);

        holder.item = items.get(position);
        holder.valueEventListener = holder.item.addValueEventListener(
                new LoggingValueEventListener(activityContext, holder.item) {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        holder.itemNameTextView.setVisibility(View.VISIBLE);
                        holder.itemNameTextView.setText("" + dataSnapshot.child("name").getValue());
                    }
                });

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference item = items.get(position);
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(fragmentManager, item, activityContext.getString(R.string.item_name));

                Intent intent = new Intent(activityContext, ItemsActivity.class);
                intent.putExtra(ItemsActivity.INTENT_EXTRA_LIST_KEY, list.getKey().toString());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                activityContext.startActivity(intent);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DatabaseReference item = items.get(position);
                item.removeValue();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //    public ItemsRecyclerViewAdapter(Context context, int resource, final DatabaseReference list) {
//        super(context, resource, R.id.list_name);
//
//        list.child("items").addChildEventListener(this);
//
//        list.addValueEventListener(new LoggingValueEventListener(getContext(), list) {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    list.child("items").removeEventListener(ItemsRecyclerViewAdapter.this);
//                    list.removeEventListener(this);
//                }
//            }
//        });
//    }
//
//    @NonNull
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService
//                (Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.item, null);
//
//        TextView itemNumberTextView = (TextView) view.findViewById(R.id.item_number);
//        String itemNumber = (position + 1) + ".";
//        itemNumberTextView.setText(itemNumber);
//
//        final DatabaseReference item = getItem(position);
//
//        final TextView itemNameTextView = (TextView) view.findViewById(R.id.item_name);
//        itemNameTextView.setVisibility(View.INVISIBLE);
//        final LoggingValueEventListener nameListener = new LoggingValueEventListener(getContext(), item.child("name")) {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                itemNameTextView.setVisibility(View.VISIBLE);
//                itemNameTextView.setText("" + dataSnapshot.getValue());
//            }
//        };
//        item.child("name").addValueEventListener(nameListener);
//
//        itemNameTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                // check if view is being torn down
//                if (position > getCount() - 1) {
//                    return;
//                }
//                if (!hasFocus) {
//                    item.child("name").setValue(itemNameTextView.getText().toString());
//                }
//            }
//        });
//
//        item.addValueEventListener(new LoggingValueEventListener(getContext(), item) {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    item.child("name").removeEventListener(nameListener);
//                    item.removeEventListener(this);
//                }
//            }
//        });
//
//        return view;
//    }
//
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//        Log.d(TAG, "onChildAdded() " + dataSnapshot.getRef().toString() + " " + dataSnapshot.getValue());
        items.add(dataSnapshot.getRef());
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
        items.remove(dataSnapshot.getRef());
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
