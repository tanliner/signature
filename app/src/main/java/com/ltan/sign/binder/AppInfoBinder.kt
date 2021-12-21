package com.ltan.sign.binder

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ltan.sign.R
import com.ltan.sign.bean.AppInfo
import com.ltan.sign.highlight.HighLightBuilder
import me.drakeet.multitype.ItemViewBinder

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/8
 * @version 1.0
 */
class AppInfoBinder : ItemViewBinder<AppInfo, AppInfoBinder.ViewHolder>() {

    private var mKeywords: String = ""
    private var mItemClick: ItemClickListener? = null

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        val holder = ViewHolder(inflater.inflate(R.layout.app_app_list_item, parent, false))
        setHolderClick(holder.itemView, holder, 0)
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, appInfo: AppInfo) {
        holder.iconIv.setImageDrawable(appInfo.appIcon)
        holder.labelTv.text = HighLightBuilder()
            .setStr(appInfo.appLabel)
            .setKeyword(mKeywords)
            .setColor(Color.RED)
            .build()
    }

    fun setKeyword(key: String) {
        mKeywords = key
    }

    fun setItemClick(l : ItemClickListener) {
        mItemClick = l
    }

    private fun setHolderClick(target: View, holder: ViewHolder, type: Int) {
        target.setOnClickListener {
            mItemClick?.onItemClick(holder.adapterPosition, it, type)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconIv: ImageView = itemView.findViewById(R.id.iv_app_icon)
        val labelTv: TextView = itemView.findViewById(R.id.tv_app_label)
    }
}