package com.duzhaokun123.bilibilihd.ui.main

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivityMainBinding
import com.duzhaokun123.bilibilihd.ui.JumpActivity
import com.duzhaokun123.bilibilihd.ui.ToolActivity
import com.duzhaokun123.bilibilihd.ui.login.LoginActivity
import com.duzhaokun123.bilibilihd.ui.settings.SettingsActivity
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity.Companion.enter
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil.setLevelDrawable
import com.duzhaokun123.bilibilihd.utils.Refreshable
import com.duzhaokun123.bilibilihd.utils.TipUtil
import com.duzhaokun123.bilibilihd.utils.systemBars
import com.hiczp.bilibili.api.app.model.MyInfo
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : BaseActivity2<ActivityMainBinding>() {
    private var mRlMyInfo: RelativeLayout? = null
    private var mTvUsername: TextView? = null
    private var mTvBBi: TextView? = null
    private var mTvCoins: TextView? = null
    private var mCivFace: CircleImageView? = null
    private var mIvLevel: ImageView? = null
    private var homeFragment: Fragment? = null
    private var historyFragment: Fragment? = null
    private var dynamicFragment: Fragment? = null
    private var lastBackPassTime = -1L
    private var myInfo: MyInfo? = null
    private var first = true
    private var title: String? = null
    private var defaultNavWidth = 0

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        this.title = title.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setIconifiedByDefault(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu)
            actionBar.setDisplayShowHomeEnabled(true)
        }
    }

    override fun onRestoreInstanceState2(savedInstanceState: Bundle) {
        first = savedInstanceState.getBoolean("first")
        title = savedInstanceState.getString("title")
    }

    override fun onResume() {
        super.onResume()
        reloadMyInfo()
    }

    override fun onBackPressed() {
        if (baseBind.dlMain != null && baseBind.dlMain!!.isOpen) {
            baseBind.dlMain!!.close()
            return
        }
        val currentTime = System.currentTimeMillis()
        if (lastBackPassTime == -1L || currentTime - lastBackPassTime >= 2000) {
//            ToastUtil.sendMsg(MainActivity.this, R.string.passe_again_to_quit);
            TipUtil.showSnackbar(baseBind.flMain, R.string.passe_again_to_quit)
            lastBackPassTime = currentTime
            return
        }
        super.onBackPressed()
    }

    private fun reloadMyInfo() {
        if (Application.getPBilibiliClient().isLogin) {
            object : Thread() {
                override fun run() {
                    try {
                        myInfo = Application.getPBilibiliClient().pAppAPI.getMyInfo()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread {
                            if (baseBind.dlMain == null) {
                                TipUtil.showTip(this@MainActivity, e.message)
                            } else {
                                TipUtil.showToast(e.message)
                            }
                        }
                    }
                    if (myInfo != null && handler != null) {
                        handler!!.sendEmptyMessage(0)
                    }
                }
            }.start()
        } else {
            if (handler != null) {
                handler!!.sendEmptyMessage(1)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (baseBind.dlMain != null) {
                    if (baseBind.dlMain!!.isOpen) {
                        baseBind.dlMain!!.close()
                    } else {
                        baseBind.dlMain!!.open()
                        reloadMyInfo()
                    }
                }
                if (baseBind.ml != null) {
                    if (baseBind.ml!!.progress == 0f) {
                        baseBind.ml!!.transitionToEnd()
                    } else {
                        baseBind.ml!!.transitionToStart()
                        reloadMyInfo()
                    }
                }
                true
            }
            R.id.search -> {
                onSearchRequested()
                true
            }
            R.id.refresh -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.fl_main)
                if (fragment is Refreshable) {
                    (fragment as Refreshable).onRefresh()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun initConfig() = setOf(Config.NEED_HANDLER)

    override fun initLayout() =  R.layout.activity_main

    override fun findViews() {
        mRlMyInfo = baseBind.navMain.getHeaderView(0) as RelativeLayout
        mTvUsername = mRlMyInfo!!.findViewById(R.id.tv_username)
        mIvLevel = mRlMyInfo!!.findViewById(R.id.iv_level)
        mTvBBi = mRlMyInfo!!.findViewById(R.id.tv_bBi)
        mTvCoins = mRlMyInfo!!.findViewById(R.id.tv_coins)
        mCivFace = mRlMyInfo!!.findViewById(R.id.civ_face)
    }

    override fun initView() {
        if (homeFragment == null && first) {
            first = false
            homeFragment = HomeFragment()
            supportFragmentManager.beginTransaction().add(R.id.fl_main, homeFragment as HomeFragment).commitAllowingStateLoss()
            setTitle(R.string.home)
        } else {
            setTitle(title!!)
        }
        if (baseBind.dlMain != null) {
            baseBind.dlMain!!.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
                override fun onDrawerOpened(drawerView: View) {
                    reloadMyInfo()
                }
            })
        }
        baseBind.navMain.setNavigationItemSelectedListener { item: MenuItem ->
            if (baseBind.dlMain != null) {
                baseBind.dlMain!!.closeDrawers()
            }
            var intent: Intent? = null
            when (item.itemId) {
                R.id.home -> {
                    if (homeFragment == null) {
                        homeFragment = HomeFragment()
                    }
                    setTitle(R.string.home)
                    supportFragmentManager.beginTransaction().replace(R.id.fl_main, homeFragment!!).commitAllowingStateLoss()
                }
                R.id.dynamic -> {
                    if (dynamicFragment == null) {
                        dynamicFragment = DynamicFragment()
                    }
                    setTitle(R.string.dynamic)
                    supportFragmentManager.beginTransaction().replace(R.id.fl_main, dynamicFragment!!).commitAllowingStateLoss()
                }
                R.id.history -> {
                    if (historyFragment == null) {
                        historyFragment = HistoryFragment()
                    }
                    setTitle(R.string.history)
                    supportFragmentManager.beginTransaction().replace(R.id.fl_main, historyFragment!!).commitAllowingStateLoss()
                }
                R.id.tools -> intent = Intent(this@MainActivity, ToolActivity::class.java)
                R.id.settings -> intent = Intent(this@MainActivity, SettingsActivity::class.java)
                R.id.junp -> intent = Intent(this@MainActivity, JumpActivity::class.java)
            }
            intent?.let { startActivity(it) }
            true
        }
        mRlMyInfo!!.setOnClickListener {
            if (Application.getPBilibiliClient().isLogin) {
                enter(this, Application.getPBilibiliClient().uid, mCivFace, mTvUsername)
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun initData() {}
    override fun initRegisterCoordinatorLayout() = baseBind.flMain

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (defaultNavWidth == 0) {
            defaultNavWidth = baseBind.navMain.width
        }
        setActionBarUp(true)
    }

    override fun onApplyWindowInsets(windowInsetsCompat: WindowInsetsCompat) {
        windowInsetsCompat.systemBars.let {
            baseBind.navMain.updatePadding(top = it.top, bottom = it.bottom)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            0 -> {
                Glide.with(this@MainActivity).load(myInfo!!.data.face).into(mCivFace!!)
                mTvUsername!!.text = myInfo!!.data.name
                setLevelDrawable(mIvLevel!!, myInfo!!.data.level)
                //                    mTvBBi.setText(getString(R.string.b_bi) + ": " + myInfo.getData().get);
                mTvBBi!!.text = getString(R.string.b_bi) + ": --"
                mTvCoins!!.text = getString(R.string.coins) + ": " + myInfo!!.data.coins
                if (myInfo!!.data.vip.type != 0) {
                    mTvUsername!!.setTextColor(getColor(R.color.colorAccent))
                }
            }
            1 -> {
                mCivFace!!.setImageDrawable(null)
                mTvUsername!!.setText(R.string.not_logged_in)
                mIvLevel!!.setImageDrawable(null)
                mTvBBi!!.text = getString(R.string.b_bi) + ": --"
                mTvCoins!!.text = getString(R.string.coins) + ": --"
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("first", first)
        outState.putString("title", title)
    }
}