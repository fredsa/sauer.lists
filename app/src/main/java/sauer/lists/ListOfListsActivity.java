package sauer.lists;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

public class ListOfListsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_of_lists);

        DatabaseReference lists = Store.lists();

        listView = (ListView) findViewById(R.id.list_view);
        ListOfListsAdapter adapter = new ListOfListsAdapter(getApplicationContext(), R.layout.named_list, lists);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        NamedList namedList = (NamedList) listView.getItemAtPosition(position);
        Intent intent = new Intent(this, ListOfItemsActivity.class);
        intent.putExtra("key", namedList.databaseReference.getKey());
        startActivity(intent);
    }
}
