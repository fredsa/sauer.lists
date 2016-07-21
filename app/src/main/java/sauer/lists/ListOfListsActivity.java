package sauer.lists;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.Random;

public class ListOfListsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_lists);

        final DatabaseReference lists = Store.lists();

        listView = (ListView) findViewById(R.id.list_view);
        ListOfListsAdapter adapter = new ListOfListsAdapter(getApplicationContext(), R.layout.named_list, lists);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lists.child("List " + Math.round(1000 * Math.random())).setValue("");
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NamedList namedList = (NamedList) listView.getItemAtPosition(position);
        Intent intent = new Intent(this, ListOfItemsActivity.class);
        intent.putExtra("key", namedList.databaseReference.getKey());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        NamedList namedList = (NamedList) listView.getItemAtPosition(position);
        if (namedList.getCount() == 0) {
            namedList.databaseReference.removeValue();
        }
        return true;
    }
}