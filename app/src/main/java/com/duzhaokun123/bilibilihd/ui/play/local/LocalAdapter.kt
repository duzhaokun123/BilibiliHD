package com.duzhaokun123.bilibilihd.ui.play.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.documentfile.provider.DocumentFile
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.bases.BaseSimpleAdapter
import com.duzhaokun123.bilibilihd.databinding.ItemLocalVideoCardBinding
import com.duzhaokun123.bilibilihd.model.Entry
import com.duzhaokun123.bilibilihd.ui.play.ordinary.OrdinaryPlayActivity
import com.duzhaokun123.bilibilihd.utils.GlideUtil
import com.duzhaokun123.bilibilihd.utils.GsonUtil
import com.duzhaokun123.bilibilihd.utils.OtherUtils
import com.github.salomonbrys.kotson.fromJson
import java.io.InputStreamReader

class LocalAdapter(context: Context, root: Uri, private val model: LocalPlayActivity.LocalVideoModel) : BaseSimpleAdapter<ItemLocalVideoCardBinding>(context) {
    private val documentFile = DocumentFile.fromTreeUri(context, root)
    private val files = documentFile?.listFiles()

    override fun getItemCount() = documentFile?.listFiles()?.size ?: 0

    override fun initLayout() = R.layout.item_local_video_card

    override fun initView(baseBind: ItemLocalVideoCardBinding, position: Int) {
        baseBind.llHsv.removeAllViews()
    }

    @SuppressLint("SetTextI18n")
    override fun initData(baseBind: ItemLocalVideoCardBinding, position: Int) {
        files!![position].let { file1 ->
            file1.listFiles().forEach { file2 ->
                val button = Button(context).apply {
                    text = file2.name
                    setBackgroundResource(R.drawable.rb_video_page_bg)
                }
                val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.rightMargin = OtherUtils.dp2px(5f)
                baseBind.llHsv.addView(button, params)
                file2.findFile("entry.json")?.uri?.let {
                    val entry: Entry
                    context.contentResolver.openInputStream(it)!!.use { inputStream ->
                        InputStreamReader(inputStream).use { inputStreamReader ->
                            entry = GsonUtil.getGsonInstance().fromJson(inputStreamReader)
                        }
                    }
                    baseBind.tvTitle.text = entry.title
                    entry.pageData?.part?.let { part -> button.text = part }
                    entry.ep?.let { ep -> button.text = "${ep.index}: ${ep.indexTitle}" }
                    GlideUtil.loadUrlInto(context, entry.cover, baseBind.ivCover, false)

                    button.setOnClickListener {
                        model.title.value = Pair(entry.title,
                                entry.pageData?.part
                                        ?: entry.ep?.indexTitle)
                        model.videoAudioUri.value = Pair(
                                file2.findFile(entry.typeTag)?.findFile("video.m4s")?.uri,
                                if (entry.hasDashAudio)
                                    file2.findFile(entry.typeTag)?.findFile("audio.m4s")?.uri
                                else
                                    null
                        )
                        model.danmakuUri.value = file2.findFile("danmaku.xml")?.uri
                        if (entry.pageData != null) {
                            model.widthHeight.value = if (entry.pageData!!.rotate == 0) {
                                Pair(entry.pageData!!.width, entry.pageData!!.height)
                            } else {
                                Pair(entry.pageData!!.height, entry.pageData!!.width)
                            }
                        }
                        if (entry.ep != null) {
                            model.widthHeight.value = if (entry.ep!!.rotate == 0) {
                                Pair(entry.ep!!.width, entry.ep!!.height)
                            } else {
                                Pair(entry.ep!!.height, entry.ep!!.width)
                            }
                        }
                        model.cover.value = entry.cover
                        model.aid.value = entry.aid
                    }
                    baseBind.cv.setOnClickListener {
                        val intent = Intent(context, OrdinaryPlayActivity::class.java).apply {
                            putExtra(OrdinaryPlayActivity.EXTRA_AID, entry.aid)
                            putExtra(OrdinaryPlayActivity.EXTRA_FAST_LOAD_COVER_URL, entry.cover)
                        }
                        context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, baseBind.ivCover, "cover").toBundle())
                    }
                }
            }
        }
    }
}