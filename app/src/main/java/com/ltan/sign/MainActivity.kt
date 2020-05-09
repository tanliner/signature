package com.ltan.sign

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import com.jakewharton.rxbinding3.widget.textChanges
import com.ltan.sign.bean.AppInfo
import com.ltan.sign.binder.AppInfoBinder
import com.ltan.sign.binder.HeaderBinder
import com.ltan.sign.binder.ItemClickListener
import com.ltan.sign.decoration.HorizontalLine
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.drakeet.multitype.MultiTypeAdapter
import java.util.concurrent.TimeUnit
import kotlin.String as String1

class MainActivity : BaseActivity() {

    companion object {
        private const val TAG = "Sf"
    }

    @BindView(R2.id.rv_app_list)
    lateinit var appListRv: RecyclerView

    @BindView(R2.id.ed_app_search)
    lateinit var searchEdit: EditText

    private lateinit var appBinder: AppInfoBinder
    private val mItems = ArrayList<Any>()
    private val mItemsBackup = ArrayList<Any>()
    private val mAdapter = MultiTypeAdapter()

    private var editHeight: Int = 0
    private var totalScroll = 0F

    override fun init(savedInstanceState: Bundle?) {
        initEditor()
        initRecycler()
        queryAppList(this)
    }

    override fun contentLayout(): Int {
        return R.layout.activity_main
    }

    private fun initEditor() {
        // pre search
        addDisposable(
            searchEdit.textChanges()
                .subscribeOn(Schedulers.io())
                .debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { str: CharSequence ->
                    mItems.clear()
                    appBinder.setKeyword(str.toString())
                    if (!TextUtils.isEmpty(str)) {
                        mItems.addAll(filter(mItemsBackup, str.toString()))
                    } else {
                        mItems.addAll(mItemsBackup)
                    }
                    mAdapter.notifyDataSetChanged()
                }
        )

        searchEdit.setOnClickListener {
            searchEdit.isFocusable = true
            searchEdit.isFocusableInTouchMode = true
            searchEdit.requestFocus()
            searchEdit.requestFocusFromTouch()
            showSoftInput(searchEdit)
        }
    }

    private fun initRecycler() {
        appBinder = AppInfoBinder()
        mAdapter.register(AppInfo::class.java, appBinder)
        mAdapter.register(String1::class.java, HeaderBinder())

        mAdapter.items = mItems
        appListRv.adapter = mAdapter
        appListRv.layoutManager = LinearLayoutManager(this)
        val decor = HorizontalLine(
            resources.getDimensionPixelSize(R.dimen.app_item_offset_h),
            resources.getDimensionPixelSize(R.dimen.app_item_offset_v)
        )
        decor.setHeaderCount(0)
        appListRv.addItemDecoration(decor)

        appBinder.setItemClick(object : ItemClickListener {
            override fun onItemClick(pos: Int, v: View, type: Int) {
                val obj = mItems[pos]
                if (obj !is AppInfo) {
                    return
                }
                val intent = Intent(this@MainActivity, AppDetailActivity::class.java)
                intent.putExtra("pkg", obj.pkg)
                intent.putExtra("label", obj.appLabel)
                startActivity(intent)
            }
        })
    }

    private fun scrollEditor() {
        // reader editor height when idle
        appListRv.post {
            editHeight = searchEdit.measuredHeight
        }
        appListRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalScroll += dy
                if (totalScroll > editHeight) {
                    searchEdit.translationY = (editHeight * -1.0F)
                } else {
                    searchEdit.translationY = -totalScroll
                }
            }
        })
    }

    private fun queryAppList(ctx: Context) {
        showProgressTips("", 0, 5000)
        addDisposable(Flowable.create({ emitter: FlowableEmitter<ArrayList<AppInfo>> ->
            val pm = ctx.packageManager
            // val appPkgs = pm.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
            // handlePkg(appPkgs)
            val appInfos = pm.getInstalledApplications(PackageManager.MATCH_DISABLED_COMPONENTS)
            emitter.onNext(ArrayList<AppInfo>(handleApp(appInfos)))
        }, BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.w(TAG, "get the bg result ${it.size}")
                dismissDialog()
                mItems.clear()
                // mItems.add("header")
                mItems.addAll(it)
                mItemsBackup.clear()
                mItemsBackup.addAll(it)
                mAdapter.notifyDataSetChanged()
            })
    }

    private fun handlePkg(pkgInfos: List<PackageInfo>) {
        // read version codes
    }

    private fun handleApp(appInfos: List<ApplicationInfo>): List<AppInfo> {
        val pm = this.packageManager
        val apps = ArrayList<AppInfo>()
        for (app in appInfos) {

            Log.v(TAG, "handleApp ${app.packageName}")
            val icon: Drawable? = app.loadIcon(pm)
            val label = app.loadLabel(pm).toString()
            apps.add(AppInfo(app.packageName, label, icon))
        }

        // var tarCtx: Context?
        // try {
        //     tarCtx = ctx.createPackageContext(
        //         app.packageName, Context.CONTEXT_IGNORE_SECURITY.or(Context.CONTEXT_RESTRICTED)
        //     )
        //
        //     label = tarCtx.getString(app.labelRes)
        //     icon = tarCtx.getDrawable(app.icon)
        //     // Log.d(TAG, "test apps pkg name=${app.packageName}, app label=${label} app icon=${icon}")
        // } catch (e: Exception) {
        //     Log.w(TAG, "this apps not found ${app.packageName}, e=$e")
        //     continue
        // }
        // apps.add(AppInfo(label, icon))
        return apps
    }

    private fun filter(source: ArrayList<Any>, key: String1): ArrayList<AppInfo> {
        val ret = ArrayList<AppInfo>()
        for (appInfo in source) {
            if (appInfo !is AppInfo) {
                continue
            }
            if (appInfo.appLabel.contains(key)) {
                ret.add(appInfo)
            }
        }
        return ret
    }
}
