package dk.mustache.beaconbacon.fragments;

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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dk.mustache.beaconbacon.R;
import dk.mustache.beaconbacon.activities.BeaconBaconActivity;
import dk.mustache.beaconbacon.data.BeaconBaconManager;

public class FindTheBookFragment extends Fragment implements View.OnClickListener {
    private RelativeLayout rootLayout;
    private LinearLayout boxLayout;
    private Button okBtn;
    private Button okDontShowAgainBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_find_the_book, container, false);

        rootLayout = view.findViewById(R.id.ftb_root_view);
        rootLayout.setOnClickListener(this);
        boxLayout = view.findViewById(R.id.ftb_box_layout);
        boxLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boxLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                BeaconBaconActivity.boxHeight = boxLayout.getHeight();
            }
        });
        okBtn = view.findViewById(R.id.ftb_ok_btn);
        okBtn.setOnClickListener(this);
        okDontShowAgainBtn = view.findViewById(R.id.ftb_ok_dont_show_again_btn);
        okDontShowAgainBtn.setOnClickListener(this);

        TextView header = view.findViewById(R.id.ftb_header);
        TextView text = view.findViewById(R.id.ftb_text);

        if(BeaconBaconManager.getInstance().getConfigurationObject() != null && BeaconBaconManager.getInstance().getConfigurationObject().getTypeface() != null) {
            header.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());
            text.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());
            okBtn.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());
            okDontShowAgainBtn.setTypeface(BeaconBaconManager.getInstance().getConfigurationObject().getTypeface());
        }

        return view;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.ftb_root_view) {
            if (getActivity() != null)
                getActivity().getSupportFragmentManager().popBackStack();

        } else if (i == R.id.ftb_ok_btn) {
            if (getActivity() != null)
                getActivity().getSupportFragmentManager().popBackStack();

        } else if (i == R.id.ftb_ok_dont_show_again_btn) {
            if (getActivity() != null) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences("BeaconBacon_Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("ftb_onboarding_info", true);
                editor.apply();

                getActivity().getSupportFragmentManager().popBackStack();
            }

        }
    }
}
