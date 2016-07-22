package sauer.lists;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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
                DialogFragment dialog = new EditNameDialogFragment(lists.push());
                dialog.show(getFragmentManager(), null);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DatabaseReference list = adapter.getItem(position);
        Intent intent = new Intent(this, ItemsActivity.class);
        intent.putExtra("list_key", list.getKey().toString());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        ListsAdapter adapter = (ListsAdapter) adapterView.getAdapter();
        DatabaseReference list = adapter.getItem(position);
        if (adapter.getItemCount()== 0) {
            list.removeValue();
        }
        return true;
    }
}
