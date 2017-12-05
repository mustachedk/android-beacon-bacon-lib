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

import dk.mustache.beaconbacon.datamodels.AllPlaces;
import dk.mustache.beaconbacon.datamodels.BBPlace;
import dk.mustache.beaconbacon.datamodels.BBRequestObject;

public class DataManager {
    private static DataManager instance;
    private Context context;

    private AllPlaces allPlaces;
    private BBPlace currentPlace;
    private Integer currentFloor;

    private BBRequestObject requestObject;



    //region DataManager Setup
    private DataManager(Context context) {
        this.context = context;
    }

    public static DataManager createInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }

        return instance;
    }

    public static DataManager getInstance() {
        return instance;
    }
    //endregion



    //region Getters and Setters
    public AllPlaces getAllPlaces() {
        return allPlaces;
    }

    public void setAllPlaces(AllPlaces allPlaces) {
        this.allPlaces = allPlaces;
    }

    public BBPlace getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(BBPlace currentPlace) {
        this.currentPlace = currentPlace;
    }

    public Integer getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(Integer currentFloor) {
        this.currentFloor = currentFloor;
    }

    public BBRequestObject getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(BBRequestObject requestObject) {
        this.requestObject = requestObject;
    }
    //endregion
}
