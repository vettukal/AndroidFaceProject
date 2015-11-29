package iiitd.ac.in.androidfaceproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.facepp.error.FaceppParseException;
import com.facepp.http.HttpRequests;
import com.facepp.http.PostParameters;
import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceAttribute;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.microsoft.projectoxford.face.contract.GenderEnum;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PhotoEditor extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
    }

    boolean lvlOneScrollView = false;
    boolean lvltwoScrollView = false;
    static boolean pos = true;
    static float fontSize = 0;
    HorizontalScrollView[] scrollViewsLvlOne;
    HorizontalScrollView[] scrollViewsLvlTwo;
    float discrete=0;
    float start=0;
    float end=100;
    float start_pos=0;
    int start_position=0;
    double brightnessValue, contrastValue, rgbValue[];
    Mat imageMat;
    final int PIC_CROP = 1;
    private RelativeLayout toolbox;

    private final int PICK_IMAGE = 1;
    private ProgressDialog detectionProgressDialog;

    private FaceServiceClient faceServiceClient =
            new FaceServiceClient("f6a16646879b4f0aa33c208521225faa");

    Bitmap logbitmap;
    static int range = 0;


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
        paintText.setTextSize(fontSize);
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

                String ageVal = Double.valueOf(attribute.age)+"(+-"+Integer.valueOf(range)+")";

                if(pos) {
                    canvas.drawText("" + ageVal + "/" + dGender, faceRectangle.left + (faceRectangle.width / 4), faceRectangle.top - 25, paintText);
                    pos = false;
                }
                else {
                    canvas.drawText("" + ageVal + "/" + dGender, faceRectangle.left + (faceRectangle.width / 4), faceRectangle.top + faceRectangle.height, paintText);
                    pos = true;
                }

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
        //Log.d("vincent", filePath);
        //Bitmap bitmap1 = BitmapFactory.decodeStream(si1);
        //tv1.setImageBitmap(yourSelectedImage);
        tv1.setImageResource(R.drawable.gallerypng);

        File imageFile = new File(filePath);

        File destination = new File(Environment
                .getExternalStorageDirectory(), "1445871154448" + ".jpg");
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_editor, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == PIC_CROP) {
                if (data != null) {
                    Toast.makeText(this, "Croped Image Saved in Gallery", Toast.LENGTH_LONG).show();

                    // get the returned data
                    /*Uri uri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    ImageView imageView = (ImageView) findViewById(R.id.imgView);
                    imageView.setImageBitmap(bitmap);
                    bitmap.recycle();
                    bitmap = null;
                    data = null*/;
                }
            }
        }
        catch(Exception e){
            Toast.makeText(this, "Crop Operation Failed", Toast.LENGTH_LONG).show();
        }

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

    private void handleFilter(String tag) {
        //First convert Bitmap to Mat
        imageMat = new Mat(logbitmap.getHeight(), logbitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = logbitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, imageMat);
        myBitmap32.recycle();
        logbitmap = null;

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
            logbitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(imageMat, logbitmap);

            //Set member to the resultBitmap. This member is displayed in an ImageView
            ImageView imageView = (ImageView) findViewById(R.id.imgView);
            imageView.setImageBitmap(logbitmap);

        } catch (Exception exception) {
            Toast.makeText(this, "Can't apply filter: " + tag, Toast.LENGTH_LONG).show();
            exception.printStackTrace();
        }

        finally {
            imageMat = null;
            //myBitmap32.recycle();
        }
        //myBitmap32.recycle();

    }



    public void onClickAdjButton(View view) {
        Log.d("vince " + this.getLocalClassName(), "Button at Adjustment Ribbon is clicked.");
        Log.d("vince " + this.getLocalClassName(), "adjustment name: " + view.getTag().toString());

        handleAdjustment(view.getTag().toString());
    }

    private void handleAdjustment(String tag) {
        //First convert Bitmap to Mat

        try {
        if(tag.equals("Brightness")) {
           double brightness = showBrightnessDialog();
        }
        else if (tag.equals("Contrast")) {
            double contrast = showContrastDialog();
            Log.d("vince " + contrast +"--"+ contrastValue, "Contrast value returned");
        }
        else if (tag.equals("Color")) {
            double[] rgb = showColorDialog();
        }
        else if (tag.equals("AutoFix"))  {
                autoFix();
        }
        else if (tag.equals("Crop"))  {
            //ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            //logbitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            //String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), logbitmap, "Title", null);
            Uri picUri = getImageUri(this,logbitmap);

            try {
                Intent cropIntent = new Intent("com.android.camera.action.CROP");
                // indicate image type and Uri
                cropIntent.setDataAndType(picUri, "image/*");
                // set crop properties
                cropIntent.putExtra("crop", "true");
                //indicate aspect of desired crop
                cropIntent.putExtra("aspectX", 1);
                cropIntent.putExtra("aspectY", 1);
                //indicate output X and Y
                cropIntent.putExtra("outputX", 800);
                cropIntent.putExtra("outputY", 800);
                // retrieve data on return
                cropIntent.putExtra("return-data", true);


                /*File f = new File(Environment.getExternalStorageDirectory(),
                        "/temporary_holder.jpg");
                try {
                    f.createNewFile();
                } catch (IOException ex) {
                    Log.e("io", ex.getMessage());
                }

                Uri uri = Uri.fromFile(f);

                cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);*/

                // start the activity - we handle returning in onActivityResult
                startActivityForResult(cropIntent, PIC_CROP);
            }
            // respond to users whose devices do not support the crop action
            catch (ActivityNotFoundException anfe) {
                // display an error message
                String errorMessage = "Whoops - your device doesn't support the crop action!";
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.show();
            }

        }

        } catch (Exception exception) {
            Toast.makeText(this, "Can't apply filter: " + tag, Toast.LENGTH_LONG).show();
            exception.printStackTrace();
        }


    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


//----------------------------------------------------------------------------------------------------------------------------Brightness Dialog --------------------------
    protected double showBrightnessDialog() {

        //set seekbar properties
        start=-10;		//you need to give starting value of SeekBar
        end=10;			//you need to give end value of SeekBar
        start_pos=0;		//you need to give starting position value of SeekBar

        start_position=(int) (((start_pos-start)/(end-start))*100);
        discrete = start_pos;

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.brightness_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final SeekBar seek=(SeekBar) promptView.findViewById(R.id.seekBar1);
        seek.setProgress(start_position);

    // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        brightnessValue = seek.getProgress();
                        double factor = brightnessValue/50;
                        Log.d("vince " + brightnessValue, "Brightness value ");
                        Log.d("vince " + factor, "Brightness factor ");

                        //changeBrightness(Math.abs((factor - 1) * 255));
                        changeBrightness(factor);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        return brightnessValue;
    }


    protected double showContrastDialog() {

        //set seekbar properties
        start=0;		//you need to give starting value of SeekBar
        end=10;			//you need to give end value of SeekBar
        start_pos=5;		//you need to give starting position value of SeekBar

        start_position=(int) (((start_pos-start)/(end-start))*100);
        discrete = start_pos;

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.contrast_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final SeekBar seek=(SeekBar) promptView.findViewById(R.id.seekBar2);
        seek.setProgress(start_position);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        contrastValue = seek.getProgress();
                        double factor = contrastValue/50;
                        Log.d("vince " + contrastValue, "Contrast value ");
                        Log.d("vince " + factor, "Contrast factor ");
                        changeContrast(factor);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        return contrastValue;

    }

    protected double[] showColorDialog() {

        //set seekbar properties
        start=-0;		//you need to give starting value of SeekBar
        end=10;			//you need to give end value of SeekBar
        start_pos=5;		//you need to give starting position value of SeekBar

        int start_position_red=(int) (((start_pos-start)/(end-start))*100);
        int start_position_green=(int) (((start_pos-start)/(end-start))*100);
        int start_position_blue=(int) (((start_pos-start)/(end-start))*100);
        discrete = start_pos;

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.color_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final SeekBar seek1=(SeekBar) promptView.findViewById(R.id.seekBar3);
        final SeekBar seek2=(SeekBar) promptView.findViewById(R.id.seekBar4);
        final SeekBar seek3=(SeekBar) promptView.findViewById(R.id.seekBar5);

        seek1.setProgress(start_position_red);
        seek2.setProgress(start_position_green);
        seek3.setProgress(start_position_blue);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        double redVal = seek1.getProgress();
                        double greenVal = seek2.getProgress();
                        double blueVal = seek3.getProgress();
                        rgbValue = new double[]{redVal/50,greenVal/50,blueVal/50};
                        changeColor(rgbValue);
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        return new double[]{seek1.getProgress(),seek2.getProgress(),seek3.getProgress()};
    }

    private Mat filterSepia(Mat imageMat) {

        Mat mSepiaKernel = new Mat(4, 4, CvType.CV_32F);
        mSepiaKernel.put(0, 0, 0.272f, 0.534f, 0.131f, 0f);
        mSepiaKernel.put(1, 0, 0.349f, 0.686f, 0.168f, 0f);
        mSepiaKernel.put(2, 0, 0.393f, 0.769f, 0.189f, 0f);
        mSepiaKernel.put(3, 0, 0.000f, 0.000f, 0.000f, 1f);
        Core.transform(imageMat, imageMat, mSepiaKernel);

        mSepiaKernel  = null;
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
        Core.merge(channels, imageMat);
        channels = null;
        tempMat = null;
        return imageMat;
    }

    private Mat filterUnicolor(Mat imageMat) {
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGB2HSV, 4);
        return imageMat;
    }

    private Mat filterInverse(Mat imageMat) {
        Imgproc.cvtColor(imageMat, imageMat, Imgproc.COLOR_RGB2GRAY, 4);
        Core.bitwise_not(imageMat, imageMat);
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
        } else if (tag.equals("Save")) {
            //DONE: Take every ribbon other than filter and make invisible
            makeOtherRibbonGone("Save");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(getIntent().getStringExtra("filename"));
                logbitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Toast.makeText(this, "Image Saved", Toast.LENGTH_LONG).show();
        }else if (tag.equals("Undo")) {
            //DONE: Take every ribbon other than filter and make invisible
            makeOtherRibbonGone("Undo");
            String filePath = getIntent().getStringExtra("filename");
            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
            File imageFile = new File(filePath);
            if (imageFile.exists()) {
                Log.d("vincent", "image file exists");
                Toast.makeText(this, "Image file loaded", Toast.LENGTH_LONG).show();
                ImageView imageView = (ImageView) findViewById((R.id.imgView));
                Log.d("vincent", imageFile.getAbsolutePath());
                imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));


                logbitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
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

        }

        else if (tag.equals("AgeGender")) {
            Log.d("vince", " Going to detect the age and gender");
            //Frame(logbitmap);
            FaceppDetect faceppDetect = new FaceppDetect();
            faceppDetect.setDetectCallback(new DetectCallback() {

                public void detectResult(JSONObject rst) {
                    //Log.v(TAG, rst.toString());

                    Log.v("My result", rst.toString());

                    try {
                        //find out all faces
                        final int count = rst.getJSONArray("face").length();
                        final Face[] faces = new Face[count];
                        for (int i = 0; i < count; ++i) {
                            faces[i] = new Face();
                            faces[i].attributes= new FaceAttribute();
                            faces[i].faceRectangle = new FaceRectangle();
                            float x, y, w, h;
                            //get the center point
                            x = (float)rst.getJSONArray("face").getJSONObject(i)
                                    .getJSONObject("position").getJSONObject("center").getDouble("x");
                            y = (float)rst.getJSONArray("face").getJSONObject(i)
                                    .getJSONObject("position").getJSONObject("center").getDouble("y");

                            //get face size
                            w = (float)rst.getJSONArray("face").getJSONObject(i)
                                    .getJSONObject("position").getDouble("width");
                            h = (float)rst.getJSONArray("face").getJSONObject(i)
                                    .getJSONObject("position").getDouble("height");

                            //change percent value to the real size
                            x = x / 100 * logbitmap.getWidth();
                            w = w / 100 * logbitmap.getWidth() * 0.7f;
                            y = y / 100 * logbitmap.getHeight();
                            h = h / 100 * logbitmap.getHeight() * 0.7f;

                            String gender = rst.getJSONArray("face").getJSONObject(i).getJSONObject("attribute").getJSONObject("gender").getString("value");
                            double genderConfidence = rst.getJSONArray("face").getJSONObject(i).getJSONObject("attribute").getJSONObject("gender").getDouble("confidence");

                            if(faces[i]==null)
                                Log.d("vince","faces[i] is mull");
                            if(faces[i].attributes==null)
                                Log.d("vince","faces[i].attributes==null");

                            if(gender.equals("Male"))
                                faces[i].attributes.gender = GenderEnum.male;
                            else
                                faces[i].attributes.gender = GenderEnum.female;

                            String age = rst.getJSONArray("face").getJSONObject(i).getJSONObject("attribute").getJSONObject("age").getString("value");
                            range = rst.getJSONArray("face").getJSONObject(i).getJSONObject("attribute").getJSONObject("age").getInt("range");


                            // vinceVasu
                            faces[i].faceRectangle.height = (int) h*2;
                            faces[i].faceRectangle.width = (int) w*2;
                            faces[i].faceRectangle.left = (int)  (x-w);
                            faces[i].faceRectangle.top = (int) (y-h);

                            faces[i].attributes.age = Double.valueOf(age);



                            String displayText = gender+"/"+age+"(+-"+range+")";

                            fontSize = w*h*0.02F;
                            if(fontSize>50F)
                                fontSize = 50F;
                            else if(fontSize<25F)
                                fontSize = 25F;

                            //textPaint.setTextSize(fontSize);
                        }

                        PhotoEditor.this.runOnUiThread(new Runnable() {

                            public void run() {
                                //show the image
                                //vincentVasu
                                ImageView imageView = (ImageView) findViewById(R.id.imgView);
                                imageView.setImageBitmap(drawFaceRectanglesOnBitmap(logbitmap, faces));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        ;
                    }

                }
            });
            faceppDetect.detect(logbitmap);

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

    public void changeBrightness(double factor){
        imageMat = new Mat(logbitmap.getHeight(), logbitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = logbitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, imageMat);
        myBitmap32.recycle();
        myBitmap32 = null;
        logbitmap = null;

        Log.d("vince", (float) factor + " Going to brighten the image");

        Mat mBrightnessKernel = new Mat(4, 4, CvType.CV_32F);
        mBrightnessKernel.put(0, 0, (float)factor, 0.000f,  0.000f, 0f);
        mBrightnessKernel.put(1, 0, 0.000f, (float)factor,  0.000f, 0f);
        mBrightnessKernel.put(2, 0, 0.000f, 0.000f,  (float)factor, 0f);
        mBrightnessKernel.put(3, 0, 0.000f, 0.000f, 0.000f, 1f);

        Core.transform(imageMat, imageMat, mBrightnessKernel);
        mBrightnessKernel = null;
        logbitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageMat, logbitmap);
        imageMat = null;
        //Set member to the resultBitmap. This member is displayed in an ImageView
        ImageView imageView = (ImageView) findViewById(R.id.imgView);
        imageView.setImageBitmap(logbitmap);

    }


    public void changeContrast(double factor){
        imageMat = new Mat(logbitmap.getHeight(), logbitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = logbitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, imageMat);
        myBitmap32.recycle();
        myBitmap32 = null;
        logbitmap = null;

        imageMat.convertTo(imageMat, -1, factor, 0); //adjust the contrast (double)


        logbitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageMat, logbitmap);
        imageMat = null;
        //Set member to the resultBitmap. This member is displayed in an ImageView
        ImageView imageView = (ImageView) findViewById(R.id.imgView);
        imageView.setImageBitmap(logbitmap);

    }

    public void autoFix(){
        changeContrast(1.5);
    }


    public void changeColor(double factor[]){
        imageMat = new Mat(logbitmap.getHeight(), logbitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap myBitmap32 = logbitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, imageMat);
        myBitmap32.recycle();
        myBitmap32 = null;
        logbitmap = null;


        Mat mBrightnessKernel = new Mat(4, 4, CvType.CV_32F);
        mBrightnessKernel.put(0, 0, (float) factor[0], 0.000f, 0.000f, 0f);
        mBrightnessKernel.put(1, 0, 0.000f, (float) factor[1], 0.000f, 0f);
        mBrightnessKernel.put(2, 0, 0.000f, 0.000f,  (float)factor[2], 0f);
        mBrightnessKernel.put(3, 0, 0.000f, 0.000f, 0.000f, 1f);

        Core.transform(imageMat, imageMat, mBrightnessKernel);
        mBrightnessKernel = null;
        logbitmap = Bitmap.createBitmap(imageMat.cols(), imageMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(imageMat, logbitmap);
        imageMat = null;
        //Set member to the resultBitmap. This member is displayed in an ImageView
        ImageView imageView = (ImageView) findViewById(R.id.imgView);
        imageView.setImageBitmap(logbitmap);

    }

    private class FaceppDetect {
        DetectCallback callback = null;

        public void setDetectCallback(DetectCallback detectCallback) {
            callback = detectCallback;
        }

        public void detect(final Bitmap image) {

            new Thread(new Runnable() {

                public void run() {
                    HttpRequests httpRequests = new HttpRequests("36d55d5e02d03582b677b42365073fa8", "KY_k9ldTUw0WgQ-rBAxFPvhztMQhLnOD", true, false);
                    //Log.v(TAG, "image size : " + img.getWidth() + " " + img.getHeight());

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    float scale = Math.min(1, Math.min(600f / logbitmap.getWidth(), 600f / logbitmap.getHeight()));
                    android.graphics.Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    Bitmap imgSmall = Bitmap.createBitmap(logbitmap, 0, 0, logbitmap.getWidth(), logbitmap.getHeight(), matrix, false);
                    //Log.v(TAG, "imgSmall size : " + imgSmall.getWidth() + " " + imgSmall.getHeight());

                    imgSmall.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] array = stream.toByteArray();

                    try {
                        //detect
                        JSONObject result = httpRequests.detectionDetect(new PostParameters().setImg(array));
                        //finished , then call the callback function
                        if (callback != null) {
                            callback.detectResult(result);
                        }
                    } catch (FaceppParseException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    interface DetectCallback {
        void detectResult(JSONObject rst);
    }
}
