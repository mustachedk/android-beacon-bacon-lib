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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.snatik.polygon.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.data.BeaconBaconManager;

import static dk.mustache.beaconbacon.utils.Converter.dpToPx;

public class CustomPoiView {
    private float scaleInit;
    private Context context;

    public Bitmap iconBitmap;

    private Bitmap infoBoxText;
    private Bitmap infoBoxArrow;
    private Bitmap infoBoxArea;
    public TextView infoWindowText;

    public String title;

    public float radius;

    public float cx = -1;
    public float cy = -1;

    public float centerX;
    public float centerY;

    private Path path = new Path();
    private Polygon polygon;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<String> area;
    private List<Float> areaFloats = new ArrayList<>();

    private Region region;
    private RectF rectF;
//    private Paint paintTest;
//    private Path pathTest = new Path();

    public float currentX;
    public float currentY;
    public float scaleFactor = 1.0f;


    //Constructors
    public CustomPoiView(Context context, Bitmap iconBitmap, float x, float y, float radius, String title, boolean isFindTheBook) {
        cx = x;
        cy = y;

        this.context = context;
        this.radius = radius;
        this.title = title;

        if(isFindTheBook)
            this.iconBitmap = createFindTheBookBitmap(iconBitmap);
        else
            this.iconBitmap = iconBitmap;

        createInfoWindow();
    }

    public CustomPoiView(Context context, float scaleInit, String area, int color, String name) {
        this.context = context;
        this.scaleInit = scaleInit;

        if(area != null)
            this.area = Arrays.asList(area.split(","));

        this.title = name;

        for(String element : this.area) {
            areaFloats.add(Float.valueOf(element));
        }

        float lowX = 0.0f;
        float highX = 0.0f;
        float lowY = 0.0f;
        float highY = 0.0f;

        for(int i=0; i<areaFloats.size(); i++) {
            if(lowX != 0.0f && lowY != 0.0f) {
                lowX = areaFloats.get(i) < lowX ? areaFloats.get(i) : lowX;
                lowY = areaFloats.get(i + 1) < lowY ? areaFloats.get(i + 1) : lowY;
            } else {
                lowX = areaFloats.get(i);
                lowY = areaFloats.get(i + 1);
            }
            highX = areaFloats.get(i) > highX ? areaFloats.get(i) : highX;
            highY = areaFloats.get(i+1) > highY ? areaFloats.get(i+1) :highY;

            centerX = lowX + (highX-lowX) /2;
            centerY = lowY + (highY-lowY) /2;

            //Jump double for x/y values
            i++;
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(false);
        paint.setColor(context.getResources().getColor(color));
        paint.setAlpha(75);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        /* TEST
        paintTest = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTest.setStyle(Paint.Style.FILL);
        paintTest.setAntiAlias(false);
        paintTest.setColor(Color.RED);
        paintTest.setAlpha(75);

        pathTest = new Path();
        pathTest.setFillType(Path.FillType.EVEN_ODD);
        */

        createInfoWindow();
    }

    private void createInfoWindow() {
        LayoutInflater inflater = LayoutInflater.from(context);

        if(iconBitmap != null) { //POI
            View view = inflater.inflate(R.layout.layout_info_window, null);
            View viewArrow = inflater.inflate(R.layout.layout_info_arrow, null);

            infoWindowText = view.findViewById(R.id.info_window_text);
            infoWindowText.setText(title);
            if(BeaconBaconManager.getInstance().getConfigurationObject() != null)
                infoWindowText.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

            ImageView infoArrow = viewArrow.findViewById(R.id.info_window_arrow);
            infoArrow.setVisibility(View.VISIBLE);

            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();

            viewArrow.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            viewArrow.layout(0, 0, viewArrow.getMeasuredWidth(), viewArrow.getMeasuredHeight());
            viewArrow.setDrawingCacheEnabled(true);
            viewArrow.buildDrawingCache();

            infoBoxText = Bitmap.createBitmap(view.getDrawingCache());
            infoBoxArrow = Bitmap.createBitmap(viewArrow.getDrawingCache());

            view.setDrawingCacheEnabled(false);
            viewArrow.setDrawingCacheEnabled(false);

            infoWindowText.setVisibility(View.GONE);

        } else { //Area
            View view = inflater.inflate(R.layout.layout_area_info, null);

            AreaView areaView = view.findViewById(R.id.area_info_cirlce);
            areaView.setCircleColor(Color.RED);

            TextView areaText = view.findViewById(R.id.area_info_text);
            areaText.setText(title);

            if(BeaconBaconManager.getInstance().getConfigurationObject() != null)
                areaText.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());

            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();

            infoBoxArea = Bitmap.createBitmap(view.getDrawingCache());

            view.setDrawingCacheEnabled(false);
        }
    }

