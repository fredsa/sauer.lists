package sauer.lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

public class ItemsActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_LIST_KEY = "list_key";

    private RecyclerView recyclerView;
    private TextView listNameTextView;
    private ItemsRecyclerViewAdapter adapter;
    private DatabaseReference list;
    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String listKey = intent.getStringExtra(INTENT_EXTRA_LIST_KEY);
        list = Store.getList(listKey);

        setContentView(R.layout.activity_items);

        listNameTextView = (TextView) findViewById(R.id.list_name);

        final LoggingValueEventListener nameListener =
                new LoggingValueEventListener(getApplicationContext(), list.child("name")) {
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

        recyclerView = (RecyclerView) findViewById(R.id.list_view);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ItemsRecyclerViewAdapter(getApplicationContext(),getFragmentManager(), list);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment dialog = new EditNameDialogFragment();
                dialog.show(getFragmentManager(), list.child("items").push(), getString(R.string.item_name));
            }
        });

        list.addValueEventListener(new LoggingValueEventListener(getApplicationContext(), list) {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    list.child("name").removeEventListener(nameListener);
                    list.removeEventListener(this);
                }
            }
        });
    }

}
