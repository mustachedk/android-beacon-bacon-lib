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
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;

/**
 * Treat this as a Black Box
 */
public class PoiHolderView extends AppCompatImageView {
    private Context context;

    private DisplayMetrics metrics;

    List<CustomPoiView> poiList = new ArrayList<>();
//    List<CustomPoiView> poiAreaList = new ArrayList<>();
    public CustomPoiView findTheBookObject;
    private float currentScale = 1.0f;
    private float scaleInit;
    private float xTranslation;
    private float yTranslation;
    private Bitmap findTheBookOriginalIcon;
    public BBResponseObject findTheBookResponseObject;
    //private List<CustomAreaInfoView> areaPOIInfoWindows;


    //Constructors & Initialization
    public PoiHolderView(Context context) {
        super(context);
        init(context);
    }

    public PoiHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PoiHolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            metrics = new DisplayMetrics();
            display.getMetrics(metrics);
        }
    }



    //Draw
    @Override
    protected void onDraw(Canvas canvas) {
        if (poiList != null) {
            for (CustomPoiView poi : poiList) {
                poi.draw(canvas);
            }
        }

        if (findTheBookObject != null) {
            for(int i=0; i<BeaconBaconManager.getInstance().getCurrentPlace().getFloors().size(); i++) {
                if(BeaconBaconManager.getInstance().getResponseObject().getData().get(0).getFloor().getId() == BeaconBaconManager.getInstance().getCurrentFloorId()) {

                    findTheBookObject.draw(canvas);
                    break;
                }
            }
        }
    }



    //Setup Point POIs
    public void setupPois(List<CustomPoiView> poiList, float scaleInit, float xTranslation, float yTranslation) {
        this.poiList = poiList;

        for(CustomPoiView poi : poiList) {
            poi.cx = poi.cx * scaleInit + xTranslation;
            poi.cy = poi.cy * scaleInit + yTranslation;
        }
    }

//    //Setup Area POIs
//    public void setupPoiAreas(List<CustomPoiView> poiList, float scaleInit, float xTranslation, float yTranslation) {
//        this.poiList = poiList;
//
//        for(CustomPoiView poi : poiList) {
//            poi.centerX = poi.centerX * scaleInit;
//            poi.centerY = poi.centerX * scaleInit;
//        }
//    }



    public void setupFindTheBook(Bitmap icon, BBResponseObject responseObject, float scaleInit, float xTranslation, float yTranslation) {
        findTheBookObject = null;
        this.findTheBookOriginalIcon = icon;
        this.findTheBookResponseObject = responseObject;
        this.scaleInit = scaleInit;
        this.xTranslation = xTranslation;
        this.yTranslation = yTranslation;

        if(responseObject != null) {
            Bitmap bitmap = icon;

            //Default to ic_book
            if(bitmap == null)
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_book);

            findTheBookObject = new CustomPoiView(context, bitmap,
                    responseObject.getData().get(0).getLocation().getPosX()/2,
                    responseObject.getData().get(0).getLocation().getPosY()/2,
                    responseObject.getRadius(),
                    BeaconBaconManager.getInstance().getRequestObject().getTitle(),
                    true);

            findTheBookObject.cx = findTheBookObject.cx * scaleInit + xTranslation;
            findTheBookObject.cy = findTheBookObject.cy * scaleInit + yTranslation;
        } else {
            invalidate();
        }
    }



    //Handle POI clicks
    public void poiWasClicked(float x, float y) {
        String name = "NO POI";

        if(findTheBookObject != null) {
            if (findTheBookObject.contains(x, y)) {
                name = findTheBookObject.toString();
                if(findTheBookObject.infoWindowText.getVisibility() == GONE)
                    findTheBookObject.infoWindowText.setVisibility(VISIBLE);
                else
                    findTheBookObject.infoWindowText.setVisibility(GONE);

                findTheBookObject.infoWindowText.setText(name);

                invalidate();
                return;
            }
        }

        if(poiList != null) {
            for (CustomPoiView poi : poiList) {
                if (poi.contains(x, y)) {
                    name = poi.toString();
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
    }

    public void floorWasSwitched() {
        if(findTheBookObject != null) {

            findTheBookObject = new CustomPoiView(context, findTheBookOriginalIcon,
                    findTheBookResponseObject.getData().get(0).getLocation().getPosX()/2,
                    findTheBookResponseObject.getData().get(0).getLocation().getPosY()/2,
                    findTheBookResponseObject.getRadius(),
                    findTheBookObject.title,
                    true);

            findTheBookObject.cx = findTheBookObject.cx * scaleInit + xTranslation;
            findTheBookObject.cy = findTheBookObject.cy * scaleInit + yTranslation;
        }
    }



    //Handle Map Movements
    public void mapWasTranslated(float distanceX, float distanceY) {
        for(CustomPoiView poi : poiList) {
            poi.cx += distanceX;
            poi.cy += distanceY;
        }

        if(findTheBookObject != null) {
            findTheBookObject.cx += distanceX;
            findTheBookObject.cy += distanceY;
        }
    }

    public void mapWasScaled(float scale, float translationX, float translationY) {
        for(CustomPoiView poi : poiList) {
            poi.cx *= scale;
            poi.cx -= translationX;
            poi.cy *= scale;
            poi.cy -= translationY;
        }

        if(findTheBookObject != null) {
            findTheBookObject.cx *= scale;
            findTheBookObject.cx -= translationX;
            findTheBookObject.cy *= scale;
            findTheBookObject.cy -= translationY;
        }
    }

}
