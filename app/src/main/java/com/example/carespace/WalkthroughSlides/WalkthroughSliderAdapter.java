package com.example.carespace.WalkthroughSlides;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.carespace.R;

public class WalkthroughSliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public WalkthroughSliderAdapter(Context context)
    {
        this.context=context;
    }

    //arrays
    public int[] slideImages = {
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground,
    };

    public String[] headings = {
            "exercise tutorial",
            "hospital nearby",
            "chat with doc"
    };

    public String[] descriptions = {
            "exercise follow gym trainer",
            "check hospital nearby",
            "chat with doctor about your concern"
    };

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.walkthrough_slide_layout, container,false);

        ImageView slide_img = (ImageView) view.findViewById(R.id.slide_img);
        TextView slide_header = (TextView) view.findViewById(R.id.slide_header);
        TextView slide_desc = (TextView) view.findViewById(R.id.slide_desc);

        slide_img.setImageResource(slideImages[position]);
        slide_header.setText(headings[position]);
        slide_desc.setText(descriptions[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