    private Bitmap createFindTheBookBitmap(Bitmap bitmap) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.layout_find_the_book_bitmap, null);

        ImageView icon = view.findViewById(R.id.find_the_book_icon);
        icon.setImageBitmap(bitmap);

        this.radius = dpToPx(15);

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap b = null;
        if(view.getDrawingCache() != null)
             b = Bitmap.createScaledBitmap(view.getDrawingCache(), (int) dpToPx(30), (int) dpToPx(30), false);

        view.setDrawingCacheEnabled(false);

        return b;
    }

    public void draw(Canvas canvas) {

        if(iconBitmap != null) { //Draw icon
            canvas.drawBitmap(iconBitmap,  cx - radius, cy - radius, null);

        } else { //Draw area

            //User visible
            path.reset();
            path.moveTo(areaFloats.get(0) + cx, areaFloats.get(1) + cy);

            for(int i=2; i<areaFloats.size(); i++) {
                path.lineTo(areaFloats.get(i) + cx, areaFloats.get(i+1) + cy);

                //Jump double for x/y values
                i++;
            }

            path.close();

            canvas.drawPath(path, paint);
            //Test point
            //canvas.drawPath(pathTest, paintTest);
        }

        //Draw the info window if visible
        if(infoWindowText != null && infoWindowText.getVisibility() == View.VISIBLE) {
//                int x = (int) ((areaFloats.get(0)) - infoWindowText.getWidth() / 2);
//                int y = (int) ((areaFloats.get(1)) - infoWindowText.getHeight() - radius);
//                int x2 = (int) ((areaFloats.get(0)) - infoBoxArrow.getWidth() / 2);
//                int y2 = (int) ((areaFloats.get(1)) - infoBoxArrow.getHeight() / 2 - radius);
//
//                canvas.drawBitmap(Bitmap.createScaledBitmap(infoBoxText, (int) (infoBoxText.getWidth() / scaleFactor), (int) (infoBoxText.getHeight() / scaleFactor), false), x, y-infoBoxArrow.getHeight()/3+2, null);
//                canvas.drawBitmap(Bitmap.createScaledBitmap(infoBoxArrow, (int) (infoBoxArrow.getWidth() / scaleFactor), (int) (infoBoxArrow.getHeight() * scaleFactor), false), x2, y2, null);
            if(cx != -1 && cy != -1) {
                int x = (int) ((cx) - infoWindowText.getWidth() / 2);
                int y = (int) ((cy) - infoWindowText.getHeight() - radius);
                int x2 = (int) ((cx) - infoBoxArrow.getWidth() / 2);
                int y2 = (int) ((cy) - infoBoxArrow.getHeight() / 2 - radius);

                canvas.drawBitmap(Bitmap.createScaledBitmap(infoBoxText, infoBoxText.getWidth(), infoBoxText.getHeight(), false), x, y-infoBoxArrow.getHeight()/3+2, null);
                canvas.drawBitmap(Bitmap.createScaledBitmap(infoBoxArrow, infoBoxArrow.getWidth(), infoBoxArrow.getHeight(), false), x2, y2, null);
            }
        }
    }

    public boolean contains(float x, float y) {
            return Math.hypot(cx - x, cy - y) < radius;
    }

    public boolean contains(float x, float y, float scale) {
        //TODO Good when clicking in center of screen, but not so good when deviating from center, it deviates further than expected, works at all scales though
        int pointX = (int) ((x - currentX/scaleFactor) * (1/scale));
        int pointY = (int) ((y - currentY/scaleFactor) * (1/scale));

        /*
        int pointX = (int) (((x/scaleFactor - currentX/scaleFactor)) * (1/scale));
        int pointY = (int) (((y/scaleFactor - currentY/scaleFactor)) * (1/scale));
        */

        //int pointX = (int) (((x - currentX) * ((1/scale))) * scaleFactor);
        //int pointY = (int) (((y - currentY) * ((1/scale))) * scaleFactor);

        /*
        //User visible test point
        pathTest.reset();
        pathTest.moveTo(pointX-50, pointY-50);
        pathTest.lineTo(pointX+50, pointY-50);
        pathTest.lineTo(pointX+50, pointY+50);
        pathTest.lineTo(pointX-50, pointY+50);
        pathTest.close();
        */

        rectF = new RectF();
        path.computeBounds(rectF, true);
        region = new Region();
        region.setPath(path, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));

        return region.contains(pointX, pointY);
    }

    @Override
    public String toString() {
        return title;
    }
}
