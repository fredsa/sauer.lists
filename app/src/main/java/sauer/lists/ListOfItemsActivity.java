package sauer.lists;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

public class ListOfItemsActivity extends AppCompatActivity {

    ListView listView;

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
        ListOfItemsAdapter adapter = new ListOfItemsAdapter(getApplicationContext(), R.layout.named_list, list);
        listView.setAdapter(adapter);

    }
}
