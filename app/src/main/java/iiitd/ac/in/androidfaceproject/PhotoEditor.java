package iiitd.ac.in.androidfaceproject;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
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

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceAttribute;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.GenderEnum;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PhotoEditor extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
    }

    boolean lvlOneScrollView = false;
    boolean lvltwoScrollView = false;
    HorizontalScrollView[] scrollViewsLvlOne;
    HorizontalScrollView[] scrollViewsLvlTwo;

    private RelativeLayout toolbox;

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;

    private FaceServiceClient faceServiceClient =
            new FaceServiceClient("f6a16646879b4f0aa33c208521225faa");

    Bitmap logbitmap;

    // Detect faces by uploading face images
// Frame faces after detection

    private static Bitmap drawFaceRectanglesOnBitmap(Bitmap originalBitmap, Face[] faces) {
        Bitmap bitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        int stokeWidth = 8;
        paint.setStrokeWidth(stokeWidth);
        Paint paintText = new Paint();
        //paintText.setAntiAlias(true);
        //paintText.setStyle(Paint.Style.STROKE);
        paintText.setColor(Color.YELLOW);
        paintText.setTextSize(80);
        if (faces != null) {
            for (Face face : faces) {
                FaceRectangle faceRectangle = face.faceRectangle;
                canvas.drawRect(
                        faceRectangle.left,
                        faceRectangle.top,
                        faceRectangle.left + faceRectangle.width,
                        faceRectangle.top + faceRectangle.height,
                        paint);

                FaceAttribute attribute = face.attributes;

                Log.d("vince PhotoEditor", "Gender of the face: " + attribute.gender);
                Log.d("vince PhotoEditor", "Age of the face: " + attribute.age);
                String dGender = "";
                if (attribute.gender == GenderEnum.female) {
                    dGender = "F";
                } else {
                    dGender = "M";
                }
                canvas.drawText("" + (int) attribute.age + "/" + dGender, faceRectangle.left + (faceRectangle.width / 4), faceRectangle.top - 25, paintText);

            }
        }
        return bitmap;
    }


    private void detectAndFrame(final Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        AsyncTask<InputStream, String, Face[]> detectTask =
                new AsyncTask<InputStream, String, Face[]>() {
                    @Override
                    protected Face[] doInBackground(InputStream... params) {
                        try {
                            publishProgress("Detecting...");
                            Face[] result = faceServiceClient.detect(
                                    params[0], false, true, true, false);
                            if (result == null) {
                                publishProgress("Detection Finished. Nothing detected");
                                return null;
                            }
                            publishProgress(
                                    String.format("Detection Finished. %d face(s) detected",
                                            result.length));
                            return result;
                        } catch (Exception e) {
                            publishProgress("Detection failed");
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        detectionProgressDialog.show();

                    }

                    @Override
                    protected void onProgressUpdate(String... progress) {
                        detectionProgressDialog.setMessage(progress[0]);

                    }

                    @Override
                    protected void onPostExecute(Face[] result) {
                        detectionProgressDialog.dismiss();
                        if (result == null) return;
                        ImageView imageView = (ImageView) findViewById(R.id.imgView);
                        imageView.setImageBitmap(drawFaceRectanglesOnBitmap(imageBitmap, result));
                        imageBitmap.recycle();

                    }
                };
        detectTask.execute(inputStream);
    }


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
        tv1 = (ImageView) findViewById(R.id.imgView);
        // tv1.setImageResource(R.id.imageButtonCamera);
        String filePath = getIntent().getStringExtra("filename");
        //Toast.makeText(this,filePath,Toast.LENGTH_LONG).show();
        Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
        Log.d("vincent", filePath);
        //Bitmap bitmap1 = BitmapFactory.decodeStream(si1);
        //tv1.setImageBitmap(yourSelectedImage);
        tv1.setImageResource(R.drawable.gallerypng);

        File imageFile = new File(filePath);

        File destination = new File(Environment
                .getExternalStorageDirectory(), "1445871154448" + ".jpg");
        //imageFile = destination;

        /**
         scrollViewsLvlOne = new HorizontalScrollView [] {(HorizontalScrollView) findViewById(R.id.hsv_filters),
         (HorizontalScrollView) findViewById(R.id.hsv_adjustments)};
         */
        if (imageFile.exists()) {
            Log.d("vincent", "image file exists");
            Toast.makeText(this, "image file exists", Toast.LENGTH_LONG).show();
            ImageView imageView = (ImageView) findViewById((R.id.imgView));
            Log.d("vincent", imageFile.getAbsolutePath());
            imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));


            logbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            //Log.d("vincent",logbitmap.toString());
            if (logbitmap == null) {
                Log.d("vincent", "bitmap is null");

            } else {
                Log.d("vincent", "bitmap is NOT NOT null");
            }
            imageView.setImageBitmap(logbitmap);
            Log.d("vincent", "file decoded");
        } else {
            Log.d("vincent", "image file not NOT exists");
        }
        detectionProgressDialog = new ProgressDialog(this);
    }

    /**
     * public void onClickAdjstmentBtn(View v)
     * {
     * HorizontalScrollView hsvAdjustments = (HorizontalScrollView) findViewById(R.id.hsv_adjustments);
     * if(hsvAdjustments.getVisibility() == View.VISIBLE)
     * hsvAdjustments.setVisibility(View.GONE);
     * else if(lvlOneScrollView) {
     * turnOffScrollView(1);
     * hsvAdjustments.setVisibility(View.VISIBLE);
     * lvlOneScrollView = true;
     * }
     * else{
     * hsvAdjustments.setVisibility(View.VISIBLE);
     * lvlOneScrollView = true;
     * }
     * <p/>
     * <p/>
     * <p/>
     * //        Toast.makeText(this, "Clicked on Adjustment Button", Toast.LENGTH_LONG).show();
     * }
     * <p/>
     * <p/>
     * public void onClickFilterBtn(View v)
     * {
     * HorizontalScrollView hsvFilters = (HorizontalScrollView) findViewById(R.id.hsv_filters);
     * if(hsvFilters.getVisibility() == View.VISIBLE)
     * hsvFilters.setVisibility(View.GONE);
     * else if(lvlOneScrollView) {
     * turnOffScrollView(1);
     * hsvFilters.setVisibility(View.VISIBLE);
     * lvlOneScrollView = true;
     * }
     * else{
     * hsvFilters.setVisibility(View.VISIBLE);
     * lvlOneScrollView = true;
     * }
     * //Toast.makeText(this, "Clicked on filter Button", Toast.LENGTH_LONG).show();
     * }
     * <p/>
     * <p/>
     * private void turnOffScrollView(int i) {
     * if(i==1)
     * {
     * for(HorizontalScrollView temp : scrollViewsLvlOne)
     * temp.setVisibility(View.GONE);
     * lvlOneScrollView = false;
     * }
     * <p/>
     * <p/>
     * }
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

    public void toggleToolbox(View v) {
        Log.d("vince " + this.getLocalClassName(), " image clicked and inside the toggleToolBox");

        if (toolbox.getVisibility() == View.VISIBLE) {
            toolbox.setVisibility(View.GONE);
        } else {
            toolbox.setVisibility(View.VISIBLE);
        }
    }

    public void onClickEffectButton(View view) {
        Log.d("vince " + this.getLocalClassName(), "Button at bottom is clicked.");
        Log.d("vince " + this.getLocalClassName(), "view name: " + view.getTag().toString());

        handleLevel1(view.getTag().toString());
    }

    public void onClickFilterEffectButton(View view) {
        Log.d("vince " + this.getLocalClassName(), "Button at Filter Ribbon is clicked.");
        Log.d("vince " + this.getLocalClassName(), "filter name: " + view.getTag().toString());

        handleFilter(view.getTag().toString());
    }


    public void onClickAdjButton(View view) {
        Log.d("vince " + this.getLocalClassName(), "Button at Adjustment Ribbon is clicked.");
        Log.d("vince " + this.getLocalClassName(), "adjustment name: " + view.getTag().toString());

        //handleLevel1(view.getTag().toString());
    }

    private void handleFilter(String tag) {
        //First convert Bitmap to Mat
        Mat imageMat = new Mat(logbitmap.getHeight(), logbitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = logbitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, imageMat);

        try {
            if (tag.equals("Sepia")) {
                Log.d("vince", " Going to apply Sepia filter");
                imageMat = filterSepia(imageMat);
            }
            else if (tag.equals("Monochrome")) {
                Log.d("vince", " Going to apply monochrome filter");
                imageMat = filterMonochrome(imageMat);
            }
            else if (tag.equals("HDR")) {
                Log.d("vince", " Going to apply HDR filter");
                imageMat = filterHdr(imageMat);
            }
            else if (tag.equals("Monocolor")) {
                Log.d("vince", " Going to apply Unicolor filter");
                imageMat = filterUnicolor(imageMat);
            }
            else if (tag.equals("Inverse")) {
                Log.d("vince", " Going to apply Inverse filter");
                imageMat = filterInverse(imageMat);
            }

            //Then convert the processed Mat to Bitmap
            Bitmap resultBitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(imageMat, resultBitmap);

            //Set member to the resultBitmap. This member is displayed in an ImageView
            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(resultBitmap);

        } catch (Exception exception) {
            Toast.makeText(this, "Can't apply filter: " + tag, Toast.LENGTH_LONG).show();
            exception.printStackTrace();
        }
        myBitmap32.recycle();

    }

    private Mat filterSepia(Mat imageMat) {

        Mat mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, 0.272f, 0.534f, 0.131f, 0f);
        mSepiaKernel.put(1, 0, 0.349f, 0.686f, 0.168f, 0f);
        mSepiaKernel.put(2, 0, 0.393f, 0.769f, 0.189f, 0f);
        mSepiaKernel.put(3, 0, 0.000f, 0.000f, 0.000f, 1f);
/*
        Mat mSepiaKernel = new Mat(3, 3, CvType.CV_32F);
        mSepiaKernel.put(0, 0, 0.393f, 0.349f, 0.272f);
        mSepiaKernel.put(1, 0, 0.769f, 0.686f, 0.534f);
        mSepiaKernel.put(2, 0, 0.189f, 0.168f, 0.131f);*/

        Core.transform(imageMat, imageMat, mSepiaKernel);


        return imageMat;
    }

    private Mat filterHdr(Mat imageMat) {
        ArrayList<Mat> channels = new ArrayList<Mat>();
        Core.split(imageMat, channels);
        Mat tempMat = null;
        int i= 0;
        for(i= 0 ; i < 3 ; i++){
        tempMat = new Mat(imageMat.rows(), imageMat.cols(), CvType.CV_8U, new Scalar(4));
        Imgproc.equalizeHist(channels.get(i), tempMat);
        channels.set(i, tempMat);
        }
        Core.merge(channels,imageMat);

        return imageMat;
    }

    private Mat filterUnicolor(Mat imageMat) {
        /*Size s = new Size(3,3);
        Imgproc.GaussianBlur(imageMat, imageMat, s, 2);*/
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGB2HSV, 4);
        return imageMat;
    }

    private Mat filterInverse(Mat imageMat) {
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGB2GRAY, 4);
        Core.bitwise_not(imageMat,imageMat);
        return imageMat;
    }

    private Mat filterMonochrome(Mat imageMat) {
        //convert RGB to Grayscale
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGB2GRAY, 4);
        return imageMat;
    }

    private void handleLevel1(String tag) {

        // TODO: check whether the tag is valid or not.
        // doing the dirty way of just applying the effect without checking

        if (tag.equals("Adjustments")) {
            //DONE: Take every ribbon other than adjustments and make invisible
            makeOtherRibbonGone("Adjustments");
            LinearLayout adjustPop = (LinearLayout) findViewById(R.id.effects_holder2);
            if (adjustPop.getVisibility() == View.VISIBLE) {
                adjustPop.setVisibility(View.GONE);
            } else {
                adjustPop.setVisibility(View.VISIBLE);
            }

        } else if (tag.equals("Filters")) {
            //DONE: Take every ribbon other than filter and make invisible
            makeOtherRibbonGone("Filters");
            LinearLayout adjustPop = (LinearLayout) findViewById(R.id.effects_holder3);
            if (adjustPop == null) {
                Log.d("vince " + this.getLocalClassName(), " adjustPop is null");
                return;
            }
            if (adjustPop.getVisibility() == View.VISIBLE) {
                adjustPop.setVisibility(View.GONE);
            } else {
                adjustPop.setVisibility(View.VISIBLE);
            }
        } else if (tag.equals("AgeGender")) {
            Log.d("vince", " Going to detect the age and gender");
            detectAndFrame(logbitmap);

        }
    }

    private void makeOtherRibbonGone(String ribbonName) {
        HashMap<String, Integer> hm = new HashMap<>();
        hm.put("Adjustments", R.id.effects_holder2);
        hm.put("Filters", R.id.effects_holder3);

        for (String key : hm.keySet()) {
            if (key.equalsIgnoreCase(ribbonName)) {
                continue;
            }

            int idRibbon = hm.get(key);
            LinearLayout adjustPop = (LinearLayout) findViewById(idRibbon);
            adjustPop.setVisibility(View.GONE);

        }


    }
}
