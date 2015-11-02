package iiitd.ac.in.androidfaceproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;


public class PhotoEditor extends AppCompatActivity {


    boolean lvlOneScrollView = false;
    boolean lvltwoScrollView = false;
    HorizontalScrollView [] scrollViewsLvlOne;
    HorizontalScrollView [] scrollViewsLvlTwo;

    private RelativeLayout toolbox;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_editor);

        toolbox = (RelativeLayout) findViewById(R.id.toolbox);
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
        tv1.setImageResource(R.drawable.gallerypng);

        File imageFile = new  File(filePath);

        File destination = new File(Environment
                .getExternalStorageDirectory(), "1445871154448" + ".jpg");
        //imageFile = destination;

        /**
        scrollViewsLvlOne = new HorizontalScrollView [] {(HorizontalScrollView) findViewById(R.id.hsv_filters),
                (HorizontalScrollView) findViewById(R.id.hsv_adjustments)};
        */
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

    /**
    public void onClickAdjstmentBtn(View v)
    {
        HorizontalScrollView hsvAdjustments = (HorizontalScrollView) findViewById(R.id.hsv_adjustments);
        if(hsvAdjustments.getVisibility() == View.VISIBLE)
            hsvAdjustments.setVisibility(View.GONE);
        else if(lvlOneScrollView) {
            turnOffScrollView(1);
            hsvAdjustments.setVisibility(View.VISIBLE);
            lvlOneScrollView = true;
            }
        else{
            hsvAdjustments.setVisibility(View.VISIBLE);
            lvlOneScrollView = true;
        }



//        Toast.makeText(this, "Clicked on Adjustment Button", Toast.LENGTH_LONG).show();
    }


    public void onClickFilterBtn(View v)
    {
        HorizontalScrollView hsvFilters = (HorizontalScrollView) findViewById(R.id.hsv_filters);
        if(hsvFilters.getVisibility() == View.VISIBLE)
            hsvFilters.setVisibility(View.GONE);
        else if(lvlOneScrollView) {
            turnOffScrollView(1);
            hsvFilters.setVisibility(View.VISIBLE);
            lvlOneScrollView = true;
        }
        else{
            hsvFilters.setVisibility(View.VISIBLE);
            lvlOneScrollView = true;
        }
        //Toast.makeText(this, "Clicked on filter Button", Toast.LENGTH_LONG).show();
    }


    private void turnOffScrollView(int i) {
        if(i==1)
        {
            for(HorizontalScrollView temp : scrollViewsLvlOne)
                temp.setVisibility(View.GONE);
            lvlOneScrollView = false;
        }


    }


    */
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

    public void toggleToolbox(View v){
        Log.d("vince "+this.getLocalClassName()," image clicked and inside the toggleToolBox");

        if (toolbox.getVisibility() == View.VISIBLE) {
            toolbox.setVisibility(View.GONE);
        } else {
            toolbox.setVisibility(View.VISIBLE);
        }
    }

    public void onClickEffectButton(View view){
        Log.d("vince "+this.getLocalClassName(),"Button at bottom is clicked.");
        Log.d("vince "+this.getLocalClassName(),"view name: "+view.getTag().toString());

        handleLevel1(view.getTag().toString());
    }

    public void onClickFilterEffectButton(View view){
        Log.d("vince "+this.getLocalClassName(),"Button at Filter Ribbon is clicked.");
        Log.d("vince "+this.getLocalClassName(),"view name: "+view.getTag().toString());

        //handleLevel1(view.getTag().toString());
    }


    public void onClickAdjButton(View view){
        Log.d("vince "+this.getLocalClassName(),"Button at Adjustment Ribbon is clicked.");
        Log.d("vince "+this.getLocalClassName(),"view name: "+view.getTag().toString());

        //handleLevel1(view.getTag().toString());
    }


    private void handleLevel1(String tag) {

        // TODO: check whether the tag is valid or not.
        // doing the dirty way of just applying the effect without checking

        if(tag.equals("Adjustments")){
            //TODO: Take every ribbon other than adjustments and make invisible
            LinearLayout adjustPop = (LinearLayout) findViewById(R.id.effects_holder2);
            if(adjustPop.getVisibility()==View.VISIBLE){
                adjustPop.setVisibility(View.GONE);
            }
            else {
                adjustPop.setVisibility(View.VISIBLE);
            }

        }

        else if(tag.equals("Filters")){
            //TODO: Take every ribbon other than filter and make invisible
            LinearLayout adjustPop = (LinearLayout) findViewById(R.id.effects_holder3);
            if(adjustPop==null){
                Log.d("vince "+this.getLocalClassName()," adjustPop is null");
                return;
            }
            if(adjustPop.getVisibility()==View.VISIBLE){
                adjustPop.setVisibility(View.GONE);
            }
            else {
                adjustPop.setVisibility(View.VISIBLE);
            }
        }
    }
}
