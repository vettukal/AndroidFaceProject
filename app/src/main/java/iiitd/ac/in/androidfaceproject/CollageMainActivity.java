package iiitd.ac.in.androidfaceproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

public class CollageMainActivity extends Activity {

    GalleryAdapter adapter;
    ImageView imgSinglePick;
    ViewSwitcher viewSwitcher;
    ImageLoader imageLoader;
    private final String TAG="cool_collage_main";
    //private int layout_num;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG,"on create");
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_collage_main);
        Intent intent=getIntent();
        //layout_num=intent.getIntExtra("layout_num",4); //setting default 4 now
        initImageLoader();
        init();
    }

    private void initImageLoader() {
        Log.d(TAG," initimageloader");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                this).defaultDisplayImageOptions(defaultOptions).memoryCache(
                new WeakMemoryCache());

        ImageLoaderConfiguration config = builder.build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);
    }

    private void init() {
        Log.d(TAG," init");
        Handler handler = new Handler();
        GridView gridGallery=(GridView) findViewById(R.id.gridGallery);
        if(gridGallery==null)
            Log.d(TAG,"grid gallery null");
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        adapter.setMultiplePick(false);
        gridGallery.setAdapter(adapter);

        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        Log.d(TAG,"want to know display child setting to 1");
        viewSwitcher.setDisplayedChild(1);

        imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);


        Button btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
        btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG," btnpickmul clicked");
                Intent i = new Intent(Constants.ACTION_MULTIPLE_PICK);
                //i.putExtra("layout_num",layout_num);
                startActivityForResult(i, 200);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, " onactivityresult");
        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            Log.d(TAG,"");
            String[] all_path = data.getStringArrayExtra("all_path");

            ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

            for (String string : all_path) {
                CustomGallery item = new CustomGallery();
                item.sdcardPath = string;

                dataT.add(item);
            }
            Log.d(TAG,"want to know display child setting to 0");
            viewSwitcher.setDisplayedChild(0);
            adapter.addAll(dataT);
        }
    }
}
