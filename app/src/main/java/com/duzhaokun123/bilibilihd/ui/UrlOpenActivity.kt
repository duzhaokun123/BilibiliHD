package com.duzhaokun123.bilibilihd.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.duzhaokun123.bilibilihd.ui.article.ArticleActivity
import com.duzhaokun123.bilibilihd.ui.play.PlayActivity
import com.duzhaokun123.bilibilihd.ui.userspace.UserSpaceActivity
import com.duzhaokun123.bilibilihd.utils.BrowserUtil
import com.duzhaokun123.bilibilihd.utils.MyBilibiliClientUtil
import com.duzhaokun123.bilibilihd.utils.TipUtil

class UrlOpenActivity : AppCompatActivity() {
    companion object {
        const val TAG = "UrlOpenActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val uri = intent.data
        val scheme = uri!!.scheme
        val host = uri.host
        val path = uri.path

        Log.d(TAG, uri.toString())
        Log.d(TAG, "scheme: $scheme")
        Log.d(TAG, "host: $host")
        Log.d(TAG, "path: $path")

        try {
            var intent1: Intent? = null
            if ("bilibili" != scheme) {
                when (hostLooksLikeWhichType(host)) {
                    Type.SPACE -> {
                        intent1 = Intent(this, UserSpaceActivity::class.java)
                        intent1.putExtra("uid", getUidFromPath(path))
                    }
                    Type.WWW, Type.M -> when (pathLooksLikeWhichType(path)) {
                        Type.READ_MOBILE -> {
                            intent1 = Intent(this, ArticleActivity::class.java)
                            if (path != null) {
                                intent1.putExtra("id", path.substring(13).toLong())
                            }
                        }
                        Type.READ -> {
                            intent1 = Intent(this, ArticleActivity::class.java)
                            if (path != null) {
                                intent1.putExtra("id", path.substring(8).toLong())
                            }
                        }
                        Type.VIDEO -> {
                            intent1 = Intent(this, PlayActivity::class.java)
                            if (path != null) {
                                try {
                                    intent1.putExtra("aid", MyBilibiliClientUtil.bv2av(path.substring(7)))
                                } catch (e: Exception) {
                                    try {
                                        intent1.putExtra("aid", path.substring(9).toLong())
                                    } catch (e1: NumberFormatException) {
                                        intent1.putExtra("aid", path.substring(9, path.length - 1).toLong())
                                    }
                                }
                            }
                        }
                        Type.UNKNOWN -> if (hostLooksLikeWhichType(host) == Type.WWW) {
                            BrowserUtil.openWebViewActivity(this, uri.toString(), true)
                        } else {
                            BrowserUtil.openWebViewActivity(this, uri.toString(), false)
                        }
                        else -> throw RuntimeException("shouldn't be here")
                    }
                    Type.B23TV, Type.LIVE -> BrowserUtil.openWebViewActivity(this, uri.toString(), false, false, true)
                    Type.T -> BrowserUtil.openWebViewActivity(this, uri.toString(), true, true)
                    Type.UNKNOWN -> BrowserUtil.openWebViewActivity(this, uri.toString(), true, false)
                    else -> throw RuntimeException("shouldn't be here")
                }
            } else {
                when (host) {
                    "video" -> {
                        intent1 = Intent(this, PlayActivity::class.java)
                        try {
                            intent1.putExtra("aid", path!!.substring(1).toLong())
                        } catch (e: NumberFormatException) {
                            intent1.putExtra("aid", MyBilibiliClientUtil.bv2av(path!!.substring(1)))
                        }
                    }
                    "article" -> {
                        intent1 = Intent(this, ArticleActivity::class.java)
                        intent1.putExtra("id", path!!.substring(1).toLong())
                    }
                    "space", "author" -> {
                        intent1 = Intent(this, UserSpaceActivity::class.java)
                        intent1.putExtra("uid", path!!.substring(1).toLong())
                    }
                    else -> TipUtil.showToast("不支持 $uri")
                }
            }
            if (intent1 != null) {
                startActivity(intent1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if ("bilibili" != scheme) {
                BrowserUtil.openWebViewActivity(this, uri.toString(), true, false)
            } else {
                TipUtil.showToast("不支持 $uri")
            }
        }
        finish()
    }

    private fun hostLooksLikeWhichType(host: String?): Type {
        return when {
            host == null -> Type.UNKNOWN
            host.startsWith("space.") -> Type.SPACE
            host.startsWith("m.") -> Type.M
            host.startsWith("www.") -> Type.WWW
            "b23.tv" == host -> Type.B23TV
            host.startsWith("live.") -> Type.LIVE
            host.startsWith("t.") -> Type.T
            else -> Type.UNKNOWN
        }
    }

    private fun pathLooksLikeWhichType(path: String?): Type {
        return when {
            path == null -> Type.UNKNOWN
            path.startsWith("/read/mobile") -> Type.READ_MOBILE
            path.startsWith("/read/cv") -> Type.READ
            path.startsWith("/video") -> Type.VIDEO
            else -> Type.UNKNOWN
        }
    }

    private fun getUidFromPath(path: String?): Long {
        if (path == null) {
            return 0
        }
        var slash: Int
        var re: Long = 0
        Log.d("getUidFromPath", "input $path")
        try {
            if (path.indexOf('/', 1).also { slash = it } != -1) {
                re = path.substring(1, slash).toLong()
            } else {
                re = path.substring(1).toLong()
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        Log.d("getUidFromPath", "output $re")
        return re
    }

    internal enum class Type {
        SPACE, VIDEO, M, WWW, READ, READ_MOBILE, B23TV, LIVE, T, UNKNOWN
    }
}