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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;

public class MapHolderView extends AppCompatImageView {
    private Context context;

    private final float MIN_ZOOM = 0.5f;
    private final float MAX_ZOOM = 10.0f;
    private float scaleFactor = 1.0f;

    public PoiHolderView poiHolderView;

    private DisplayMetrics metrics;

    private float scaleInit;
    private float xTranslationInit;
    private float yTranslationInit;

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    Matrix matrix = new Matrix();
    Matrix inverse = new Matrix();
    List<CustomPoiView> areaCustomPoiViews = new ArrayList<>();

    Bitmap mapBitmap;


    //Constructors & Initialization
    public MapHolderView(Context context) {
        super(context);
        init(context);
    }

    public MapHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MapHolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        gestureDetector = new GestureDetector(context, simpleOnGestureListener);
        gestureDetector.setIsLongpressEnabled(false);
        scaleGestureDetector = new ScaleGestureDetector(context, simpleOnScaleGestureListener);

        //Get screen dimensions
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            metrics = new DisplayMetrics();
            display.getMetrics(metrics);
        }
    }



    //Draw & Touch
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.concat(matrix);

        if(mapBitmap != null) {
            canvas.drawBitmap(mapBitmap, 0, 0, null);

            poiHolderView.mapWasRedrawn();
        }

        if (areaCustomPoiViews != null) {
            for (CustomPoiView poi : areaCustomPoiViews) {
                poi.draw(canvas);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }


    private int translateX = 0;
    private int translateY = 0;
    //Gesture and Scale Detectors
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //TODO Limit how far we can drag the view
            matrix.postTranslate(-distanceX, -distanceY);
            poiHolderView.mapWasTranslated(-distanceX, -distanceY);
            invalidate();

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            poiHolderView.poiWasClicked(event.getX(), event.getY());

            return true;
        }
    };

    private float translationX;
    private float translationY;
    private ScaleGestureDetector.SimpleOnScaleGestureListener simpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            if(scaleFactor >= MIN_ZOOM && scaleFactor <= MAX_ZOOM) {
                float scale = detector.getScaleFactor();

                //Scale the Matrix for MapHolderView
                matrix.postScale(scale, scale, 0, 0);

                //Generate translations for the scaling
                translationX = ((metrics.widthPixels * scale - metrics.widthPixels) / 2);
                translationY = ((metrics.heightPixels * scale - metrics.heightPixels) / 2);
                matrix.postTranslate(-translationX, -translationY);

                //Notify POIs map was scaled
                poiHolderView.mapWasScaled(scale, translationX, translationY);

                invalidate();
            }

            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            return true;
        }
    };



    //Setup Map
    public void setImageBitmap(Bitmap bitmap) {
        this.mapBitmap = bitmap;

        //Reset the scale factor when we load a new place/floor
        scaleFactor = 1.0f;

        //Fit image to view
        if(this.mapBitmap != null)
            fitMapToView();
    }

    private void fitMapToView() {
        //Define the scaling for Fit Center
        float scaleX = (float) metrics.widthPixels / mapBitmap.getWidth();
        float scaleY = (float) metrics.heightPixels / mapBitmap.getHeight();
        scaleInit = scaleX = scaleY = Math.min(scaleX, scaleY);

        //Evaluate remaining redundant space
        float redundantXSpace = this.getWidth() - (scaleX * mapBitmap.getWidth());
        float redundantYSpace = this.getHeight() - (scaleY * mapBitmap.getHeight());
        xTranslationInit = redundantXSpace/2;
        yTranslationInit = redundantYSpace/2;


        //Set scale and translate image to center
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
    }



    //Setup POIs
    public void setMapPois(List<CustomPoiView> customPoiViews) {
        if(customPoiViews != null) {
            List<CustomPoiView> poiOnlyViews = new ArrayList<>();

            for (CustomPoiView poi : customPoiViews) {
                if (poi.iconBitmap != null)
                    poiOnlyViews.add(poi);
                else
                    areaCustomPoiViews.add(poi);
            }

            poiHolderView.setupPois(poiOnlyViews, scaleInit, xTranslationInit, yTranslationInit);
        }
    }



    //Find the Book
    public void setFindTheBook(Bitmap icon, BBResponseObject responseObject) {
        if (responseObject != null) {
            poiHolderView.setupFindTheBook(icon, responseObject, scaleInit, xTranslationInit, yTranslationInit);

            if (poiHolderView.findTheBookObject != null) {
                for(int i = 0; i< BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size(); i++) {
                    if(BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getFloor().getId() == BeaconBaconManager.getInstance().getCurrentFloorId()) {

                        scrollToBook();
                        break;
                    }
                }
            }
        }
    }

    public void scrollToBook() {
        //Translate the map
        matrix.postTranslate(metrics.widthPixels/2 - poiHolderView.findTheBookObject.cx, metrics.heightPixels/2 - poiHolderView.findTheBookObject.cy);
        poiHolderView.mapWasTranslated(metrics.widthPixels/2 - poiHolderView.findTheBookObject.cx, metrics.heightPixels/2 - poiHolderView.findTheBookObject.cy);

        //Update view
        invalidate();
    }
}
