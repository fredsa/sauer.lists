package sauer.lists;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.w3c.dom.Text;

public class EditNameDialogFragment extends DialogFragment {

    DatabaseReference databaseReference;
    String title;
    EditText nameEditText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.edit_name_dialog, null);

        TextView titleTextView = (TextView) view.findViewById(R.id.title);
        titleTextView.setText(title);

        nameEditText = (EditText) view.findViewById(R.id.name);
        databaseReference.child("name").addValueEventListener(new LoggingValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                nameEditText.append(value == null ? "" : value.toString());
            }
        });

        nameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL) {
                    save();
                    close();
                    return true;
                }
                return false;
            }
        });

        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                save();
                close();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                close();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return dialog;
    }

    public void show(FragmentManager manager, DatabaseReference databaseReference, String title) {
        this.databaseReference = databaseReference;
        this.title = title;
        super.show(manager, null);
    }

    void close() {
        getDialog().cancel();
    }

    void save() {
        String value = nameEditText.getText().toString().trim();
        if (value.length() != 0) {
            databaseReference.child("name").setValue(value);
        }
    }
}
