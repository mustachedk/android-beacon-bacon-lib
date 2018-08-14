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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
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

import dk.mustache.beaconbacon.activities.BeaconBaconActivity;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;
import dk.mustache.beaconbacon.enums.PoiType;

import static android.graphics.Matrix.*;
import static dk.mustache.beaconbacon.utils.Converter.dpToPx;

/**
 * Treat this as a Black Box
 */
public class MapHolderView extends AppCompatImageView {
    private Context context;

    private float MAX_DRAG_HORIZONTAL = 300.f;
    private float MAX_DRAG_VERTICAL = 500.f;
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
    private boolean hasShownFTBAlert = false;


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
            MAX_DRAG_HORIZONTAL = (float)(metrics.widthPixels * 0.40);
            MAX_DRAG_VERTICAL = (float)(metrics.heightPixels * 0.40);
        }

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
                //poi.scaleFactor = scaleFactor;
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

            if(mapBitmap == null) { return false; }


            // TODO: Instead of making postTranslate and translate back - make the calculation with matrix values and adjust translate values.

            matrix.postTranslate(-distanceX, -distanceY);
            mapWasTranslated(-distanceX, -distanceY);

            boolean limitDrag = false;

            // Limit Left Drag
            if (mapCurrentX() > MAX_DRAG_HORIZONTAL) {
                // Revert translate.
                distanceX = mapCurrentX() - MAX_DRAG_HORIZONTAL;
                matrix.postTranslate(-distanceX, 0);
                mapWasTranslated(-distanceX, 0);
            }

            // Limit Top Drag
            if (mapCurrentY() > MAX_DRAG_VERTICAL) {
                // Revert translate.
                distanceY = mapCurrentY() - MAX_DRAG_VERTICAL;
                matrix.postTranslate(0, -distanceY);
                mapWasTranslated(0, -distanceY);
            }

            // Limit Right Drag
            float bitmapWidth = mapBitmap.getWidth() * mapCurrentScaleX();
            float screenWidth = metrics.widthPixels;

            float newX = bitmapWidth + mapCurrentX();
            float maxX = screenWidth - MAX_DRAG_HORIZONTAL;
            if (newX < maxX) {
                distanceX = maxX - newX;
                matrix.postTranslate(distanceX, 0);
                mapWasTranslated(distanceX, 0);

            }

            // Limit Bottom Drag
            float bitmapHeight = mapBitmap.getHeight() * mapCurrentScaleY();
            float screenHeight = metrics.heightPixels;

            float newY = bitmapHeight + mapCurrentY();
            float maxY = screenHeight - MAX_DRAG_VERTICAL;
            if (newY < maxY) {
                distanceY = maxY - newY;
                matrix.postTranslate(0, distanceY);
                mapWasTranslated(0, distanceY);
            }

            invalidate();

