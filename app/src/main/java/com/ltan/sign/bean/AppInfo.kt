package com.ltan.sign.bean

import android.graphics.drawable.Drawable

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/8
 * @version 1.0
 */
data class AppInfo(
    var pkg: String = "",
    var appLabel: String = "",
    val appIcon: Drawable?
)