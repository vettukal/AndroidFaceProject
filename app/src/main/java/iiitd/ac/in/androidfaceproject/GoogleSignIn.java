package iiitd.ac.in.androidfaceproject;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;

public class GoogleSignIn extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;

    private static final int RC_SIGN_IN = 0;
    private boolean mIsResolving = false;
    private boolean mShouldResolve = false;

    //Bitmap bmp;
    //ImageView iv_background;

    private ImageView img; // The second image
    private MyApp app;
    private int navid; //= R.id.imageView1; // id of the imageView
    private int navdid ;//= R.drawable.secondimage; // id of the image drawable
    private int bgid = R.drawable.bg; // id of the background drawable
    private int layoutid = R.id.layout_google_sign_in; // id of the activity layout
    private RelativeLayout layout; // the layout of the activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_sign_in);

        SharedPreferences settings = getSharedPreferences("mysettings",0);
        SharedPreferences.Editor editor = settings.edit();
        Log.d("vince","Google SignIn fetch: "+settings.getString("email","none"));
        if(settings.contains("email")){
            Log.d("vince SignIn","email field is present");
            if(!settings.getString("email","none").equalsIgnoreCase("none")){
                callLanding();
            }
        }



        app = (MyApp)getApplication();
        layout = (RelativeLayout) findViewById(layoutid);
        app.setBackground(layout, bgid);


        findViewById(R.id.sign_in_button).setOnClickListener(this);
        ((SignInButton) findViewById(R.id.sign_in_button)).setSize(SignInButton.SIZE_WIDE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addApi(LocationServices.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }
    /**
    private void loadBG() {
        // adapt the image to the size of the display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.bg),size.x,size.y,true);

        // fill the background ImageView with the resized image
        iv_background = (ImageView) findViewById(R.id.bg_google_signin);
        iv_background.setImageBitmap(bmp);
    }
    */

    /**
    private void unloadBG(){
        if(iv_background!=null)
            iv_background.setImageBitmap(null);

        if(bmp!=null)
            bmp = null;
    }

    */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Message","Connection has been suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                }
                catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
            else {
                Log.e("Error","Some error!");
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mShouldResolve = false;

        String currentAccount = Plus.AccountApi.getAccountName(mGoogleApiClient);
        if(true) {
            Toast.makeText(this,"Signed in...",Toast.LENGTH_LONG).show();
            SharedPreferences settings = getSharedPreferences("mysettings",0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("email",currentAccount);
            Log.d("vince","SignIN put: "+currentAccount);
            editor.commit();
            callLanding();
        }
        else {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
    }

    private void onSignInClicked() {
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("vince GoogleSignIn"," ON stop() called. Now unloading.");
        //unloadBG();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        layout = (RelativeLayout) findViewById(layoutid);
        app.setBackground(layout, bgid);
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    private void callLanding(){
        Intent intent = new Intent(this,LandingPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
