package sauer.lists;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ListsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView listView;
    ListsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lists);

        final DatabaseReference lists = Store.lists();

        adapter = new ListsAdapter(getApplicationContext(), R.layout.list, lists);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), lists.push(), getString(R.string.list_name));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DatabaseReference list = adapter.getItem(position);
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra(ItemsActivity.INTENT_EXTRA_LIST_KEY, list.getKey().toString());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        final ListsAdapter adapter = (ListsAdapter) adapterView.getAdapter();
        final DatabaseReference list = adapter.getItem(position);
        list.child("items").addListenerForSingleValueEvent(new LoggingValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    list.removeValue();
                }
            }
        });
        return true;
    }
}
