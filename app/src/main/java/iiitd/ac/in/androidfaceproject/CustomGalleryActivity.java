package iiitd.ac.in.androidfaceproject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CustomGalleryActivity extends Activity {

    private static final String TAG = "cool CustGlryActivity";

    GridView gridGallery;
    Handler handler;
    GalleryAdapter adapter;
    ImageView imgNoMedia;
    Button btnGalleryShare, btnGalleryCollage;
    String action;
    private ImageLoader imageLoader;
    Point size;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "oncreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gallery);

        action = getIntent().getAction();
        if (action == null) {
            finish();
        }
        initImageLoader();
        init();
    }

    private void initImageLoader() {
        try {
            String CACHE_DIR = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.temp_tmp";
            new File(CACHE_DIR).mkdirs();

            File cacheDir = StorageUtils.getOwnCacheDirectory(getBaseContext(),
                    CACHE_DIR);

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                    .bitmapConfig(Bitmap.Config.RGB_565).build();
            ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                    getBaseContext())
                    .defaultDisplayImageOptions(defaultOptions)
                            //.discCache(new UnlimitedDiscCache(cacheDir))
                    .memoryCache(new WeakMemoryCache());

            ImageLoaderConfiguration config = builder.build();
            imageLoader = ImageLoader.getInstance();
            imageLoader.init(config);

        } catch (Exception e) {

        }
    }

    private void init() {

        handler = new Handler();
        gridGallery = (GridView) findViewById(R.id.gridGallery);
        gridGallery.setFastScrollEnabled(true);
        adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
        PauseOnScrollListener listener = new PauseOnScrollListener(imageLoader,
                true, true);
        gridGallery.setOnScrollListener(listener);

        if (action.equalsIgnoreCase(Constants.ACTION_MULTIPLE_PICK)) {

            findViewById(R.id.llBottomContainer).setVisibility(View.VISIBLE);
            gridGallery.setOnItemClickListener(mItemMulClickListener);
            adapter.setMultiplePick(true);

        } else if (action.equalsIgnoreCase(Constants.ACTION_PICK)) {

            findViewById(R.id.llBottomContainer).setVisibility(View.GONE);
            gridGallery.setOnItemClickListener(mItemSingleClickListener);
            adapter.setMultiplePick(false);

        }

        gridGallery.setAdapter(adapter);
        imgNoMedia = (ImageView) findViewById(R.id.imgNoMedia);

        btnGalleryShare = (Button) findViewById(R.id.btnGalleryShare);
        btnGalleryShare.setOnClickListener(mShareClickListener);

        btnGalleryCollage = (Button) findViewById(R.id.btnGalleryCollage);
        //btnGalleryCollage.setOnClickListener(mCreateCollageClickListener);

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        adapter.addAll(getGalleryPhotos());
                        checkImageStatus();
                    }
                });
                Looper.loop();
            }

            ;

        }.start();

    }

    private void checkImageStatus() {
        if (adapter.isEmpty()) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
    }

    String[] allPath;
    //View.OnClickListener mCreateCollageClickListener=new View.OnClickListener() {

    public void mCreateCollageClickListener(View v) {
        Log.d(TAG, "create collage onclick");
        ArrayList<CustomGallery> selected = adapter.getSelected();

        allPath = new String[selected.size()];
        for (int i = 0; i < allPath.length; i++) {
            allPath[i] = selected.get(i).sdcardPath;
            Log.d(TAG, "selected media:" + allPath[i]);
        }

        Intent data = new Intent().putExtra("all_path", allPath);
        setResult(RESULT_OK, data);

        //obtain display size
        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        Log.d(TAG, "display height=" + size.y + " width=" + size.x);

        //allow users to sleect maximum 9 images
        if(allPath.length>9){
            Toast.makeText(CustomGalleryActivity.this, "Can't select more than 9 photos!", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"max 9 can be selected..error");
        }
        else{
            try {
                Bitmap mergedImage=createSingle();
                if (mergedImage == null)
                    Log.d(TAG, "null image");
                else
                    Log.d(TAG, "image received..not null");
                // display image in new activity

                //Write file
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                String fileName = "/testbitmap"+formatter.format(now) + ".png";
                File file = new File(Environment.getExternalStorageDirectory() +fileName);
                /* //add if want to replace file, do not append date then
                if (file.exists()) {
                    Log.d(TAG, "file already exists at " + file + "...deleting..");
                    file.delete();
                } else
                    Log.d(TAG, "file not present..so craeting new");*/
                FileOutputStream stream = new FileOutputStream(file); //this.openFileOutput(filename, Context.MODE_PRIVATE);
                mergedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

                //Cleanup
                stream.close();
                mergedImage.recycle();

                Toast.makeText(CustomGalleryActivity.this, "Image saved in gallery", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"imaged saved in SD card");

                //Pop intent to display and share collage
                            Bundle b=new Bundle();
                            b.putString("mergedImage",file.toString());
                            Intent intent=new Intent(this,DisplayCollageActivity.class);
                            intent.putExtras(b);
                            startActivity(intent);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    View.OnClickListener mShareClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "share onclick");
            ArrayList<CustomGallery> selected = adapter.getSelected();

            allPath = new String[selected.size()];
            for (int i = 0; i < allPath.length; i++) {
                allPath[i] = selected.get(i).sdcardPath;
                Log.d(TAG, "selected media:" + allPath[i]);
            }

            Intent data = new Intent().putExtra("all_path", allPath);
            setResult(RESULT_OK, data);
            share(allPath);
        }
    };

    private Bitmap createSingle()
    {
        Bitmap bmp = Bitmap.createBitmap(size.x,size.y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.GRAY);
        Matrix m=new Matrix(size.x/3,size.y/3);
        Bitmap b=null;
        Cell cell;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        try {
            for (int i = 0; i < allPath.length; i++) {
                b = BitmapFactory.decodeStream(new FileInputStream(new File(allPath[i])), null, options);
                cell = m.getCell(i);
                b = Bitmap.createScaledBitmap(b,size.x/3 ,size.y/3, true);
                //Log.d(TAG,"i="+i+" top=" + cell.getTop() + " left=" + cell.getLeft());
                canvas.save();
                //canvas.rotate(10);
                canvas.drawBitmap(b, cell.getLeft(),cell.getTop(),null);
                //canvas.restore();
            }
            return bmp;
        }
        catch(FileNotFoundException e)
        {
            Log.d(TAG,e.toString());
        }
        return null;
    }

    private void share(String[] filePaths) {
        Log.i(TAG, "Share image");
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        ArrayList<Uri> files = new ArrayList<Uri>();


        for (String path : filePaths) {
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            files.add(uri);
        }

        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        try {
            startActivity(intent.createChooser(intent, "Share using"));

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,"No client available for sharing", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Oops no client available for sharing!");
        }
    }

    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            adapter.changeSelection(v, position);

        }
    };

    AdapterView.OnItemClickListener mItemSingleClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            CustomGallery item = adapter.getItem(position);
            Intent data = new Intent().putExtra("single_path", item.sdcardPath);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    private ArrayList<CustomGallery> getGalleryPhotos() {
        ArrayList<CustomGallery> galleryList = new ArrayList<CustomGallery>();

        try {
            final String[] columns = {MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;

            Cursor imagecursor = managedQuery(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);

            if (imagecursor != null && imagecursor.getCount() > 0) {

                while (imagecursor.moveToNext()) {
                    CustomGallery item = new CustomGallery();

                    int dataColumnIndex = imagecursor
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.sdcardPath = imagecursor.getString(dataColumnIndex);

                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // show newest photo at beginning of the list
        Collections.reverse(galleryList);
        return galleryList;
    }


}
