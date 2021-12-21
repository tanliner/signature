package com.ltan.sign.highlight

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import androidx.annotation.ColorInt

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/9
 * @version 1.0
 */
class HighLightBuilder {
    private var spanBuilder: SpannableStringBuilder = SpannableStringBuilder()
    private lateinit var str: String
    private lateinit var keyword: String
    private var color = 0

    fun setStr(str: String): HighLightBuilder {
        this.str = str
        return this
    }

    fun setKeyword(keyword: String): HighLightBuilder {
        this.keyword = keyword
        return this
    }

    fun setColor(@ColorInt color: Int): HighLightBuilder {
        this.color = color
        return this
    }

    fun build(): CharSequence? {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(keyword)) {
            return str
        }
        if (!str.contains(keyword, ignoreCase = true)) {
            return str
        }
        spanBuilder.clear()
        spanBuilder.append(str)
        val charaStyle: CharacterStyle = ForegroundColorSpan(color)
        val start = str.indexOf(keyword, ignoreCase = true)
        val end = start + keyword.length
        spanBuilder.setSpan(charaStyle, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spanBuilder
    }
}