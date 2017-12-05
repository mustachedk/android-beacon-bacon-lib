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

import android.graphics.Bitmap;
import android.graphics.Canvas;

import static dk.mustache.beaconbacon.utils.Converter.pxToDp;

public class CustomPoiView {
    private Bitmap bitmap;
    //TODO create an infoBitmap (possibly as a layout resource to change the title)
    private Bitmap infoBitmap;
    private String title;

    public float radius;

    public float cx;
    public float cy;

    public CustomPoiView(Bitmap bitmap, float x, float y, float radius, String title) {
        cx = pxToDp(x);
        cy = pxToDp(y);

        this.radius = radius;
        this.title = title;
        this.bitmap = bitmap;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(Bitmap.createScaledBitmap(bitmap, (int) (radius*2), (int) (radius*2), false), cx-radius, cy-radius, null);
    }

    public boolean contains(float x, float y) {
        return Math.hypot(cx - x, cy - y) < radius;
    }

    @Override
    public String toString() {
        return title;
    }
}
