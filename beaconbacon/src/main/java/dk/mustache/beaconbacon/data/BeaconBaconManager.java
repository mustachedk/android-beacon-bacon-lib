package dk.mustache.beaconbacon.data;

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

import dk.mustache.beaconbacon.datamodels.BBAllPlaces;
import dk.mustache.beaconbacon.datamodels.BBConfigurationObject;
import dk.mustache.beaconbacon.datamodels.BBPlace;
import dk.mustache.beaconbacon.datamodels.BBRequestObject;
import dk.mustache.beaconbacon.datamodels.BBResponseObject;

public class BeaconBaconManager {
    private static BeaconBaconManager instance;
    private Context context;

    private BBAllPlaces allPlaces;
    private BBPlace currentPlace;
    private Integer currentFloorIndex;
    private Integer currentFloorId;
    private Float floorScale;

    private BBRequestObject requestObject;
    private BBResponseObject responseObject;
    private BBConfigurationObject configurationObject;



    //region BeaconBaconManager Setup
    private BeaconBaconManager(Context context) {
        this.context = context;
    }

    public static BeaconBaconManager createInstance(Context context) {
        if (instance == null) {
            instance = new BeaconBaconManager(context);
        }

        return instance;
    }

    public static BeaconBaconManager getInstance() {
        return instance;
    }
    //endregion



    //region Getters and Setters
    public BBAllPlaces getAllPlaces() {
        return allPlaces;
    }

    public void setAllPlaces(BBAllPlaces allPlaces) {
        this.allPlaces = allPlaces;
    }

    public BBPlace getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(BBPlace currentPlace) {
        this.currentPlace = currentPlace;
    }

    public BBRequestObject getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(BBRequestObject requestObject) {
        this.requestObject = requestObject;
    }

    public BBConfigurationObject getConfigurationObject() {
        return configurationObject;
    }

    public void setConfigurationObject(BBConfigurationObject configurationObject) {
        this.configurationObject = configurationObject;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public BBResponseObject getResponseObject() {
        return responseObject;
    }

    public void setResponseObject(BBResponseObject responseObject) {
        this.responseObject = responseObject;
    }

    public Integer getCurrentFloorIndex() {
        return currentFloorIndex;
    }

    public void setCurrentFloorIndex(Integer currentFloorIndex) {
        this.currentFloorIndex = currentFloorIndex;
    }

    public Integer getCurrentFloorId() {
        return currentFloorId;
    }

    public void setCurrentFloorId(Integer currentFloorId) {
        this.currentFloorId = currentFloorId;
    }

    public Float getFloorScale() {
        return floorScale;
    }

    public void setFloorScale(Float floorScale) {
        this.floorScale = floorScale;
    }
    //endregion
}
