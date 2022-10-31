package com.example.album.item_decoration;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PhotoSpaceDecoration extends RecyclerView.ItemDecoration {
    private final int spacingWidthPx;

    /**
     * Initialise with the with of the spacer in dp
     *
     * @param spacingWidthDp this will be divided between elements and applied as a space on each side
     *                       NB: for proper alignment this must be divisible by 2 and by the number of columns
     */
    public PhotoSpaceDecoration(Context context, int spacingWidthDp) {
        // Convert DP to pixels
        this.spacingWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spacingWidthDp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * @param index           a 0 indexed value of the current item
     * @param numberOfColumns
     * @return a 0 indexed Point with the x & y location of the item in the grid
     */
    private Point getItemXY(int index, int numberOfColumns) {
        int x = index % numberOfColumns;
        int y = index / numberOfColumns; // NB: integer division
        return new Point(x, y);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int position = parent.getChildAdapterPosition(view);
        final int columns = getTotalSpanCount(parent);
        final int rows = (int) Math.ceil(parent.getChildCount() / (double) columns); // NB: NOT integer division
        int spanSize = getItemSpanSize(parent, position);
        if (columns == spanSize) {
            return;
        }

        Point point = getItemXY(position, columns);
        int firstMargin = spacingWidthPx * (columns - 1) / columns;
        int secondMargin = spacingWidthPx - firstMargin;
        int middleMargin = spacingWidthPx / 2;

        if (point.x == 0) { // first column
            outRect.left = 0;
            outRect.right = firstMargin;
        } else if (point.x == 1) { // second column
            outRect.left = secondMargin;
            outRect.right = rows > 3 ? middleMargin : secondMargin;
        } else if (point.x - columns == -2) { // penultimate column
            outRect.left = rows > 3 ? middleMargin : secondMargin;
            outRect.right = secondMargin;
        } else if (point.x - columns == -1) { // last column
            outRect.left = firstMargin;
            outRect.right = 0;
        } else { // middle columns
            outRect.left = middleMargin;
            outRect.right = middleMargin;
        }

        if (point.y == 0) { // first row
            outRect.top = 0;
            outRect.bottom = firstMargin;
        } else if (point.y == 1) { // second row
            outRect.top = secondMargin;
            outRect.bottom = rows > 3 ? middleMargin : secondMargin;
        } else if (point.y - rows == -2) { // penultimate row
            outRect.top = rows > 3 ? middleMargin : secondMargin;
            outRect.bottom = secondMargin;
        } else if (point.y - rows == -1) { // last row
            outRect.top = firstMargin;
            outRect.bottom = 0;
        } else { // middle rows
            outRect.top = middleMargin;
            outRect.bottom = middleMargin;
        }
    }

    private int getTotalSpanCount(RecyclerView parent) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        return layoutManager instanceof GridLayoutManager ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
    }

    private int getItemSpanSize(RecyclerView parent, int position) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        return layoutManager instanceof GridLayoutManager ? ((GridLayoutManager) layoutManager).getSpanSizeLookup()
                .getSpanSize(
                        position) : 1;
    }
}
