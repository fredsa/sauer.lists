package sauer.lists;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class SauerList {
    public DatabaseReference databaseReference;
    public Object value;

    public SauerList(DatabaseReference ref, Object s) {
        databaseReference = ref;
        value = s;
    }

    @Override
    public String toString() {
        return databaseReference.getKey().toString();
    }

    public int getCount() {
        if (value != null && value.getClass().equals(ArrayList.class)) {
            ArrayList list = (ArrayList) value;
            return list.size();
        } else {
            return 0;
        }
    }
}
