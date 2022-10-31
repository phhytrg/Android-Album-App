package com.example.album.item_decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacing;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int columnIndex = position % spanCount; // item column
        if (includeEdge) {
            outRect.left = spacing - columnIndex * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (columnIndex + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
            outRect.top = (position < spanCount) ? spacing : 0;
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = columnIndex * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (columnIndex + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if(position >= spanCount){
                outRect.top = spacing;
            }
        }
    }
}

