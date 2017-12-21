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
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;
import dk.mustache.beaconbacon.enums.PoiType;

import static android.graphics.Matrix.*;
import static dk.mustache.beaconbacon.utils.Converter.dpToPx;

public class MapHolderView extends AppCompatImageView {
    private Context context;

    private float MAX_DRAG = 300.f;
    private float MAX_DRAG_BOTTOM = 500.f;
    private final float MIN_ZOOM = 1.0f;
    private float MAX_ZOOM = 1.0f;
    private float scaleFactor = 1.0f;

    private DisplayMetrics metrics;

    private Bitmap mapBitmap;
    private Matrix matrix = new Matrix();
    public PoiHolderView poiHolderView;

    private float scaleInit;
    private float xTranslationInit;
    private float yTranslationInit;
//    private int areaInfoY = (int) dpToPx(70);

    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private List<CustomPoiView> areaCustomPoiViews = new ArrayList<>();
//    private List<CustomAreaInfoView> areaPOIInfoWindows = new ArrayList<>();
    public CustomPoiView findTheBookAreaObject;
    private BBResponseObject findTheBookResponseObject;



    //region Constructors & Initialization
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
            MAX_DRAG = (float)(metrics.widthPixels * 0.15);
            MAX_DRAG_BOTTOM = (float)(metrics.heightPixels * 0.25);
        }

//        areaPOIInfoWindows = new ArrayList<>();
    }
    //endregion



    //region Draw & Touch
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.concat(matrix);

        if(mapBitmap != null) {
            canvas.drawBitmap(mapBitmap, 0, 0, null);
            poiHolderView.invalidate();
        }

        if(findTheBookAreaObject != null) {
            for(int i=0; i<BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size(); i++) {
                if (BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getFloor().getId() == BeaconBaconManager.getInstance().getCurrentFloorId()) {
                    findTheBookAreaObject.draw(canvas);
                    break;
                }
            }
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


    //Gesture and Scale Detectors
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            // TODO:
            // Instead of making postTranslate and translate back -
            // make the calculation with matrix values and adjust translate values.

            matrix.postTranslate(-distanceX, -distanceY);
            poiHolderView.mapWasTranslated(-distanceX, -distanceY);

            boolean limitDrag = false;

            // Limit Left Drag
            if (mapCurrentX() > MAX_DRAG) {
                // Revert translate.
                distanceX = mapCurrentX() - MAX_DRAG;
                matrix.postTranslate(-distanceX, 0);
                poiHolderView.mapWasTranslated(-distanceX, 0);
            }

            // Limit Top Drag
            if (mapCurrentY() > MAX_DRAG) {
                // Revert translate.
                distanceY = mapCurrentY() - MAX_DRAG;
                matrix.postTranslate(0, -distanceY);
                poiHolderView.mapWasTranslated(0, -distanceY);
            }


            // Limit Right Drag
            float bitmapWidth = mapBitmap.getWidth() * mapCurrentScaleX();
            float screenWidth = metrics.widthPixels;

            float newX = bitmapWidth + mapCurrentX();
            float maxX = screenWidth - MAX_DRAG;
            if (newX < maxX) {
                distanceX = maxX - newX;
                matrix.postTranslate(distanceX, 0);
                poiHolderView.mapWasTranslated(distanceX, 0);
            }

            // Limit Bottom Drag
            float bitmapHeight = mapBitmap.getHeight() * mapCurrentScaleY();
            float screenHeight = metrics.heightPixels;

            float newY = bitmapHeight + mapCurrentY();
            float maxY = screenHeight - MAX_DRAG_BOTTOM;
            if (newY < maxY) {
                distanceY = maxY - newY;
                matrix.postTranslate(0, distanceY);
                poiHolderView.mapWasTranslated(0, distanceY);
            }

            // TODO: Post translate AREA POI
            if(areaCustomPoiViews != null) {
                for (CustomPoiView poi : areaCustomPoiViews) {
                    poi.currentX -= distanceX;
                    poi.currentY -= distanceY;
                }
            }

            // TODO: Post translate FTB
            if(findTheBookAreaObject != null) {
                findTheBookAreaObject.currentX -= distanceX;
                findTheBookAreaObject.currentY -= distanceY;
            }

            invalidate();

//            printMatrixDEBUG();

            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            poiHolderView.poiWasClicked(event.getX(), event.getY());
            handleTapForPOIAreas(event);
            return true;
        }
    };

    private void handleTapForPOIAreas(MotionEvent event) {
        for (CustomPoiView poi : areaCustomPoiViews) {

            // TODO: FIX TOUCH INSIDE POI!!!!!!!!
//            if (poi.contains(event.getX(), event.getY())) {
                String name = poi.toString();
                if(poi.infoWindowText.getVisibility() == GONE)
                    poi.infoWindowText.setVisibility(VISIBLE);
                else
                    poi.infoWindowText.setVisibility(GONE);

                poi.infoWindowText.setText(name);

                invalidate();
                return;
//            }
        }
    }

    private void printMatrixDEBUG() {
        float x = mapCurrentX();
        float y = mapCurrentY();
        Log.d("Rect: ", x +" | " + y);
    }

    private float mapCurrentX() {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[MTRANS_X];
    }

    private float mapCurrentY() {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[MTRANS_Y];
    }

    private float mapCurrentScaleX() {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[MSCALE_X];
    }

    private float mapCurrentScaleY() {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[MSCALE_Y];
    }

    
    private float translationX;
    private float translationY;
    private ScaleGestureDetector.SimpleOnScaleGestureListener simpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
