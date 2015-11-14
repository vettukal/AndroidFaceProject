package iiitd.ac.in.androidfaceproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

public class DisplayCollageActivity extends Activity {
    String filepath;
    private String TAG="cool displayclgActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreate");
        setContentView(R.layout.activity_display_collage);

        //receive filepath from intent
        Bundle b=this.getIntent().getExtras();
        filepath=b.getString("mergedImage");
        Log.d(TAG, "filepath=" + filepath);

        //get file
        File file=new  File(filepath);
        //set image to the imageview
        if(file.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ImageView imgView = (ImageView) findViewById(R.id.singleImageView);
            imgView.setImageBitmap(myBitmap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_collage, menu);
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
        else if(id==R.id.action_share)
        {
            //ShareImage shareImage=new ShareImage(allPath);
            share();
        }

        return super.onOptionsItemSelected(item);
    }
    private void share() {
        Log.i(TAG, "Share image");
        Intent intent = new Intent(Intent.ACTION_SEND);
        //intent.setData(Uri.parse("mailto:"));
        intent.setType("image/*");
        Log.d(TAG,"in share method filepath="+filepath);
        //ArrayList<Uri> files = new ArrayList<Uri>();


        //for (String path : filePaths) {
            //Uri uri = Uri.fromFile(new File(filepath));
            //files.add(uri);
        //}
        if (filepath != null) {
            Log.d(TAG,"reached not null filepath section");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(filepath));
            try {
                startActivity(intent.createChooser(intent, "sharing"));

            } catch (android.content.ActivityNotFoundException ex) {
                //Toast.makeText("no client available", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"Oops no client available for sharing!");
            }
        }
        else{
            Log.d(TAG,"filepath null");
        }
    }
}
