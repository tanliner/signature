package com.ltan.sign

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import butterknife.BindView
import com.ltan.sign.util.AppUtil
import java.util.*


/**
 * @desc
 * @author  tanlin
 * @since  2020/5/9
 * @version 1.0
 */
class AppDetailActivity : BaseActivity() {

    companion object {
        const val TAG = "Sf/AppDetail"
        const val ARG_PKG = "arg_package_name"
        const val ARG_LABEL = "arg_app_label"
    }

    @BindView(R.id.tv_app_pkg)
    lateinit var appPkgTv: TextView

    @BindView(R.id.tv_app_version)
    lateinit var appVersionTv: TextView

    @BindView(R.id.tv_app_signature)
    lateinit var appSignatureTv: TextView

    @BindView(R.id.tv_app_signature_copy)
    lateinit var signatureCopyBtn: TextView

    @BindView(R.id.tv_app_package_copy)
    lateinit var packageCopyBtn: TextView

    @BindView(R.id.cbx_upper_case)
    lateinit var upperCaseCbx: CheckBox

    @BindView(R.id.cbx_with_colon)
    lateinit var withColonCbx: CheckBox

    @BindView(R.id.rg_signature_type)
    lateinit var radioGroup: RadioGroup

    private lateinit var signature: String
    private lateinit var targetLabel: String
    private lateinit var targetPackage: String

    private var isSignUpperCase = true
    private var signWithColon = true
    private var type = "MD5"

    override fun contentLayout(): Int {
        return R.layout.activity_detail
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)
        processArgs()
        setCustomActionBar()
        initCopy()
        initRadioGroup()
        initCbx()
        getAppInfo(targetPackage)
    }

    private fun processArgs() {
        if (intent == null) {
            return
        }
        targetPackage = intent.getStringExtra(ARG_PKG) ?: ""
        targetLabel = intent.getStringExtra(ARG_LABEL) ?: ""
    }

    private fun setCustomActionBar() {
        val lp: ActionBar.LayoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        val mActionBarView: View =
            LayoutInflater.from(this).inflate(R.layout.actionbar_layout, null)
        val actionBar = supportActionBar ?: return
        actionBar.setCustomView(mActionBarView, lp)
        actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowHomeEnabled(false)
        actionBar.setDisplayShowTitleEnabled(false)
        title = targetLabel
        mActionBarView.findViewById<TextView>(android.R.id.title).text = title
    }

    private fun initCopy() {
        val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        signatureCopyBtn.setOnClickListener {
            val clipData = ClipData.newPlainText("sign", getFixedSign())
            cm.setPrimaryClip(clipData)
            toastShort(resources.getString(R.string.app_signature_copied))
        }
        packageCopyBtn.setOnClickListener {
            val clipData = ClipData.newPlainText("pkg", targetPackage)
            cm.setPrimaryClip(clipData)
            toastShort(resources.getString(R.string.app_pkg_name_copied))
        }
    }

    private fun initRadioGroup() {
        radioGroup.setOnCheckedChangeListener { group, id ->
            when (id) {
                R.id.rb_signature_md5 -> {
                    type = "MD5"
                }
                R.id.rb_signature_sha1 -> {
                    type = "SHA1"
                }
                R.id.rb_signature_sha256 -> {
                    type = "SHA256"
                }
            }
            getSignature(targetPackage, type)
        }
    }

    private fun initCbx() {
        upperCaseCbx.setOnCheckedChangeListener { view, isChecked ->
            isSignUpperCase = isChecked
            appSignatureTv.text = buildSignStr(getString(R.string.app_signature, getFixedSign()))
        }
        withColonCbx.setOnCheckedChangeListener { view, isChecked ->
            signWithColon = isChecked
            getSignature(targetPackage, type)
        }
    }

    private fun getAppInfo(app: String) {
        val pm = packageManager
        // val applicationInfo = pm.getApplicationInfo(app, 0);
        // appNameTv.text = getString(R.string.app_target_name, applicationInfo.loadLabel(pm))
        appPkgTv.text = getString(R.string.app_package, app)
        val pkgInfo = pm.getPackageInfo(app, 0)
        appVersionTv.text =
            getString(R.string.app_version, pkgInfo.versionCode, pkgInfo.versionName)
        getSignature(targetPackage, "MD5")
    }

    private fun getSignature(pkg: String, type: String) {
        RxUtils.async({
            AppUtil.getSignature(this, pkg, type, signWithColon)
        }, {
            this.signature = it
            appSignatureTv.text = buildSignStr(getString(R.string.app_signature, getFixedSign()))
        })
    }

    private fun getFixedSign(): String {
        if (isSignUpperCase) {
            return signature.toUpperCase(Locale.US)
        }
        return signature
    }

    private fun buildSignStr(txt: String) : CharSequence {
        val ssb = SpannableStringBuilder(txt)
        val sizeSpan = RelativeSizeSpan(0.65F)
        val index = txt.indexOf(':')
        ssb.setSpan(sizeSpan, index + 1, txt.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        val colorSpan = ForegroundColorSpan(resources.getColor(android.R.color.holo_red_dark))
        ssb.setSpan(colorSpan,index + 1, txt.length, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }
}