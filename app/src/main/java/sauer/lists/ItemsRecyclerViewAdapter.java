package sauer.lists;

import android.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder> {

    static final String TAG = ItemsRecyclerViewAdapter.class.getName();

    private RecyclerView recyclerView;
    private FragmentManager fragmentManager;

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

    public ItemsRecyclerViewAdapter(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUpListeners();
    }

    public void add(DatabaseReference item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void remove(DatabaseReference item) {
        items.remove(item);
        notifyDataSetChanged();
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
        assert holder.valueEventListener == null;
        holder.itemNumberTextView.setText("" + (position + 1) + ".");
        holder.itemNameTextView.setVisibility(View.INVISIBLE);

        holder.item = items.get(position);
        holder.valueEventListener = new LoggingValueEventListener(recyclerView.getContext(), holder.item) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                        if (!dataSnapshot.exists()) {
//                            holder.cleanUpListeners();
//                        }
                holder.itemNameTextView.setVisibility(View.VISIBLE);
                holder.itemNameTextView.setText("" + dataSnapshot.child("name").getValue());
            }
        };
        holder.item.addValueEventListener(holder.valueEventListener);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference item = items.get(position);
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(fragmentManager, item, recyclerView.getContext().getString(R.string.item_name));

            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.cleanUpListeners();
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

}
