package iiitd.ac.in.androidfaceproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;

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
        Log.d(TAG,"File object created ");
        //set image to the imageview
        if(file.exists()) {
            Log.d(TAG," file exists block!");
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                FileInputStream fs = new FileInputStream(file);
                BitmapFactory.decodeStream(fs, null, options);
                fs.close();
                fs= new FileInputStream(file);
                options.inJustDecodeBounds = false;
                Bitmap myBitmap = BitmapFactory.decodeStream(fs, null, options);
                //these 3 lines were there initially
                //Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                ImageView imgView = (ImageView) findViewById(R.id.singleImageView);
                imgView.setImageBitmap(myBitmap);
                //
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else
            Log.d(TAG," file not exist block");
        FloatingActionButton fab=(FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //receive filepath from intent

                share();
            }
        });
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
        intent.setType("image/png");
        Log.d(TAG,"in share method filepath="+filepath);
        //ArrayList<Uri> files = new ArrayList<Uri>();


        //for (String path : filePaths) {
            //Uri uri = Uri.fromFile(new File(filepath));
            //files.add(uri);
        //}
        if (filepath != null) {
            Log.d(TAG,"reached not null filepath section");
            Uri uri=Uri.fromFile(new File(filepath));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            
            try {
                startActivity(intent.createChooser(intent, "share using"));

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
