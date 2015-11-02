package iiitd.ac.in.androidfaceproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;


public class LandingPage extends AppCompatActivity {

    File destination;
    Uri selectedImage;
    public static String selectedPath1 = "NONE";
    private static final int PICK_Camera_IMAGE = 2;
    private static final int SELECT_FILE1 = 1;
    public static Bitmap bmpScale;
    public static String imagePath;
    Bitmap bmp;
    ImageView iv_background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        loadBG();
        ImageButton mcamera;
        mcamera = (ImageButton) findViewById(R.id.imageButtonCamera);
        mcamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                //String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");

                // Create an instance of SimpleDateFormat used for formatting
// the string representation of date (month/day/year)
                //DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                long time = System.currentTimeMillis();

// Get the date today using Calendar object.
                //Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
                String reportDate = time + "";

                destination = new File(Environment
                        .getExternalStorageDirectory(), reportDate + ".jpg");
                Log.d("vincent", "destination: " + destination.toString());
                Log.d("vincent", "destination: " + destination.getAbsolutePath());

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(destination));
                startActivityForResult(intent, PICK_Camera_IMAGE);

            }
        });


        ImageButton mgallery = (ImageButton) findViewById(R.id.imageButtonGallery);
        mgallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                final int ACTIVITY_SELECT_IMAGE = 1234;
                startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
            }
        });

        ImageButton mCollage = (ImageButton) findViewById(R.id.imageButtonCollage);
        final Activity a = this;
        mCollage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(a, Collage.class);
                startActivity(intent);

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_landing_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadBG() {
        /* adapt the image to the size of the display */
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        bmp = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.bg2), size.x, size.y, true);

        /* fill the background ImageView with the resized image */
        iv_background = (ImageView) findViewById(R.id.bg_landing_page);
        iv_background.setImageBitmap(bmp);
    }

    private void unloadBG() {
        if (iv_background != null)
            iv_background.setImageBitmap(null);

        if (bmp != null)
            bmp = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unloadBG();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBG();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1234:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();


                    Intent intent = new Intent(this, PhotoEditor.class);
                    intent.putExtra("filename", filePath);
                    startActivity(intent);
                    //Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
            /* Now you have choosen image in Bitmap format in object "yourSelectedImage". You can use it in way you want! */
                }
        }

    }
}
