package sauer.lists;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

class ListsRecyclerViewAdapter extends RecyclerView.Adapter<ListsRecyclerViewAdapter.ViewHolder> {

    static final String TAG = ListsRecyclerViewAdapter.class.getName();

    private DatabaseReference userAcls;
    private RecyclerView recyclerView;
    private ArrayList<DatabaseReference> lists = new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        TextView listNameTextView;
        TextView itemCountTextView;
        DatabaseReference list;
        ValueEventListener valueEventListener;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.listNameTextView = (TextView) view.findViewById(R.id.list_name);
            this.itemCountTextView = (TextView) view.findViewById(R.id.item_count);
        }

        void cleanUpListeners() {
            if (valueEventListener != null) {
                list.removeEventListener(valueEventListener);
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
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    ListsRecyclerViewAdapter(DatabaseReference userAcls) {
        this.userAcls = userAcls;
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.cleanUpListeners();
    }

    void add(DatabaseReference list) {
        lists.add(list);
        notifyDataSetChanged();
    }

    void remove(DatabaseReference list) {
        lists.remove(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int currentPosition) {
        assert holder.valueEventListener == null;
        holder.listNameTextView.setVisibility(View.INVISIBLE);
        holder.itemCountTextView.setVisibility(View.INVISIBLE);

        holder.list = lists.get(currentPosition);
        holder.valueEventListener = new LoggingValueEventListener(recyclerView.getContext(), holder.list) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.listNameTextView.setVisibility(View.VISIBLE);
                holder.listNameTextView.setText(Utils.GetListNameFromSnapshot(dataSnapshot));

                holder.itemCountTextView.setVisibility(View.VISIBLE);
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.child("items").getValue();
                int itemCount = map == null ? 0 : map.size();
                String countText = "(" + itemCount + ")";
                holder.itemCountTextView.setText(countText);
            }
        };
        holder.list.addValueEventListener(holder.valueEventListener);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference list = lists.get(holder.getAdapterPosition());
                Intent intent = new Intent(recyclerView.getContext(), ItemsActivity.class);
                intent.putExtra(Constants.LIST_KEY, list.getKey());
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                recyclerView.getContext().startActivity(intent);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.cleanUpListeners();
                final DatabaseReference list = lists.get(holder.getAdapterPosition());
                list.removeValue();
                userAcls.child(list.getKey()).removeValue();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

}