//            MAX_DRAG *= detector.getScaleFactor();
            if(scaleFactor >= MIN_ZOOM && scaleFactor <= MAX_ZOOM) {
                float scale = detector.getScaleFactor();

                //Scale the Matrix for MapHolderView
                matrix.postScale(scale, scale, 0, 0);

                //Generate translations for the scaling
                translationX = ((metrics.widthPixels * scale - metrics.widthPixels) / 2);
                translationY = ((metrics.heightPixels * scale - metrics.heightPixels) / 2);

                matrix.postTranslate(-translationX, -translationY);

//                Log.d("currentTranslationX | currentTranslationY", currentTranslationX + " | " + currentTranslationY);

                //Notify POIs map was scaled
                poiHolderView.mapWasScaled(scale, translationX, translationY);
                if(areaCustomPoiViews != null) {
                    for (CustomPoiView poi : areaCustomPoiViews) {
                        poi.currentX /= scale;
                        poi.currentX -= translationX;
                        poi.currentY /= scale;
                        poi.currentY -= translationY;
                    }
                }

                if(findTheBookAreaObject != null) {
                    findTheBookAreaObject.currentX *= scale;
                    findTheBookAreaObject.currentY *= scale;
                    findTheBookAreaObject.scaleFactor *= scale;
                }

                invalidate();
            }
            scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            return true;
        }
    };
    //endregion



    //region Setup Map
    public void setImageBitmap(Bitmap bitmap) {
        this.mapBitmap = bitmap;

        //Reset the scale factor and drag factors when we load a new place/floor
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

        //Set scale and trnslate image to center
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);

        float currentScale = mapCurrentScaleX();

        MAX_ZOOM = Math.max(MIN_ZOOM, MIN_ZOOM / scaleInit);

    }
    //endregion



    //region Setup POIs
    public void setMapPois(List<CustomPoiView> customPoiViews) {
        if(customPoiViews != null && !customPoiViews.isEmpty()) {
            List<CustomPoiView> poiOnlyViews = new ArrayList<>();
            areaCustomPoiViews.clear();

            for (CustomPoiView poi : customPoiViews) {
                if (poi.iconBitmap != null) {
                    poiOnlyViews.add(poi);
                } else {
                    areaCustomPoiViews.add(poi);
                }
            }
            poiHolderView.setupPois(poiOnlyViews, scaleInit, xTranslationInit, yTranslationInit);

            //poiHolderView.setupPoiAreas(areaCustomPoiViews, scaleInit, 0, 0);
        }
    }



    //region Find the Book
    public void setFindTheBook(Bitmap icon, BBResponseObject responseObject) {
//        areaPOIInfoWindows.clear();
//        areaInfoY = (int) dpToPx(70);

        if (responseObject != null) {
            if(Objects.equals(BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getLocation().getType(), PoiType.POI.getStringValue())) {
                poiHolderView.setupFindTheBook(icon, responseObject, scaleInit, xTranslationInit, yTranslationInit);
            } else {
//                areaPOIInfoWindows.add(new CustomAreaInfoView(context, 0, areaInfoY, BeaconBaconManager.getInstance().getRequestObject().getTitle(), BeaconBaconManager.getInstance().getConfigurationObject().getTintColor()));
//                areaInfoY += (int) dpToPx(40);
                this.setupFindTheBook(responseObject);
            }

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

    private void setupFindTheBook(BBResponseObject responseObject) {
        findTheBookAreaObject = null;
        this.findTheBookResponseObject = responseObject;

        if(responseObject != null) {
            findTheBookAreaObject = new CustomPoiView(context,
                    findTheBookResponseObject.getData().get(0).getLocation().getArea(),
                    BeaconBaconManager.getInstance().getConfigurationObject().getTintColor(),
                    BeaconBaconManager.getInstance().getRequestObject().getTitle());
        } else {
            invalidate();
        }
    }

    public void scrollToBook() {
        //Translate the map
        if(findTheBookAreaObject != null) {
            matrix.postTranslate(metrics.widthPixels / 2 - findTheBookAreaObject.centerY * scaleInit, metrics.heightPixels / 2 - findTheBookAreaObject.centerX * scaleInit);
            poiHolderView.mapWasTranslated(metrics.widthPixels / 2 - findTheBookAreaObject.centerY * scaleInit, metrics.heightPixels / 2 - findTheBookAreaObject.centerX * scaleInit);
        } else {
            matrix.postTranslate(metrics.widthPixels / 2 - poiHolderView.findTheBookObject.cx, metrics.heightPixels / 2 - poiHolderView.findTheBookObject.cy);
            poiHolderView.mapWasTranslated(metrics.widthPixels / 2 - poiHolderView.findTheBookObject.cx, metrics.heightPixels / 2 - poiHolderView.findTheBookObject.cy);
        }

        //Update view
        invalidate();
    }
    //endregion
}
