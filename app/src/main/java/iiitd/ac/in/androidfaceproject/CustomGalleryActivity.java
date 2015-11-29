package iiitd.ac.in.androidfaceproject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class CustomGalleryActivity extends Activity {

    private static final String TAG = "cool CustGlryActivity";
    public final static String APP_PATH_SD_CARD = "/photobooth/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";

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
        Log.d(TAG, "initimageloader");
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
        Log.d(TAG, "init");
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
            };

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
        if (allPath.length > Constants.layout_num) {
            Toast.makeText(CustomGalleryActivity.this, "Maximum "+Constants.layout_num+" photos allowed for this template", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Maximum "+Constants.layout_num+" photos allowed for this template");
        } else {
            try {
                Bitmap mergedImage = createSingle();
                if (mergedImage == null)
                    Log.d(TAG, "null image");
                else
                    Log.d(TAG, "image received..not null");
                //save image in internal storage
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                Date now = new Date();
                String fileName = "testbitmap" + formatter.format(now) + ".png";
                /* //uncomment if using internal storage
                File file = new File(Environment.getExternalStorageDirectory() + fileName);
                FileOutputStream stream = new FileOutputStream(file);
                mergedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                stream.close();
                mergedImage.recycle();
                Toast.makeText(CustomGalleryActivity.this, "Image saved in storage", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "imaged saved in storage");
                */

                //save image to external storage
                File file1=null;
                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
                try {
                    File dir = new File(fullPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    FileOutputStream fOut = null;
                    file1 = new File(fullPath, fileName);
                    file1.createNewFile();
                    fOut = new FileOutputStream(file1);

                    // 100 means no compression, the lower you go, the stronger the compression
                    mergedImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    MediaStore.Images.Media.insertImage(getContentResolver(), file1.getAbsolutePath(), file1.getName(), file1.getName());
                    Toast.makeText(CustomGalleryActivity.this, "Image saved in storage", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"saved in storage");

                } catch (Exception e) {
                    Log.d("TAG", e.getMessage());
                }

                //create notification
                NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.rainbow)
                .setContentTitle("Collage created and saved in device!")
                .setContentText("Click to open the collage!");
                Intent resultIntent = new Intent(this, DisplayCollageActivity.class);
                resultIntent.putExtra("mergedImage",file1.toString());
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addParentStack(DisplayCollageActivity.class);
                // Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(1789, mBuilder.build());
                
                //Pop intent to display and share collage
                Bundle b = new Bundle();
                b.putString("mergedImage", file1.toString());
                Intent intent = new Intent(this, DisplayCollageActivity.class);
                intent.putExtras(b);
                startActivity(intent);

            } /*catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            catch(Exception e){
                Log.e(TAG," exception my god!");
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



    private Bitmap createSingle() {
        Log.d(TAG, "create sigle method");
        Bitmap bmp = Bitmap.createBitmap(size.x, size.y, Bitmap.Config.RGB_565);
        Log.d(TAG, "width=" + size.x + " height=" + size.y);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(Color.GRAY);
        Matrix m = new Matrix(size.x, size.y);
        Bitmap b;
        Cell cell;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inPreferredConfig = Bitmap.Config.RGB_565;
        //options.inJustDecodeBounds=true;//options.inPurgeable=true;
        //options.inInputShareable=true;
        Log.d(TAG, "options height=" + options.outHeight + " width=" + options.outWidth + " mimetype=" + options.outMimeType);
        //int inSampleSize=1;

        try {

            for (int i = 0; i < allPath.length; i++) {
                Log.d(TAG, "i=" + i);
                options.inJustDecodeBounds = true;
                InputStream fs = new FileInputStream(new File(allPath[i]));
                BitmapFactory.decodeStream(fs, null, options);
                fs.close();
                cell = m.getCell(i);
                int scale = 1;
                int REQUIRED_HEIGHT = cell.getBottom() - cell.getTop();
                int REQUIRED_WIDTH = cell.getRight() - cell.getLeft();
                while (options.outWidth / scale / 2 >= REQUIRED_WIDTH && options.outHeight / scale / 2 >= REQUIRED_HEIGHT)
                    scale *= 2;
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                //fs.close();
                fs = new FileInputStream(new File(allPath[i]));
                o2.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                //sometimes outofmemory
                b = BitmapFactory.decodeStream(fs, null, o2);
                //Log.d(TAG,"b height="+b.getHeight()+" b width="+b.getWidth());
                /*if(b==null)
                    Log.d(TAG," bmp null ");
                else
                    Log.d(TAG," not null bmp ");*/

                b = Bitmap.createScaledBitmap(b, cell.getRight() - cell.getLeft(), cell.getBottom() - cell.getTop(), true);
                //Log.d(TAG,"i="+i+" top=" + cell.getTop() + " left=" + cell.getLeft());
                canvas.save();
                //canvas.rotate(10);
                canvas.drawBitmap(b, cell.getLeft(), cell.getTop(), null);
                b.recycle();
                //b=null;
                //canvas.restore();
            }
            return bmp;
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.toString());
            //System.gc();
            //b=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //b=null;
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
            Toast.makeText(this, "No client available for sharing", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Oops no client available for sharing!");
        }
    }

    AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> l, View v, int position, long id) {
            Log.d(TAG, "adapterview mitemmulticlk listener");
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
        Log.d(TAG, "getgalleryphotos");
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

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


}
