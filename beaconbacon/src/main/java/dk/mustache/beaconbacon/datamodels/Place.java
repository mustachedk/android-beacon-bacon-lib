package dk.mustache.beaconbacon.datamodels;

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

import java.util.Date;
import java.util.List;

public class Place {
    private int id;
    private int team_id;
    private String name;
    private int created_by;
    private String created_at;
    private String updated_at;
    private String address;
    private String zipcode;
    private String identifier_one;
    private String identifier_two;
    private String identifier_three;
    private String identifier_four;
    private String identifier_five;
    private int place_id;
    private int beacon_positioning_enabled;
    private int beacon_proximity_enabled;
    private int order;
    private int activated;
    private List<Floor> floors;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeam_id() {
        return team_id;
    }

    public void setTeam_id(int team_id) {
        this.team_id = team_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getIdentifier_one() {
        return identifier_one;
    }

    public void setIdentifier_one(String identifier_one) {
        this.identifier_one = identifier_one;
    }

    public String getIdentifier_two() {
        return identifier_two;
    }

    public void setIdentifier_two(String identifier_two) {
        this.identifier_two = identifier_two;
    }

    public String getIdentifier_three() {
        return identifier_three;
    }

    public void setIdentifier_three(String identifier_three) {
        this.identifier_three = identifier_three;
    }

    public String getIdentifier_four() {
        return identifier_four;
    }

    public void setIdentifier_four(String identifier_four) {
        this.identifier_four = identifier_four;
    }

    public String getIdentifier_five() {
        return identifier_five;
    }

    public void setIdentifier_five(String identifier_five) {
        this.identifier_five = identifier_five;
    }

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public int getBeacon_positioning_enabled() {
        return beacon_positioning_enabled;
    }

    public void setBeacon_positioning_enabled(int beacon_positioning_enabled) {
        this.beacon_positioning_enabled = beacon_positioning_enabled;
    }

    public int getBeacon_proximity_enabled() {
        return beacon_proximity_enabled;
    }

    public void setBeacon_proximity_enabled(int beacon_proximity_enabled) {
        this.beacon_proximity_enabled = beacon_proximity_enabled;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void setFloors(List<Floor> floors) {
        this.floors = floors;
    }
}
