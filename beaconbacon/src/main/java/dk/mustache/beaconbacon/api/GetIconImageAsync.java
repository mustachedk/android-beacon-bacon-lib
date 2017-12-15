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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dk.mustache.beaconbacon.customviews.CustomPoiView;
import dk.mustache.beaconbacon.data.BeaconBaconManager;
import dk.mustache.beaconbacon.datamodels.BBLocation;
import dk.mustache.beaconbacon.datamodels.BBPoi;
import dk.mustache.beaconbacon.interfaces.IconImageAsyncResponse;

import static dk.mustache.beaconbacon.utils.Converter.dpToPx;

public class GetIconImageAsync extends AsyncTask<List<BBPoi>, Void, List<CustomPoiView>> {
    public IconImageAsyncResponse delegate = null;

    @Override
    protected List<CustomPoiView> doInBackground(List<BBPoi>[] selectedPois) {
        if(BeaconBaconManager.getInstance().getCurrentPlace() != null && BeaconBaconManager.getInstance().getCurrentPlace().getFloors() != null && BeaconBaconManager.getInstance().getCurrentFloorIndex() != null) {
            List<BBLocation> locations = BeaconBaconManager.getInstance().getCurrentPlace().getFloors().get(BeaconBaconManager.getInstance().getCurrentFloorIndex()).getLocations();
            List<CustomPoiView> customPoiViewList = new ArrayList<>();

            if (locations != null) {
                for (int i = 0; i < locations.size(); i++) {
                    if (locations.get(i).getPoi() != null && selectedPois[0] != null) {
                        for (int j = 0; j < selectedPois[0].size(); j++) {
                            if (locations.get(i).getPoi().getId() == selectedPois[0].get(j).getId()) {
                                if (Objects.equals(locations.get(i).getPoi().getType(), "icon")) {
                                    try {
                                        Bitmap bitmap = Bitmap.createScaledBitmap(ApiManager.getInstance().getPicasso().load(locations.get(i).getPoi().getIcon()).get(), (int) dpToPx(30), (int) dpToPx(30), false);
                                        customPoiViewList.add(new CustomPoiView(BeaconBaconManager.getInstance().getContext(), bitmap, dpToPx(locations.get(i).getPosX()), dpToPx(locations.get(i).getPosY()), (int) dpToPx(15), locations.get(i).getPoi().getName(), false));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else if (Objects.equals(locations.get(i).getPoi().getType(), "area")) {
                                    if (!Objects.equals(locations.get(i).getArea(), ""))
                                        customPoiViewList.add(new CustomPoiView(BeaconBaconManager.getInstance().getContext(), locations.get(i).getArea(), Color.parseColor(locations.get(i).getPoi().getColor()), locations.get(i).getPoi().getName()));
                                }
                            }
                        }
                    }
                }
            }

            return customPoiViewList;
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<CustomPoiView> result) {
        super.onPostExecute(result);

        delegate.iconImageAsyncFinished(result);
    }
}
