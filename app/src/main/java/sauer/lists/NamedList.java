package sauer.lists;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class NamedList implements Serializable{
    public DatabaseReference databaseReference;
    public Object value;

    public NamedList(DatabaseReference ref, Object s) {
        databaseReference = ref;
        value = s;
    }

    @Override
    public String toString() {
        return databaseReference.getKey().toString();
    }

    public int getCount() {
        Log.d("TAG", value.getClass().getCanonicalName());
        if (value != null && value.getClass().equals(HashMap.class)) {
            HashMap map = (HashMap) value;
            return map.size();
        } else {
            return 0;
        }
    }
}
