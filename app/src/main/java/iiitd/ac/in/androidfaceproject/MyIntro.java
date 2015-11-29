package iiitd.ac.in.androidfaceproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class MyIntro extends AppIntro {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    // Please DO NOT override onCreate. Use init
    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(AppIntroFragment.newInstance("PhotoEditor", "Edit your photos or click one on the go", R.drawable.bg2, Color.argb(232, 218, 176, 64)));
        addSlide(AppIntroFragment.newInstance("Age and Gender Detection", "Detect age and gender of your friends and family on the go ", R.drawable.agender_scr2, Color.argb(232, 218, 176, 64)));
        addSlide(AppIntroFragment.newInstance("Filters", "Add filters to your photos to give them a fresh new look..", R.drawable.filters_scr, Color.argb(232, 218, 176, 64)));
        addSlide(AppIntroFragment.newInstance("Collage", "Arrange your photos into memories, by our Collage feature", R.drawable.collage, Color.argb(232, 218, 176, 64)));

        showDoneButton(true);
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed() {
        Intent intent = new Intent(this,GoogleSignIn.class);
        startActivity(intent);
    }


    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        Intent intent = new Intent(this,GoogleSignIn.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged() {

    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("first_time", false))
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("first_time", true);
            editor.commit();

        }
        else
        {
            Intent intent = new Intent(this,GoogleSignIn.class);
            startActivity(intent);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}