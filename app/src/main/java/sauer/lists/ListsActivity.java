package sauer.lists;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ListsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lists);

        ListView listView = (ListView) findViewById(R.id.list_view);
        SauerListAdapter adapter = new SauerListAdapter(getApplicationContext(), R.layout.sauer_lists_entry);
        listView.setAdapter(adapter);
    }
}
