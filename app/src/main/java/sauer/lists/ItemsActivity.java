package sauer.lists;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class ItemsActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    ListView listView;
    TextView listNameTextView;

    ItemsAdapter adapter;

    DatabaseReference list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_items);

        Intent intent = getIntent();
        String listKey = intent.getStringExtra("list_key");

        list = Store.getList(listKey);

        listNameTextView = (TextView) findViewById(R.id.list_name);


        list.child("name").addValueEventListener(new LoggingValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listNameTextView.setText("" + dataSnapshot.getValue());
            }
        });

        listNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new EditNameDialogFragment(list);
                dialog.show(getFragmentManager(), null);
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
                DialogFragment dialog = new EditNameDialogFragment(list.child("items").push());
                dialog.show(getFragmentManager(), null);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        DatabaseReference item = adapter.getItem(position);
        DialogFragment dialog = new EditNameDialogFragment(item);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        DatabaseReference item = adapter.getItem(position);
        item.removeValue();
        return true;
    }

}
