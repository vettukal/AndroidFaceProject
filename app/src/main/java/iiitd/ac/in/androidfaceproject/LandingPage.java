package iiitd.ac.in.androidfaceproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;


public class LandingPage extends AppCompatActivity {

    File destination;
    Uri selectedImage;
    public static String selectedPath1 = "NONE";
    private static final int PICK_Camera_IMAGE = 1235;
    private static final int SELECT_FILE1 = 1;
    public static Bitmap bmpScale;
    public static String imagePath;
    //Bitmap bmp;
    //ImageView iv_background;

    private ImageView img; // The second image
    private MyApp app;
    private int navid; //= R.id.imageView1; // id of the imageView
    private int navdid ;//= R.drawable.secondimage; // id of the image drawable
    private int bgid = R.drawable.bg2; // id of the background drawable
    private int layoutid = R.id.layout_landing_page; // id of the activity layout
    private RelativeLayout layout; // the layout of the activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        app = (MyApp)getApplication();
        layout = (RelativeLayout) findViewById(layoutid);
        app.setBackground(layout, bgid);

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
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), reportDate + ".jpg");
                Log.d("vincent", "destination: " + destination.toString());
                Log.d("vincent", "destination: " + destination.getAbsolutePath());

                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.DATE_TAKEN, reportDate);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                values.put(MediaStore.MediaColumns.DATA, destination.toString());

                getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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
                Intent intent = new Intent(a,CollageFirst.class);
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
    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onResume() {
        super.onResume();
        layout = (RelativeLayout) findViewById(layoutid);
        app.setBackground(layout, bgid);
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

                break;
            case 1235:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(this, PhotoEditor.class);
                    intent.putExtra("filename", destination.toString());
                    startActivity(intent);
                }
        }

    }
}
