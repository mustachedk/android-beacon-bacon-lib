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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.data.BeaconBaconManager;

import static dk.mustache.beaconbacon.utils.Converter.dpToPx;

public class CustomPoiView {
    private Context context;

    public Bitmap iconBitmap;

    private Bitmap infoBoxText;
    private Bitmap infoBoxArrow;
    public TextView infoWindowText;

    public String title;

    public float radius;
    public float infoScale = 1.0f;

    public float cx;
    public float cy;

    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<String> area;
    private List<Integer> areaInts = new ArrayList<>();;


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

    public CustomPoiView(Context context, String area, String color, String name) {
        this.context = context;

        if(area != null)
            this.area = Arrays.asList(area.split(","));

        this.title = name;

        for(String element : this.area) {
            areaInts.add(Integer.valueOf(element));
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(false);
        paint.setColor(Color.parseColor(color));
        paint.setAlpha(255/10*3);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);

        createInfoWindow();
    }

    private void createInfoWindow() {
        LayoutInflater inflater = LayoutInflater.from(context);

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
            path.reset();
            path.moveTo(areaInts.get(0) + cx, areaInts.get(1) + cy);

            for(int i=2; i<areaInts.size(); i++) {
                path.lineTo(areaInts.get(i) + cx, areaInts.get(i+1) + cy);

                //Jump double for x/y values
                i++;
            }

            path.close();

            canvas.drawPath(path, paint);
        }

        //Draw the info window if visible
        if(infoWindowText != null && infoWindowText.getVisibility() == View.VISIBLE) {
            int x = (int) ((cx) - infoWindowText.getWidth()/2);
            int y = (int) ((cy) - infoWindowText.getHeight() - radius);
            int x2 = (int) ((cx) - infoBoxArrow.getWidth()/2);
            int y2 = (int) ((cy) - infoBoxArrow.getHeight()/2 - radius);

            canvas.drawBitmap(Bitmap.createScaledBitmap(infoBoxText, infoBoxText.getWidth(), infoBoxText.getHeight(), false), x, y-infoBoxArrow.getHeight()/4, null);
            canvas.drawBitmap(Bitmap.createScaledBitmap(infoBoxArrow, infoBoxArrow.getWidth(), infoBoxArrow.getHeight(), false), x2, y2, null);
        }
    }

    public boolean contains(float x, float y) {
        if(iconBitmap != null)
            return Math.hypot(cx - x, cy - y) < radius;
        else
            return path.op(path, Path.Op.DIFFERENCE); //TODO Fix Area Touch Inside registration somehow
    }

    @Override
    public String toString() {
        return title;
    }
}
