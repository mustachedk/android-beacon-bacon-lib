package dk.mustache.beaconbacon.customviews;

/* CLASS NAME GOES HERE

Copyright (c) 2017 Mustache ApS

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;

import java.util.List;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;

public class CustomMapView extends AppCompatImageView {

    //TOD Quality of POIs reduced on too much scaling - fix

    public static final String TAG = "Custom Map View";

    private float scaleFactor = 1.0f;
    private final float MIN_ZOOM = 0.5f;
    private final float MAX_ZOOM = 10.0f;

    private Context context;

    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;

    Matrix matrix = new Matrix();
    Matrix inverse = new Matrix();

    Bitmap bitmap;
    CustomPoiView[] pois;
    CustomPoiView findTheBookObject;

    public CustomMapView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CustomMapView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CustomMapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(context, simpleOnGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        scaleGestureDetector = new ScaleGestureDetector(context, simpleOnScaleGestureListener);
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        scaleFactor = 1.0f;

        //Fit image to view
        if(this.bitmap != null)
            fitImageToView();

        //Update view
//        invalidate();
    }

    public void setPois(List<CustomPoiView> customPoiViews, boolean invalidate) {
        //Create custom poi views base don list
        if(customPoiViews != null) {
            pois = new CustomPoiView[customPoiViews.size()];
            customPoiViews.toArray(pois);

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);


            float scaleX = 1.0f;

            if (windowManager != null) {
                //Get screen dimensions
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);

                scaleX = (float) metrics.widthPixels / bitmap.getWidth();
                float scaleY = (float) metrics.heightPixels / bitmap.getHeight();
                scaleX = scaleY = Math.min(scaleX, scaleY);
            }

            //Change radius of poiArray based on scale
            for (CustomPoiView poi : pois) {
                poi.radius = poi.radius / scaleFactor * scaleX;
                poi.scaleFactor = poi.scaleFactor / scaleFactor * scaleX;
            }

            //Update view
            if(invalidate)
                invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int desiredWidth = metrics.widthPixels;
        int desiredHeight = metrics.heightPixels;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        Display display = w.getDefaultDisplay();
//        DisplayMetrics metrics = new DisplayMetrics();
//        display.getMetrics(metrics);
//        Log.d("DensityDpi", metrics.density + "");
//
//        if(this.mapBitmap != null) {
//            setMeasuredDimension(mapBitmap.getWidth(), mapBitmap.getHeight());
//        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.concat(matrix);

        if(bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null); //Padding has already been applied

            /*
            List<CustomPoiView> list = new ArrayList<>();
            for (int i=0; i<poiArray.length; i++) {
                list.add(poiArray[i]);
            }
            setMapPois(list, true);
            */

            if (pois != null) {
                for (CustomPoiView poi : pois) {
                    poi.draw(canvas);
                }
            }

            if (findTheBookObject != null)
                findTheBookObject.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    //Gesture detector
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //TODO Limit how far we can drag the view
            //Translate the map
            matrix.postTranslate(-distanceX, -distanceY);

            //Update view
            invalidate();

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            matrix.invert(inverse);

            float[] pts = {
                    event.getX(),
                    event.getY()
            };

            inverse.mapPoints(pts);

            String name = "NO POI";

            if(findTheBookObject != null) {
                if (findTheBookObject.contains(pts[0], pts[1])) {
                    name = findTheBookObject.toString();
                    if(findTheBookObject.infoWindowText.getVisibility() == GONE)
                        findTheBookObject.infoWindowText.setVisibility(VISIBLE);
                    else
                        findTheBookObject.infoWindowText.setVisibility(GONE);

                    findTheBookObject.infoWindowText.setText(name);

                    invalidate();
                }
            }

            if(pois != null) {
                for (CustomPoiView poi : pois) {
                    if (poi.contains(pts[0], pts[1])) {
                        name = poi.toString();
                        if(poi.infoWindowText.getVisibility() == GONE)
                            poi.infoWindowText.setVisibility(VISIBLE);
                        else
                            poi.infoWindowText.setVisibility(GONE);

                        poi.infoWindowText.setText(name);

                        invalidate();
                        break;
                    }
                }
            }

            //TODO - show info window on this poi
            Log.d(TAG, "Tap inside " + name);

            return true;
        }
    };

    //Scale detector
    private ScaleGestureDetector.SimpleOnScaleGestureListener simpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Update scale factor
            scaleFactor *= detector.getScaleFactor();

            //Only apply scale to matrix if we're within zoom bounds
            if(scaleFactor >= MIN_ZOOM && scaleFactor <= MAX_ZOOM) {
                float scale = detector.getScaleFactor();

                //Scale the mapBitmap
                matrix.postScale(scale, scale, detector.getFocusX(), detector.getFocusY());

                //Change radius of poiArray based on scale
                if(pois != null) {
                    for (CustomPoiView poi : pois) {
                        poi.radius = poi.radius / scale;
                        poi.scaleFactor = poi.scaleFactor / scale;
                    }
                }

                if(findTheBookObject != null) {
                    findTheBookObject.radius = findTheBookObject.radius / scale;
                    findTheBookObject.scaleFactor = findTheBookObject.scaleFactor / scale;
                }

                //Update view
                invalidate();
            }

            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            return true;
        }
    };

    public void moveViewToBook() {
        //Capture current X/Y values
        float[] values = new float[9];
        matrix.getValues(values);
        float currentX = values[Matrix.MTRANS_X];
        float currentY = values[Matrix.MTRANS_Y];

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        if (windowManager != null) {
            //Get screen dimensions
            Display display = windowManager.getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);

            //Translate the map
            //TODO Finalize the calulation to center the book on screen



            matrix.postTranslate(metrics.widthPixels/2 - findTheBookObject.cx, metrics.heightPixels/2 - findTheBookObject.cy);

            //Update view
            invalidate();
        }
    }

    private void fitImageToView() {
        if(bitmap != null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            if (windowManager != null) {
                //Get screen dimensions
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);

                //Define the scaling for Fit Center
                float scaleX = (float) metrics.widthPixels / bitmap.getWidth();
                float scaleY = (float) metrics.heightPixels / bitmap.getHeight();
                scaleX = scaleY = Math.min(scaleX, scaleY);

                //Evaluate remaining redundant space
                float redundantXSpace = this.getWidth() - (scaleX * bitmap.getWidth());
                float redundantYSpace = this.getHeight() - (scaleY * bitmap.getHeight());

                //Set scale and translate image to center
                matrix.setScale(scaleX, scaleY);
                matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
                //Update view
//                invalidate();
            }
        }
    }

    public void setFindTheBook(Bitmap image, BBResponseObject responseObject) {
        findTheBookObject = null;

        //Create custom poi views base don list
        if(responseObject != null) {
            Bitmap bitmap = image;

            //Default to ic_book if image == null
            if(bitmap == null)
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_book);

            findTheBookObject = new CustomPoiView(context, bitmap,
                    responseObject.getData().get(0).getLocation().getPosX(),
                    responseObject.getData().get(0).getLocation().getPosY(),
                    responseObject.getRadius(),
                    BeaconBaconManager.getInstance().getRequestObject().getTitle(),
                    true);

            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            float scaleX = 1.0f;

            if (windowManager != null) {
                //Get screen dimensions
                Display display = windowManager.getDefaultDisplay();
                DisplayMetrics metrics = new DisplayMetrics();
                display.getMetrics(metrics);

                scaleX = (float) metrics.widthPixels / bitmap.getWidth();
                float scaleY = (float) metrics.heightPixels / bitmap.getHeight();
                scaleX = scaleY = Math.min(scaleX, scaleY);
            }

            //Change radius of poiArray based on scale
//            int radius = findTheBookAreaObject.radius != 0 ? (int) findTheBookAreaObject.radius : 10;
            int radius = 6;

            findTheBookObject.radius = radius / scaleFactor * scaleX;

            //Update view
//            invalidate();
        } else {
            //Refresh view to show no FindTheBook Object because it does not exist
            invalidate();
        }
    }
}
