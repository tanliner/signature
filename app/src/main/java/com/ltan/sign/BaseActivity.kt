package com.ltan.sign

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import butterknife.ButterKnife
import butterknife.Unbinder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 *
 * @desc
 * @author  tanlin
 * @since  2020/5/9
 * @version 1.0
 */
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private const val MSG_DISMISS = 0x101
    }

    private var mCompositeDisposable: CompositeDisposable? = null
    private var mInputMethodManager: InputMethodManager? = null
    private lateinit var unbinder: Unbinder

    private lateinit var mMainHandler: Handler
    private var mDlgMissAction: Runnable? = null
    private var mProgsDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainHandler = Handler()
        setContentView(contentLayout())
        unbinder = ButterKnife.bind(this)
        init(savedInstanceState)
    }

    abstract fun contentLayout(): Int

    protected open fun init(savedInstanceState: Bundle?) {
    }

    protected fun addDisposable(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    protected fun removeDisposable() {
        mCompositeDisposable?.clear()
    }

    fun showSoftInput(focus: View) {
        mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        mInputMethodManager?.showSoftInput(focus, 0)
    }

    fun hideSoftInput() {
        val view = window.decorView
        mInputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    protected fun post(action: Runnable) {
        mMainHandler.post(action)
    }

    protected fun post(action: Runnable, delay: Long) {
        mMainHandler.postDelayed(action, delay)
    }

    protected fun toastShort(msg: String) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mMainHandler.post { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun toastLong(msg: String) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    protected fun showProgressTips(tips: CharSequence, iconId: Int, duration: Int) {
        initDismissAction();
        initProgressContent(tips, iconId)
        mProgsDialog?.setCancelable(false)
        mProgsDialog?.setCanceledOnTouchOutside(false)
        mProgsDialog?.show()
        if (duration > 0) {
            mMainHandler.sendMessageDelayed(
                mMainHandler.obtainMessage(MSG_DISMISS, mDlgMissAction), duration.toLong()
            )
        }
    }

    open fun dismissDialog() {
        mDlgMissAction?.run()
    }

    private fun initDismissAction() {
        if (mDlgMissAction != null) {
            return
        }
        mDlgMissAction = Runnable {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                return@Runnable
            }
            if (mProgsDialog != null && mProgsDialog!!.isShowing) {
                mProgsDialog!!.dismiss()
            }
            mMainHandler.removeMessages(MSG_DISMISS)
        }
    }

    private fun initProgressContent(tips: CharSequence, iconId: Int) {
        if (mProgsDialog == null) {
            mProgsDialog = inflateDialog(R.style.SignatureDialog)
        } else {
            mDlgMissAction?.run()
        }
        val icon: ImageView = mProgsDialog!!.findViewById(R.id.iv_toast_tips)
        val progressBar: ProgressBar = mProgsDialog!!.findViewById(R.id.progress_bar)
        val title: TextView = mProgsDialog!!.findViewById(android.R.id.message)
        title.text = tips
        if (iconId > 0) {
            icon.setImageResource(iconId)
            progressBar.visibility = View.GONE
            centerIconIfNeed(tips, title, icon)
        } else {
            icon.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            centerIconIfNeed(tips, title, progressBar)
        }
    }

    private fun inflateDialog(themeId: Int): Dialog {
        val dialog = Dialog(this, themeId)
        val root: View = layoutInflater.inflate(R.layout.toast_icon_layout, null)
        dialog.setContentView(root)
        return dialog
    }

    private fun centerIconIfNeed(tips: CharSequence, title: View, icon: View) {
        if (TextUtils.isEmpty(tips)) {
            title.visibility = View.GONE
            val lp = icon.layoutParams as RelativeLayout.LayoutParams
            lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
            icon.layoutParams = lp
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeDisposable()
        unbinder.unbind()
        mDlgMissAction?.let {
            mMainHandler.removeCallbacks(it)
        }
    }
}