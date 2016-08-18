package sauer.lists;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getName() + " *****";
    private static final int RC_SIGN_IN = 42;

    private Button signInButton;
    private Button continueAsButton;
    private Button signOutButton;
    private TextView statusTextView;
    private FirebaseAuth auth;

    private static boolean firstRun = true;
    private String deepLinkListKey;
    private String deepLinkInviteCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_login);

        Uri deepLinkUri = getIntent().getData();
        if (deepLinkUri != null) {
            Log.d(TAG, "onCreate(): deepLinkUri=" + deepLinkUri);
            processDeepLink(deepLinkUri.toString());
        }

        statusTextView = (TextView) findViewById(R.id.status_text);

        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        continueAsButton = (Button) findViewById(R.id.contine_as_button);
        continueAsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListsActivity();
            }
        });

        signOutButton = (Button) findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateButtonsStates();
            }
        });

        GoogleApiClient.OnConnectionFailedListener connectionFailedListener =
                new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.w(TAG, "onConnectionFailed() " + connectionResult);
                    }
                };
    }

    private void processDeepLink(String deepLinkUri) {
        Pattern pattern = Pattern.compile(getString(R.string.share_deep_link_regex));
        Matcher matcher = pattern.matcher(deepLinkUri);
        if (!matcher.matches()) {
            FirebaseCrash.report(new Exception("Unable to parse deep link URL " + deepLinkUri));
            return;
        }
        if (matcher.groupCount() != 2) {
            FirebaseCrash.report(new Exception("Deep link URL unexpectedly matches " + matcher.groupCount() + " groups: " + deepLinkUri));
            return;
        }
        deepLinkListKey = matcher.group(1);
        deepLinkInviteCode = matcher.group(2);
        if (deepLinkListKey != null) {
            Log.d(TAG, "deepLinkListKey=" + deepLinkListKey);
            getSharedPreferences(Constants.SHARED_PREFERENCES_INVITES, MODE_PRIVATE)
                    .edit()
                    .putString(Constants.LIST_KEY, deepLinkListKey)
                    .apply();
        }
        if (deepLinkInviteCode != null) {
            Log.d(TAG, "deepLinkInviteCode=" + deepLinkInviteCode);
            getSharedPreferences(Constants.SHARED_PREFERENCES_INVITES, MODE_PRIVATE)
                    .edit()
                    .putString(Constants.INVITE_CODE, deepLinkInviteCode)
                    .apply();
        }
    }

    private void updateButtonsStates() {
        boolean isSignedIn = auth.getCurrentUser() != null;

        signInButton.setVisibility(!isSignedIn ? View.VISIBLE : View.GONE);

        continueAsButton.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);
        if (isSignedIn) {
            continueAsButton.setText(getString(R.string.continue_as, auth.getCurrentUser().getEmail()));
        }

        signOutButton.setVisibility(isSignedIn ? View.VISIBLE : View.GONE);

        if (isSignedIn) {
            setStatus("Signed in as " + auth.getCurrentUser().getEmail() + ".");
        } else {
            setStatus("Please sign-in.");
        }

        if (firstRun && isSignedIn) {
            firstRun = false;
            startListsActivity();
        }
    }

    private void signOut() {
        Log.d(TAG, "Signing out…");
        setStatus("Signing out…");
        AuthUI.getInstance(getFirebaseApp())
                .signOut(this);
    }

    private void signIn() {
        Log.d(TAG, "Signing in…");
        setStatus("Signing in…");
        startActivityForResult(
                AuthUI.getInstance(getFirebaseApp())
                        .createSignInIntentBuilder()
                        .setProviders(AuthUI.GOOGLE_PROVIDER, AuthUI.EMAIL_PROVIDER)
                        .build(),
                RC_SIGN_IN);
    }

    private FirebaseApp getFirebaseApp() {
        return FirebaseApp.getInstance();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(" + requestCode + ", " + resultCode + ", " + data + ")");
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                startListsActivity();
            } else {
                setStatus("Please sign in.");
            }
        }
    }

    private void startListsActivity() {
        Intent intent = new Intent(this, ListsActivity.class);
        startActivity(intent);
    }

    public void setStatus(String text) {
        Log.d(TAG, "STATUS: " + text);
        statusTextView.setText(text);
    }

}