//            printMatrixDEBUG();

            return true;
        }

        public void mapWasTranslated(float distanceX, float distanceY) {
            poiHolderView.mapWasTranslated(distanceX, distanceY);

            if(areaCustomPoiViews != null) {
                for (CustomPoiView poi : areaCustomPoiViews) {
                    poi.currentX += distanceX;
                    poi.currentY += distanceY;
                }
            }

            if(findTheBookAreaObject != null) {
                findTheBookAreaObject.currentX += distanceX;
                findTheBookAreaObject.currentY += distanceY;
            }
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

            float x = event.getX() - mapCurrentX();
            float y = event.getY() - mapCurrentY();

            // poi.radius/mapCurrentScaleX()
            if (poi.areaContains(x/mapCurrentScaleX(), y/mapCurrentScaleY(), poi.radius)) {
                String name = poi.toString();
                if(poi.infoWindowText.getVisibility() == GONE)
                    poi.infoWindowText.setVisibility(VISIBLE);
                else
                    poi.infoWindowText.setVisibility(GONE);

                poi.infoWindowText.setText(name);

                invalidate();
                return;
            }
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
            float validZoom = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

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
                if(areaCustomPoiViews != null) {
                    for (CustomPoiView poi : areaCustomPoiViews) {
                        poi.currentX /= scale;
                        poi.currentX -= translationX;
                        poi.currentY /= scale;
                        poi.currentY -= translationY;
                        poi.scaleFactor = MAX_ZOOM/validZoom;
                    }
                }

                if(findTheBookAreaObject != null) {
                    findTheBookAreaObject.currentX *= scale;
                    findTheBookAreaObject.currentY *= scale;
                    findTheBookAreaObject.scaleFactor *= scale;
                }

                invalidate();
            }

            scaleFactor = validZoom;

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

        //Set scale and translate image to center
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);

        float currentScale = mapCurrentScaleX();

        MAX_ZOOM = Math.max(MIN_ZOOM, MIN_ZOOM / scaleInit * 2);

    }
    //endregion



    //region Setup POIs
    public void setMapPois(List<CustomPoiView> customPoiViews) {
        if(customPoiViews != null) {
            List<CustomPoiView> poiOnlyViews = new ArrayList<>();
            areaCustomPoiViews.clear();

            for (CustomPoiView poi : customPoiViews) {
                if (poi.iconBitmap != null) {
                    poiOnlyViews.add(poi);
                } else {
                    poi.scaleFactor = MAX_ZOOM/scaleFactor;
                    areaCustomPoiViews.add(poi);
                }
            }
            poiHolderView.setupPois(poiOnlyViews, scaleInit, xTranslationInit, yTranslationInit);

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

            if (poiHolderView.findTheBookObject != null || findTheBookAreaObject != null) {
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
                    context.getResources().getColor(BeaconBaconManager.getInstance().getConfigurationObject().getTintColor()),
                    BeaconBaconManager.getInstance().getRequestObject().getTitle());
        } else {
            invalidate();
        }
    }

    public void scrollToBook() {
        // Translate the map
        float centerScreenX = this.getWidth() / 2;
        float centerScreenY = this.getHeight() / 2;

        float posX;
        float posY;
        if(findTheBookAreaObject != null) {
            posX = findTheBookAreaObject.centerX;
            posY = findTheBookAreaObject.centerY;
        } else {
            posX = poiHolderView.findTheBookResponseObject.getData().get(0).getLocation().getPosX()/2;
            posY = poiHolderView.findTheBookResponseObject.getData().get(0).getLocation().getPosY()/2;
        }

        float offsetX = -mapCurrentX() + centerScreenX - posX * mapCurrentScaleX();
        float offsetY = -mapCurrentY() + centerScreenY - posY * mapCurrentScaleY();

        //Should we display FTB Info?
        SharedPreferences sharedPref = context.getSharedPreferences("BeaconBacon_Preferences", Context.MODE_PRIVATE);
        if (!sharedPref.getBoolean("ftb_onboarding_info", false) && !hasShownFTBAlert) {
            final Handler handler = new Handler();

            final Runnable runnable = createRunnable(handler, offsetX, offsetY);
            handler.postDelayed(runnable, 0);

        } else {
            postTranslateAll(offsetX, offsetY);
            invalidate();
        }
    }
    //endregion

    private Runnable createRunnable(final Handler handler, final float offsetX, final float offsetY) {

        Runnable runnable = new Runnable(){
            public void run(){

                if (BeaconBaconActivity.boxHeight != -1) {
                    hasShownFTBAlert = true;
                    postTranslateAll(offsetX, offsetY +  BeaconBaconActivity.boxHeight/2 + dpToPx(15));
                    invalidate();
                } else {
                    handler.postDelayed(this, 10);
                }
            }
        };

        return runnable;
    }

    private void postTranslateAll(float x, float y) {
        matrix.postTranslate(x, y);
        poiHolderView.mapWasTranslated(x, y);
    }
}
