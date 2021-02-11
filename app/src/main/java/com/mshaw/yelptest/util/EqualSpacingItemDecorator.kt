package com.mshaw.yelptest.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import java.lang.IllegalStateException

class EqualSpacingItemDecorator(
    private val spacing: Int,
    private var displayMode: DisplayMode = DisplayMode.VERTICAL
) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildViewHolder(view).adapterPosition
        val itemCount = state.itemCount
        val layoutManager = parent.layoutManager ?: throw IllegalStateException("")
        setSpacingForDirection(outRect, layoutManager, position, itemCount)
    }

    private fun setSpacingForDirection(
        outRect: Rect,
        layoutManager: RecyclerView.LayoutManager,
        position: Int,
        itemCount: Int
    ) {
        // Resolve display mode automatically
        if (displayMode == DisplayMode.VERTICAL) {
            displayMode = resolveDisplayMode(layoutManager)
        }
        when (displayMode) {
            DisplayMode.HORIZONTAL -> {
                outRect.left = spacing
                outRect.right = if (position == itemCount - 1) spacing else 0
                outRect.top = spacing / 2
                outRect.bottom = spacing / 2
            }
            DisplayMode.VERTICAL -> {
                outRect.left = spacing / 2
                outRect.right = spacing / 2
                outRect.top = spacing
                outRect.bottom = if (position == itemCount - 1) spacing else 0
            }
            DisplayMode.GRID -> if (layoutManager is GridLayoutManager) {
                val cols = layoutManager.spanCount
                val rows = itemCount / cols
                outRect.left = spacing
                outRect.right = if (position % cols == cols - 1) spacing else 0
                outRect.top = spacing
                outRect.bottom = if (position / cols == rows - 1) spacing else 0
            }
        }
    }

    private fun resolveDisplayMode(layoutManager: RecyclerView.LayoutManager): DisplayMode {
        if (layoutManager is GridLayoutManager) return DisplayMode.GRID
        return if (layoutManager.canScrollHorizontally()) DisplayMode.HORIZONTAL else DisplayMode.VERTICAL
    }
}

enum class DisplayMode {
    HORIZONTAL, VERTICAL, GRID
}