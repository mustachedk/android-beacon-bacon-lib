package dk.mustache.beaconbacon;

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

import android.app.Application;
import android.content.Context;

public class BBApplication extends Application {
    public static final String TAG = "BeaconBacon";

    public static final String PLACE_SELECTION_FRAGMENT = "place_selection_fragment";
    public static final String POI_SELECTION_FRAGMENT = "poi_selection_fragment";
    public static final String PLACE_ID = "place_id";
    public static final String FAUST_ID = "faust_id";

    private static Context context;
    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
