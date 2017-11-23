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

public class BBLocation {
    private int id;
    private int team_id;
    private int place_id;
    private int poi_id;
    private String name;
    private int posX;
    private int posY;
    private int created_by;
    private String created_at;
    private String updated_at;
    private String type;
    private String parameter_one;
    private String parameter_two;
    private String parameter_three;
    private String parameter_four;
    private String parameter_five;
    private String draw_type;
    private String area;
    private int block_id;
    private int findable_id;
    private int rotation;
    private BBPoi poi;

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

    public int getPoi_id() {
        return poi_id;
    }

    public void setPoi_id(int poi_id) {
        this.poi_id = poi_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParameter_one() {
        return parameter_one;
    }

    public void setParameter_one(String parameter_one) {
        this.parameter_one = parameter_one;
    }

    public String getParameter_two() {
        return parameter_two;
    }

    public void setParameter_two(String parameter_two) {
        this.parameter_two = parameter_two;
    }

    public String getParameter_three() {
        return parameter_three;
    }

    public void setParameter_three(String parameter_three) {
        this.parameter_three = parameter_three;
    }

    public String getParameter_four() {
        return parameter_four;
    }

    public void setParameter_four(String parameter_four) {
        this.parameter_four = parameter_four;
    }

    public String getParameter_five() {
        return parameter_five;
    }

    public void setParameter_five(String parameter_five) {
        this.parameter_five = parameter_five;
    }

    public String getDraw_type() {
        return draw_type;
    }

    public void setDraw_type(String draw_type) {
        this.draw_type = draw_type;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getBlock_id() {
        return block_id;
    }

    public void setBlock_id(int block_id) {
        this.block_id = block_id;
    }

    public int getFindable_id() {
        return findable_id;
    }

    public void setFindable_id(int findable_id) {
        this.findable_id = findable_id;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public BBPoi getPoi() {
        return poi;
    }

    public void setPoi(BBPoi poi) {
        this.poi = poi;
    }
}
