package com.duzhaokun123.bilibilihd.bases

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.DisplayCutout
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.utils.Handler
import com.duzhaokun123.bilibilihd.utils.OtherUtils
import com.duzhaokun123.bilibilihd.utils.TipUtil

abstract class BaseActivity2<Layout : ViewDataBinding> : AppCompatActivity(), Handler.IHandlerMessageCallback {
    enum class Config {
        // FIXME: 21-1-29 Activity 重构导致全屏失效
        FULLSCREEN,
        HIDE_ACTION_BAR,
        FIX_LAYOUT,
        TRANSPARENT_ACTION_BAR,
        NEED_HANDLER
    }

    val className by lazy { this::class.simpleName }
    val startIntent: Intent by lazy { intent }
    val actionBarHeight get() = supportActionBar?.height ?: 0
    var isStopped = true
        private set
    var isFirstCreate = true
        private set
    var handler: Handler? = null
        private set
    lateinit var baseBind: Layout
        private set

    private val config by lazy { initConfig() }
    private val windowInsetsControllerCompat by lazy {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
    private val onApplyWindowInsetsCallbacks = mutableMapOf<Int, (WindowInsetsCompat) -> Unit>()

    private var isFirstWindowForce = true
    private var layoutFixed = false
    private var displayCutout: DisplayCutout? = null
    private var isNavigationBarOnBottom = true
    private var lastWindowInsetsCompat: WindowInsetsCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) isFirstCreate = false

        super.onCreate(savedInstanceState)

        if (Config.NEED_HANDLER in config) {
            handler = Handler(this)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setFullscreen(Config.FULLSCREEN in config)
        setHideActionBar(Config.HIDE_ACTION_BAR in config)

        val abc = if (Config.TRANSPARENT_ACTION_BAR in config) Color.TRANSPARENT else getColor(R.color.actionBarHalfTransparent)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setBackgroundDrawable(ColorDrawable(abc))
        }

        window.statusBarColor = abc

        baseBind = DataBindingUtil.setContentView(this, initLayout())
        ViewCompat.setOnApplyWindowInsetsListener(baseBind.root) { v, insets ->
            lastWindowInsetsCompat = insets
            onApplyWindowInsets(insets)
            onApplyWindowInsetsCallbacks.forEach { (_, v) -> v.invoke(insets) }
            insets.getInsets(WindowInsetsCompat.Type.systemBars()).let {
                v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    leftMargin = it.left
                    rightMargin = it.right
                    if (Config.FIX_LAYOUT in config) {
                        topMargin = it.top
                        bottomMargin = it.bottom
                    }
                }
            }
            WindowInsetsCompat.CONSUMED
        }

        savedInstanceState?.let { onRestoreInstanceState2(it) }

        findViews()
        initView()
        initData()
        TipUtil.registerCoordinatorLayout(this, initRegisterCoordinatorLayout())
    }

    override fun onStart() {
        super.onStart()
        isStopped = false
    }

    override fun onResume() {
        super.onResume()
        setFullscreen(Config.FULLSCREEN in config)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus) {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val viewHeight = displayMetrics.heightPixels
            val decorViewHeight = window.decorView.height
            isNavigationBarOnBottom = decorViewHeight != viewHeight

            if (displayCutout == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                displayCutout = window.decorView.rootWindowInsets.displayCutout
            }

            if (isFirstWindowForce) {
                isFirstWindowForce = false
                findViewById<View?>(android.R.id.statusBarBackground)?.elevation = 0.01F
                setActionBarUp(false)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        isStopped = true
    }

    override fun onDestroy() {
        super.onDestroy()
        TipUtil.unregisterCoordinatorLayout(this)
        handler?.destroy()
        handler = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun setFullscreen(v: Boolean) {
        val decorView = window.decorView
        if (v) {
            if (Build.VERSION.SDK_INT <= 29) {
                decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            } else {
                windowInsetsControllerCompat.hide(WindowInsetsCompat.Type.systemBars())
            }
        } else {
            if (Build.VERSION.SDK_INT <= 29) {
                decorView.systemUiVisibility = (decorView.systemUiVisibility
                        and (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY).inv())
            } else {
                windowInsetsControllerCompat.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    fun setHideActionBar(v: Boolean) {
        if (v)
            supportActionBar?.hide()
        else
            supportActionBar?.show()
    }

    fun setActionBarUp(v: Boolean, anima: Boolean = false) {
        val supportActionBar = supportActionBar ?: return
        val e1 = OtherUtils.dp2px(4F).toFloat()
        if (anima) {
            val start = supportActionBar.elevation
            val end = if (v) e1 else 0.01F
            if (start == end) return
            ObjectAnimator.ofFloat(supportActionBar, "elevation", start, end).apply {
                duration = 200
            }.start()
        } else {
            if (v)
                supportActionBar.elevation = e1
            else
                supportActionBar.elevation = 0.01F
        }
    }

    fun registerOnApplyWindowInsets(key: Int, onApplyWindowInsets: (windowInsetsCompat: WindowInsetsCompat) -> Unit) {
        onApplyWindowInsetsCallbacks[key] = onApplyWindowInsets
        lastWindowInsetsCompat?.let { onApplyWindowInsets(it) }
    }

    fun unregisterOnApplyWindowInsets(key: Int) {
        onApplyWindowInsetsCallbacks.remove(key)
    }

    abstract fun initConfig(): Set<Config>

    @LayoutRes
    abstract fun initLayout(): Int

    open fun findViews() {}
    abstract fun initView()
    abstract fun initData()
    open fun initRegisterCoordinatorLayout() = null as CoordinatorLayout?
    open fun onRestoreInstanceState2(savedInstanceState: Bundle) {}
    open fun onApplyWindowInsets(windowInsetsCompat: WindowInsetsCompat) {}

    private fun DisplayCutout.compatSafeInsetTop() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) this.safeInsetTop else 0

    private fun DisplayCutout.compatSafeInsetBottom() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) this.safeInsetBottom else 0

    inner class NSVAutoSetActionBarUpListener : NestedScrollView.OnScrollChangeListener {
        override fun onScrollChange(v: NestedScrollView?, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
            setActionBarUp(scrollY > 0, true)
        }
    }

    inner class RVAutoSetActionBarUpListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            setActionBarUp(recyclerView.computeVerticalScrollOffset() > 0, true)
        }
    }
}