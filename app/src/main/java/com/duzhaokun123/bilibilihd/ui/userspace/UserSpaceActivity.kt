package com.duzhaokun123.bilibilihd.ui.userspace

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseActivity2
import com.duzhaokun123.bilibilihd.databinding.ActivityUserSpaceBinding
import com.duzhaokun123.bilibilihd.utils.*
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil.setLevelDrawable
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil.setSixDrawable
import com.duzhaokun123.bilibilihd.utils.ImageViewUtil.viewImage
import com.duzhaokun123.bilibilihd.utils.LinkifyUtil.addAllLinks
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hiczp.bilibili.api.app.model.Space
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class UserSpaceActivity : BaseActivity2<ActivityUserSpaceBinding>() {
    private var mSpace: Space? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_space_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.open_in_browser) {
            BrowserUtil.openCustomTab(this, MyBilibiliClientUtil.getUserSpaceLink(startIntent.getLongExtra(EXTRA_UID, 0)))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun initConfig() = setOf(Config.TRANSPARENT_ACTION_BAR)

    override fun initLayout() = R.layout.activity_user_space

    @SuppressLint("SetTextI18n")
    override fun initView() {
        if (Settings.layout.isUserSpaceUseWebView) {
            BrowserUtil.openWebViewActivity(this, "https://space.bilibili.com/${startIntent.getLongExtra(EXTRA_UID, 0)}", true, true)
            finish()
            return
        }
        baseBind.civFace.setImageDrawable(ObjectCache.get(startIntent.getStringExtra(EXTRA_FACE_CACHE_ID)) as Drawable?)
        baseBind.tvName.text = ObjectCache.get(startIntent.getStringExtra(EXTRA_NAME_CACHE_ID)) as CharSequence?
        baseBind.tvUid.text = "UID: " + startIntent.getLongExtra(EXTRA_UID, 0)
        baseBind.vp.adapter = MyFragmentStateAdapter(this)
        TabLayoutMediator(baseBind.tl, baseBind.vp) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.setText(R.string.home)
                1 -> tab.setText(R.string.dynamic)
                2 -> tab.setText(R.string.submit)
                3 -> tab.setText(R.string.favorite)
            }
        }.attach()
        baseBind.civFace.setOnClickListener {
            if (mSpace != null) {
                viewImage(this, mSpace!!.data.card.face, baseBind.civFace, true)
            }
        }
        title = null
        baseBind.tvFans.setOnClickListener {
            BrowserUtil.openWebViewActivity(this,
                    "https://space.bilibili.com/h5/follow?type=fans&mid=${startIntent.getLongExtra(EXTRA_UID, 0)}&night=${if (OtherUtils.isNightMode()) 1 else 0}", false, true)
        }
        baseBind.tvWatching.setOnClickListener {
            BrowserUtil.openWebViewActivity(this,
                    "https://space.bilibili.com/h5/follow?type=follow&mid=${startIntent.getLongExtra(EXTRA_UID, 0)}&night=${if (OtherUtils.isNightMode()) 1 else 0}", false, true)
        }
    }

    override fun initData() {
        if (Settings.layout.isUserSpaceUseWebView) return

        GlobalScope.launch(Dispatchers.IO) {
            try {
                mSpace = Application.getPBilibiliClient().pAppAPI.space(startIntent.getLongExtra(EXTRA_UID, 0))

            } catch (e: Exception) {
                e.printStackTrace()
                kRunOnUiThread { TipUtil.showToast(e.message) }
            }
            if (mSpace != null) {
                kRunOnUiThread { setInfo() }
            }
        }
    }

    private fun setInfo() {
        Glide.with(this).load(mSpace!!.data.card.face).into(baseBind.civFace)
        if (mSpace!!.data.images.imgUrl == "") {
            Glide.with(this).load("https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png").into(baseBind.ivSpaceImage)
            baseBind.ivSpaceImage.setOnClickListener { viewImage(this, "https://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png", baseBind.ivSpaceImage, true) }
        } else {
            Glide.with(this).load(mSpace!!.data.images.imgUrl).into(baseBind.ivSpaceImage)
            baseBind.ivSpaceImage.setOnClickListener { viewImage(this, mSpace!!.data.images.imgUrl, baseBind.ivSpaceImage, true) }
        }
        baseBind.space = mSpace
        setSixDrawable(baseBind.ivSex, mSpace!!.data.card.sex)
        setLevelDrawable(baseBind.ivLevel, mSpace!!.data.card.levelInfo.currentLevel)
        addAllLinks(baseBind.tvSign)
        if (mSpace!!.data.card.vip.vipType != 0) {
            baseBind.tvName.setTextColor(getColor(R.color.colorAccent))
        }
    }

    internal inner class MyFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        private val mHomeFragment by lazy { HomeFragment(mSpace) }
        private val mTrendFragment by lazy { DynamicFragment(mSpace) }
        private val mSubmitFragment by lazy { SubmitFragment(mSpace) }
        private val mFavoriteFragment by lazy { FavoriteFragment(mSpace) }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> mHomeFragment
                1 -> mTrendFragment
                2 -> mSubmitFragment
                3 -> mFavoriteFragment
                else -> Fragment()
            }
        }

        override fun getItemCount() = 4
    }

    companion object {
        const val EXTRA_UID = "uid"
        const val EXTRA_FACE_CACHE_ID = "face_cache_id"
        const val EXTRA_NAME_CACHE_ID = "name_cache_id"

        private fun enter(activity: Activity, uid: Long,
                          faceView: View?, faceDrawable: Drawable?,
                          nameView: View?, nameCS: CharSequence?) {
            val intent = Intent(activity, UserSpaceActivity::class.java)
            intent.putExtra(EXTRA_UID, uid)
            if (Settings.layout.isUserSpaceUseWebView) {
                BrowserUtil.openWebViewActivity(activity, "https://space.bilibili.com/$uid", true, true)
            } else {
                intent.putExtra(EXTRA_FACE_CACHE_ID, ObjectCache.put(faceDrawable))
                intent.putExtra(EXTRA_NAME_CACHE_ID, ObjectCache.put(nameCS))
                val pairs = ArrayList<Pair<View, String>>()
                if (faceView != null) {
                    pairs.add(Pair.create(faceView, "face"))
                }
                if (nameView != null) {
                    pairs.add(Pair.create(nameView, "name"))
                }
                activity.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity, *pairs.toTypedArray()).toBundle())
            }
        }

        @JvmStatic
        @JvmOverloads
        fun enter(activity: Activity, uid: Long, faceView: ImageView? = null, nameView: TextView? = null) {
            enter(activity, uid, faceView, faceView?.drawable, nameView, nameView?.text)
        }
    }
}