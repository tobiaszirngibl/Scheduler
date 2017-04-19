package com.ase_1617.organizedlib.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * A ListView which expands every listItem at any time
 */

public class ExpandedListView extends ListView {
    private int oldCount = 0;

    public ExpandedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getCount() != oldCount) {
            oldCount = getCount();
            android.view.ViewGroup.LayoutParams params = getLayoutParams();
            if(getChildCount() != 0){
                params.height = getCount() * (oldCount > 0 ? getChildAt(0).getHeight() : 0);
            }
            setLayoutParams(params);
        }

        super.onDraw(canvas);
    }
}
