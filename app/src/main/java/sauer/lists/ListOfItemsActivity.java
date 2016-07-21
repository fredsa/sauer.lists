package sauer.lists;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class ListOfItemsActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {

    ListView listView;
    ListOfItemsAdapter adapter;

    DatabaseReference list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_items);

        Intent intent = getIntent();
        String listName = intent.getStringExtra("key");

        TextView listNameTextView = (TextView) findViewById(R.id.list_name);
        listNameTextView.setText(listName);

        list = Store.lists().child(listName);

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ListOfItemsAdapter(getApplicationContext(), R.layout.named_list, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.push().setValue("Item " + Math.round(1000 * Math.random()));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        NamedItem namedItem = adapter.getItem(position);

        DialogFragment dialog = new EditNameDialogFragment(namedItem);
        dialog.show(getFragmentManager(), null);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        NamedItem namedItem = (NamedItem) listView.getItemAtPosition(position);
        namedItem.databaseReference.removeValue();
        return true;
    }

}
