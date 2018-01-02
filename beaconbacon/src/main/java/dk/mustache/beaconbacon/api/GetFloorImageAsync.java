package dk.mustache.beaconbacon.api;

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
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;

import dk.mustache.beaconbacon.BBApplication;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBFloor;
import dk.mustache.beaconbacon.interfaces.FloorImageAsyncResponse;

public class GetFloorImageAsync extends AsyncTask<Void, Void, Bitmap> {
    public FloorImageAsyncResponse delegate = null;

    @Override
    protected Bitmap doInBackground(Void... voids) {
        if(BeaconBaconManager.getInstance().getCurrentPlace() != null &&
                BeaconBaconManager.getInstance().getCurrentPlace().getFloors() != null &&
                BeaconBaconManager.getInstance().getCurrentFloorIndex() != null) {

            WindowManager windowManager = (WindowManager) BBApplication.getContext().getSystemService(Context.WINDOW_SERVICE);

            if (windowManager != null) {

                try {
                    BBFloor currentFloor = BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(BeaconBaconManager.getInstance().getCurrentFloorIndex());
                    return ApiManager.getInstance().getPicasso().load(currentFloor.getImage()).get();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        delegate.floorImageAsyncFinished(result);
    }
}
