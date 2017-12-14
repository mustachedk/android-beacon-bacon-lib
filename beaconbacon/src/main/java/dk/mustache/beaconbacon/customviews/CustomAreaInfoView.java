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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.data.BeaconBaconManager;

public class CustomAreaInfoView {
    private Bitmap infoBoxArea;
    private Context context;
    public float cx = -1;
    public float cy = -1;

    public CustomAreaInfoView(Context context, float x, float y, String title, int color) {
        this.context = context;

        cx = x;
        cy = y;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_area_info, null);

        AreaView areaView = view.findViewById(R.id.area_info_cirlce);
        areaView.setCircleColor(context.getResources().getColor(color));

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

    public void draw(Canvas canvas) {
        canvas.drawBitmap(infoBoxArea, cx, cy, null);
    }
}
