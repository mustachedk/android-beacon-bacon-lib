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

import android.graphics.Bitmap;

public class BBRequestObject {
    private String find_identifier;
    private String faust_id;
    private String title;
    private String subtitle;
    private Bitmap image;

    public BBRequestObject(String find_identifier, String faust_id, String title, String subtitle, Bitmap image) {
        this.find_identifier = find_identifier;
        this.faust_id = faust_id;
        this.title = title;
        this.subtitle = subtitle;
        this.image = image;
    }

    public String getFind_identifier() {
        return find_identifier;
    }

    public void setFind_identifier(String find_identifier) {
        this.find_identifier = find_identifier;
    }

    public String getFaust_id() {
        return faust_id;
    }

    public void setFaust_id(String faust_id) {
        this.faust_id = faust_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
