package com.example.grant.bluetooth_elicited_brain_stimulation.legacy.CustomViews;

import android.widget.ListView;
import android.content.Context;
import android.view.ViewGroup;
import android.util.AttributeSet;
/**
 * Created by Grant on 10/29/15.
 */

/*
* creates listview that doesn't scroll
* */
public class NonScrollableListView extends ListView {

    public NonScrollableListView(Context context) {
        super(context);
    }
    public NonScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public NonScrollableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}