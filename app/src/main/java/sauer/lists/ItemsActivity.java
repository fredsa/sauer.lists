package sauer.lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ItemsActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    public static final String INTENT_EXTRA_LIST_KEY = "list_key";

    ListView listView;
    TextView listNameTextView;

    ItemsAdapter adapter;

    DatabaseReference list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_items);

        Intent intent = getIntent();
        String listKey = intent.getStringExtra(INTENT_EXTRA_LIST_KEY);

        list = Store.getList(listKey);

        listNameTextView = (TextView) findViewById(R.id.list_name);


        final LoggingValueEventListener nameListener = new LoggingValueEventListener(getApplicationContext(), list.child("name").toString()) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listNameTextView.setText("" + dataSnapshot.getValue());
            }
        };
        list.child("name").addValueEventListener(nameListener);

        listNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), list, getString(R.string.list_name));
            }
        });

        adapter = new ItemsAdapter(getApplicationContext(), R.layout.list, list);

        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), list.child("items").push(), getString(R.string.item_name));
            }
        });

        list.addValueEventListener(new LoggingValueEventListener(getApplicationContext(), list.toString()) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    list.child("name").removeEventListener(nameListener);
                    list.removeEventListener(this);
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        DatabaseReference item = adapter.getItem(position);
        EditNameDialogFragment dialog = new EditNameDialogFragment();
        dialog.show(getFragmentManager(), item, getString(R.string.item_name));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        DatabaseReference item = adapter.getItem(position);
        item.removeValue();
        return true;
    }

}
