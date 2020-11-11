package com.duzhaokun123.bilibilihd.ui.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bumptech.glide.Glide
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseFragment
import com.duzhaokun123.bilibilihd.databinding.FragmentSettingsUsersBinding
import com.duzhaokun123.bilibilihd.ui.login.LoginActivity
import com.duzhaokun123.bilibilihd.utils.*
import com.hiczp.bilibili.api.app.model.MyInfo
import com.hiczp.bilibili.api.passport.model.LoginResponse
import de.hdodenhof.circleimageview.CircleImageView

@Suppress("unused")
class SettingsUsersFragment : BaseFragment<FragmentSettingsUsersBinding>() {
    private var loginUserInfoMap: LoginUserInfoMap? = null
    private var myInfo: MyInfo? = null
    private var exportLoginResponse: LoginResponse? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        loginUserInfoMap = Settings.getLoginUserInfoMap()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun initConfig() = NEED_HANDLER

    override fun initLayout() = R.layout.fragment_settings_users

    override fun initView() {
        baseBind.xrv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        baseBind.xrv.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect[0, 0, 0] = resources.getDimensionPixelOffset(R.dimen.divider_height)
            }
        })
        baseBind.xrv.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return UserCardHolder(LayoutInflater.from(context).inflate(R.layout.layout_user_card_item, parent, false))
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder as UserCardHolder).mTvContent.text = loginUserInfoMap!!.getByIndex(position).userId.toString()
                holder.mCv.setOnClickListener {
                    pBilibiliClient.loginResponse = loginUserInfoMap!!.getByIndex(position)
                    loginUserInfoMap!!.setLoggedUid(loginUserInfoMap!!.getByIndex(position).userId)
                    Settings.saveLoginUserInfoMap()
                    reloadLoggedUserInfo()
                }
                holder.mCv.setOnLongClickListener {
                    val popupMenu = PopupMenu(requireContext(), holder.mCv)
                    popupMenu.menuInflater.inflate(R.menu.settings_user, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                        when (item.itemId) {
                            R.id.export -> {
                                auth {
                                    exportLoginResponse = loginUserInfoMap!!.getByIndex(position)
                                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                                    intent.type = "application/json"
                                    intent.putExtra(Intent.EXTRA_TITLE, "loginResponse.json")
                                    startActivityForResult(intent, 1)
                                }
                            }
                            R.id.delete -> AlertDialog.Builder(requireContext()).setIcon(R.drawable.ic_info)
                                    .setTitle(R.string.delete)
                                    .setPositiveButton(android.R.string.ok) { _, _ ->
                                        loginUserInfoMap!!.remove(loginUserInfoMap!!.getByIndex(position).userId)
                                        XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, loginUserInfoMap!!.size)
                                        Settings.saveLoginUserInfoMap()
                                        pBilibiliClient.loginResponse = loginUserInfoMap!!.loggedLoginResponse
                                        reloadLoggedUserInfo()
                                    }
                                    .setNegativeButton(android.R.string.cancel, null)
                                    .show()
                        }
                        true
                    }
                    popupMenu.show()
                    true
                }
                Thread {
                    try {
                        val space = pBilibiliClient.pAppAPI.space(loginUserInfoMap!!.getByIndex(position).userId)
                        runOnUiThread {
                            if (context != null) {
                                GlideUtil.loadUrlInto(context, space.data.card.face, holder.mCivFace, false)
                            }
                            holder.mTvName.text = space.data.card.name
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { TipUtil.showTip(context, e.message) }
                    }
                }.start()
            }

            override fun getItemCount(): Int {
                return if (loginUserInfoMap != null) loginUserInfoMap!!.size else 0
            }

            inner class UserCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val mCivFace: CircleImageView = itemView.findViewById(R.id.civ_face)
                val mTvName: TextView = itemView.findViewById(R.id.tv_name)
                val mTvContent: TextView = itemView.findViewById(R.id.tv_content)
                val mCv: CardView = itemView.findViewById(R.id.cv)
            }
        }
        baseBind.xrv.setLoadingMoreEnabled(false)
        baseBind.xrv.setPullRefreshEnabled(false)
        baseBind.ibDelete.setOnClickListener {
            loginUserInfoMap!!.setLoggedUid(0)
            Settings.saveLoginUserInfoMap()
            pBilibiliClient.loginResponse = null
            reloadLoggedUserInfo()
            BrowserUtil.syncLoggedLoginResponse()
        }
        baseBind.ibAdd.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle(R.string.add)
                    .setIcon(R.drawable.ic_add_circle)
                    .setItems(arrayOf(
                            getString(R.string.login),
                            getString(R.string.import_)
                    )) { _, which ->
                        when (which) {
                            0 -> {
                                val intent = Intent(context, LoginActivity::class.java)
                                startActivity(intent)
                            }
                            1 -> {
                                val intent1 = Intent(Intent.ACTION_GET_CONTENT)
                                intent1.type = "*/*"
                                intent1.addCategory(Intent.CATEGORY_OPENABLE)
                                startActivityForResult(intent1, 0)
                            }
                        }
                    }
                    .show()
        }
    }

    override fun initData() {
        reloadLoggedUserInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            0 -> if (data != null && resultCode == Activity.RESULT_OK) {
                val uri = data.data
                val loginResponse = OtherUtils.readLoginResponseFromUri(context, uri)
                if (loginResponse != null) {
                    loginUserInfoMap!![loginResponse.userId] = loginResponse
                    Settings.saveLoginUserInfoMap()
                    XRecyclerViewUtil.notifyItemsChanged(baseBind.xrv, loginUserInfoMap!!.size)
                    TipUtil.showTip(context, R.string.imported)
                } else {
                    TipUtil.showTip(context, R.string.bad_file)
                }
            }
            1 -> if (data != null) {
                val uri = data.data
                if (!OtherUtils.writeLoginResponseToUri(context, exportLoginResponse, uri)) {
                    TipUtil.showTip(context, R.string.failure)
                } else {
                    TipUtil.showTip(context, R.string.exported)
                }
                exportLoginResponse = null
            }
        }
    }

    override fun onResume() {
        super.onResume()
        reloadLoggedUserInfo()
    }

    private fun reloadLoggedUserInfo() {
        if (loginUserInfoMap!!.loggedUdi != 0L) {
            val loginResponse = loginUserInfoMap!!.loggedLoginResponse
            if (loginResponse != null) {
                baseBind.tvContent.text = loginResponse.userId.toString()
                object : Thread() {
                    override fun run() {
                        try {
                            myInfo = pBilibiliClient.pAppAPI.getMyInfo()
                            handler?.sendEmptyMessage(2)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            runOnUiThread { TipUtil.showTip(context, e.message) }
                        }
                    }
                }.start()
            }
        } else {
            baseBind.tvContent.text = null
            baseBind.tvName.setText(R.string.not_logged_in)
            baseBind.civFace.setImageDrawable(null)
        }
    }

    override fun handlerCallback(msg: Message) {
        when (msg.what) {
            2 -> {
                baseBind.tvName.text = myInfo!!.data.name
                if (context != null) {
                    Glide.with(requireContext()).load(myInfo!!.data.face).into(baseBind.civFace)
                }
            }
        }
    }

    private fun auth(onSucceeded: () -> Unit) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(getString(R.string.auth_to_export))
                .setDeviceCredentialAllowed(true)
                .build()
        val biometricManager = BiometricManager.from(requireContext())
        when (val rec = biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> Log.d(CLASS_NAME, "auth: can authenticate")
            else -> {
                Log.d(CLASS_NAME, "auth: cannot authenticate code $rec")
            }
        }
        val biometricPrompt = BiometricPrompt(this, ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        TipUtil.showTip(context, errString)
                    }

                    override fun onAuthenticationFailed() {
                        TipUtil.showToast("Authentication failed")
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        onSucceeded()
                    }
                })
        biometricPrompt.authenticate(promptInfo)
    }
}