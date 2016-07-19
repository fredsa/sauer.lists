package sauer.lists;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

public class SauerListAdapter extends ArrayAdapter {

    public SauerListAdapter(Context context, int resource) {
        super(context, resource, R.id.list_name);
        add("foo");
        add("bar");
    }

}
