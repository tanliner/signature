package com.ltan.sign.binder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.ltan.sign.R
import me.drakeet.multitype.ItemViewBinder

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/9
 * @version 1.0
 */
class HeaderBinder : ItemViewBinder<String, HeaderBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val edt = EditText(parent.context)
        val height = parent.resources.getDimensionPixelSize(R.dimen.app_search_edit_height)
        edt.background = null
        val lp = ViewGroup.LayoutParams(-1, height)
        edt.layoutParams = lp
        val holder = ViewHolder(edt)
        return holder;
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: String) {
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}