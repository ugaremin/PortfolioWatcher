package com.ugaremin.portfoliowatcher.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.ugaremin.portfoliowatcher.R

class SwipeToDeleteCallback(private val adapter: PortfolioAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }
    private val icon = ContextCompat.getDrawable(adapter.context, R.drawable.icon_delete)
    private val iconSize = 100

    private val cornerRadius = 32f

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.deleteItem(position)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val itemView = viewHolder.itemView

        if (dX < 0) {
            val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
            c.drawRoundRect(background, cornerRadius, cornerRadius, backgroundPaint)

            val iconTop = itemView.top + (itemView.height - iconSize) / 2
            val iconMargin = (itemView.height - iconSize) / 4
            val iconLeft = itemView.right - iconMargin - iconSize
            val iconRight = itemView.right - iconMargin
            val iconBottom = iconTop + iconSize
            icon?.setBounds(iconLeft, iconTop, iconRight, iconBottom)
            icon?.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
