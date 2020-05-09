package com.ltan.sign.binder

import android.view.View

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/9
 * @version 1.0
 */
interface ItemClickListener {
    /**
     * item click for recycler-view
     * Suggestion: bind this during [androidx.recyclerview.widget.RecyclerView.Adapter.onCreateViewHolder]
     * [pos] position
     * [v] position
     * [type] item has multiply elements
     */
    fun onItemClick(pos: Int, v: View, type: Int);
}