package iiitd.ac.in.androidfaceproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CollageFirst extends AppCompatActivity {
    private final String TAG = "cool collagefirst";
    TextView tvSelect;
    //private int layout_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage_first);
        tvSelect=(TextView)findViewById(R.id.tvSelect);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collage_first, menu);
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


    public void onClickCollage3Btn(View v) {
        ImageView templateImg = (ImageView) findViewById(R.id.chooseLayoutImageView);
        templateImg.setImageResource(R.drawable.three);
        Constants.layout_num=3;
        tvSelect.setVisibility(v.INVISIBLE);
    }

    public void onClickCollage4Btn(View v) {
        ImageView templateImg = (ImageView) findViewById(R.id.chooseLayoutImageView);
        templateImg.setImageResource(R.drawable.four);
        Constants.layout_num=4;
        tvSelect.setVisibility(v.INVISIBLE);
    }

    public void onClickCollage9Btn(View v)
    {
        ImageView templateImg = (ImageView) findViewById(R.id.chooseLayoutImageView);
        templateImg.setImageResource(R.drawable.nine);
        Constants.layout_num=9;
        tvSelect.setVisibility(v.INVISIBLE);
    }

    public void handleImageClick(View v){
        Intent intent=new Intent(this,CollageMainActivity.class);
        //intent.putExtra("layout_num",layout_num);
        startActivity(intent);
    }


}
