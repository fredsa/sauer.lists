package sauer.lists;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class NamedItem {
    public DatabaseReference databaseReference;
    public Object value;

    public NamedItem(DatabaseReference ref, Object s) {
        databaseReference = ref;
        this.value = s;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public String getItemNumber() {
        return databaseReference.getKey().toString();
    }

}
