package iiitd.ac.in.androidfaceproject;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;


public class MyApp extends Application {

    private ImageView img; // the image
    private RelativeLayout bgimg; // layout of the activity
    private Bitmap nav; // the image in the Bitmap format
    private Bitmap background; // background in the Bitmap format
    private BitmapDrawable bg; // background in the Drawable format

    public void loadBackground(int id) {
        background = BitmapFactory.decodeStream(getResources().openRawResource(id));
        bg = new BitmapDrawable(background);
        bgimg.setBackgroundDrawable(bg);
    }
    public void unloadBackground() {
        if (bgimg != null)
            bgimg.setBackgroundDrawable(null);
        if (bg!= null) {
            background.recycle();
        }
        bg = null;
    }

    public void setBackground(RelativeLayout i, int sourceid) {
        unloadBackground();
        bgimg = i;
        loadBackground(sourceid);
    }

    public void loadBitmap(int id) {
        nav = BitmapFactory.decodeStream(getResources().openRawResource(id));
        img.setImageBitmap(nav);
    }
    public void unloadBitmap() {
        if (img != null)
            img.setImageBitmap(null);
        if (nav!= null) {
            nav.recycle();
        }
        nav = null;
    }

    public void setImage(ImageView i, int sourceid) {
        unloadBitmap();
        img = i;
        loadBitmap(sourceid);
    }

}
