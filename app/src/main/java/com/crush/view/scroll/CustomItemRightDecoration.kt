package com.crush.view.scroll

/**
 * @Author ct
 * @Date 2024/4/17 14:32
 */
import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class CustomItemRightDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val firstItemMargin: Int = dpToPx(context, 5) // Convert 5dp to pixels
    private val otherItemsMargin: Int = dpToPx(context, 26) // Convert 26dp to pixels

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)

        if (position == 0) {
            // First item
            outRect.right = firstItemMargin
        } else {
            // Other items
            outRect.right = otherItemsMargin
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}
