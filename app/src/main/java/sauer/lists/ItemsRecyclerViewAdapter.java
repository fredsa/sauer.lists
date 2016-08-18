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

class ItemsRecyclerViewAdapter extends RecyclerView.Adapter<ItemsRecyclerViewAdapter.ViewHolder> {

    static final String TAG = ItemsRecyclerViewAdapter.class.getName();

    private RecyclerView recyclerView;

    private ArrayList<DatabaseReference> items = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        TextView itemNumberTextView;
        TextView itemNameTextView;
        DatabaseReference item;
        ValueEventListener valueEventListener;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.itemNameTextView = (TextView) view.findViewById(R.id.item_name);
            this.itemNumberTextView = (TextView) view.findViewById(R.id.item_number);
        }

        void cleanUpListeners() {
            if (valueEventListener != null) {
                item.removeEventListener(valueEventListener);
                valueEventListener = null;
            }
        }
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

    void add(DatabaseReference item) {
        items.add(item);
        notifyDataSetChanged();
    }

    void remove(DatabaseReference item) {
        items.remove(item);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int currentPosition) {
        assert holder.valueEventListener == null;
        holder.itemNumberTextView.setText("" + (currentPosition + 1) + ".");
        holder.itemNameTextView.setVisibility(View.INVISIBLE);

        holder.item = items.get(currentPosition);
        holder.valueEventListener = new LoggingValueEventListener(recyclerView.getContext(), holder.item) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.itemNameTextView.setVisibility(View.VISIBLE);
                holder.itemNameTextView.setText(Utils.GetItemNameFromSnapshot(dataSnapshot));
            }
        };
        holder.item.addValueEventListener(holder.valueEventListener);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference item = items.get(holder.getAdapterPosition());
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), item, recyclerView.getContext().getString(R.string.item_name));
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.cleanUpListeners();
                DatabaseReference item = items.get(holder.getAdapterPosition());
                item.removeValue();
                return true;
            }
        });
    }

    private FragmentManager getFragmentManager() {
        return ((ItemsActivity) recyclerView.getContext()).getFragmentManager();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
