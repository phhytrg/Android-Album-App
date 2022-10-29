package com.example.album.gallery;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public LinearSpacingItemDecoration(int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        if (includeEdge) {
            outRect.top = spacing;
            outRect.left = spacing;
            outRect.right = spacing;
            outRect.bottom = spacing;
        } else {
            outRect.bottom = spacing;
        }
    }
}
