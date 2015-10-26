package iiitd.ac.in.androidfaceproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;


public class PhotoEditor extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);


        ImageView tv1;
        //tv1.setImageResource(R.id.imageButtonCamera);
        tv1= (ImageView) findViewById(R.id.imgView);
       // tv1.setImageResource(R.id.imageButtonCamera);
        String filePath = getIntent().getStringExtra("filename");
        //Toast.makeText(this,filePath,Toast.LENGTH_LONG).show();
        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
        Log.d("vincent",filePath);
        //Bitmap bitmap1 = BitmapFactory.decodeStream(si1);
        //tv1.setImageBitmap(yourSelectedImage);
        tv1.setImageResource(R.drawable.galleryjpg);

        File imageFile = new  File(filePath);

        File destination = new File(Environment
                .getExternalStorageDirectory(), "1445871154448" + ".jpg");
        //imageFile = destination;

        if(imageFile.exists()){
            Log.d("vincent","image file exists");

            Toast.makeText(this,"image file exists",Toast.LENGTH_LONG).show();
            ImageView imageView= (ImageView) findViewById((R.id.imgView));
            Log.d("vincent",imageFile.getAbsolutePath());
            imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));

            Bitmap logbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //Log.d("vincent",logbitmap.toString());
            if(logbitmap == null){
                Log.d("vincent","bitmap is null");

            }
            else{
                Log.d("vincent","bitmap is NOT NOT null");
            }
            imageView.setImageBitmap(logbitmap);
            Log.d("vincent","file decoded");
        }
        else {
            Log.d("vincent","image file not NOT exists");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_editor, menu);
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
}