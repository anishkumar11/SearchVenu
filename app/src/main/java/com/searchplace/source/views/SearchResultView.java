package com.searchplace.source.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.searchplace.source.R;
import com.searchplace.source.local_objects.Place;

public final class SearchResultView extends RelativeLayout
{

    private TextView _titleLabel, _addressLabel, _distanceLabel;


    public SearchResultView(Context context)
    {
        super(context);
        init();
    }

    public SearchResultView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public SearchResultView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init()
    {
        try
        {
            inflate(getContext(), R.layout.search_result_view, this);

            _titleLabel  = (TextView) findViewById(R.id.titleLabel);
            _addressLabel  = (TextView) findViewById(R.id.addressLabel);
            _distanceLabel  = (TextView) findViewById(R.id.distanceLabel);
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::SearchResultView::init");
        }
    }

    public void setInfo(Place pi)
    {
        try {

            _titleLabel.setText(pi.get_name());
            _addressLabel.setText(pi.get_address());
            _distanceLabel.setText("Distance: " + pi.get_distance() + " meters");
        }
        catch(Exception e)
        {
            System.out.println("Exception: " + e + " ::SearchResultView::setInfo");
        }

    }

}


