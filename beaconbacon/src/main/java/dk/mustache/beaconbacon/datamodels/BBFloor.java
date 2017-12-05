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

import java.util.List;

public class BBFloor {
    private int id;
    private int team_id;
    private int place_id;
    private String name;
    private int order;
    private String image;
    private int created_by;
    private String created_at;
    private String updated_at;
    private String map_width_in_centimeters;
    private String map_height_in_centimeters;
    private String map_width_in_pixels;
    private String map_height_in_pixels;
    private String map_pixel_to_centimeter_ratio;
    private String map_walkable_color;
    private String map_background_color;
    private List<BBLocation> locations;

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

    public int getPlace_id() {
        return place_id;
    }

    public void setPlace_id(int place_id) {
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public String getMap_width_in_centimeters() {
        return map_width_in_centimeters;
    }

    public void setMap_width_in_centimeters(String map_width_in_centimeters) {
        this.map_width_in_centimeters = map_width_in_centimeters;
    }

    public String getMap_height_in_centimeters() {
        return map_height_in_centimeters;
    }

    public void setMap_height_in_centimeters(String map_height_in_centimeters) {
        this.map_height_in_centimeters = map_height_in_centimeters;
    }

    public String getMap_width_in_pixels() {
        return map_width_in_pixels;
    }

    public void setMap_width_in_pixels(String map_width_in_pixels) {
        this.map_width_in_pixels = map_width_in_pixels;
    }

    public String getMap_height_in_pixels() {
        return map_height_in_pixels;
    }

    public void setMap_height_in_pixels(String map_height_in_pixels) {
        this.map_height_in_pixels = map_height_in_pixels;
    }

    public String getMap_pixel_to_centimeter_ratio() {
        return map_pixel_to_centimeter_ratio;
    }

    public void setMap_pixel_to_centimeter_ratio(String map_pixel_to_centimeter_ratio) {
        this.map_pixel_to_centimeter_ratio = map_pixel_to_centimeter_ratio;
    }

    public String getMap_walkable_color() {
        return map_walkable_color;
    }

    public void setMap_walkable_color(String map_walkable_color) {
        this.map_walkable_color = map_walkable_color;
    }

    public String getMap_background_color() {
        return map_background_color;
    }

    public void setMap_background_color(String map_background_color) {
        this.map_background_color = map_background_color;
    }

    public List<BBLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<BBLocation> locations) {
        this.locations = locations;
    }
}
