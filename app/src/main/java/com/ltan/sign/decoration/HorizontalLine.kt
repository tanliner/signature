package com.ltan.sign.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/9
 * @version 1.0
 */
class HorizontalLine constructor(offsetH: Int, offsetV: Int) : RecyclerView.ItemDecoration() {

    private var mHeaderCount = 0

    private var mOffsetH: Int = offsetH
    private var mOffsetV: Int = offsetV

    private var mDividerHeight = 2
    private lateinit var mLinePaint: Paint
    private var mDividerColor = Color.DKGRAY

    init {
        initLinePaint()
    }

    fun setHeaderCount(count: Int) {
        mHeaderCount = count
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.set(mOffsetH, mOffsetV, mOffsetH, mOffsetV)
        val pos = parent.getChildAdapterPosition(view)
        if (pos < mHeaderCount) {
            outRect.set(0, 0, 0, 0)
            if (pos == mHeaderCount - 1) {
                outRect.bottom = mOffsetV
            }
            return
        }
        if (pos == 0) {
            outRect.top = mOffsetV / 2
        } else {
            outRect.top = 0
        }
    }

    private fun initLinePaint() {
        mLinePaint = Paint()
        mLinePaint.isAntiAlias = true
        mLinePaint.isDither = true
        mLinePaint.color = mDividerColor
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent, state)
    }

    private fun drawHorizontal(
        c: Canvas,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left + child.paddingLeft
            val top = child.bottom + params.bottomMargin + mOffsetV / 2
            val right = child.right + params.rightMargin
            val bottom: Int = top + mDividerHeight
            val rect = Rect(left, top, right, bottom)
            c.drawRect(rect, mLinePaint)
        }
    }
}