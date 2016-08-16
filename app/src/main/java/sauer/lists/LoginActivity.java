package sauer.lists;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getName() + " *****";
    private static final int RC_SIGN_IN = 42;

    private Button signInButton;
    private Button continueAsButton;
    private Button signOutButton;
    private TextView statusTextView;
    private FirebaseAuth auth;

    private static boolean firstRun = true;
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_login);

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

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, connectionFailedListener)
                .build();
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
        checkForAppInvite();
        Intent intent = new Intent(this, ListsActivity.class);
        intent.putExtra("email", auth.getCurrentUser().getEmail());
        intent.putExtra("uid", auth.getCurrentUser().getUid());
        startActivity(intent);
    }

    private void checkForAppInvite() {
        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    Log.d(TAG, "GOT INVITE: intent=" + intent + " deepLink=" + deepLink + " invitiationId=" + invitationId);
                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        });
    }

    public void setStatus(String text) {
        Log.d(TAG, "STATUS: " + text);
        statusTextView.setText(text);
    }
}
