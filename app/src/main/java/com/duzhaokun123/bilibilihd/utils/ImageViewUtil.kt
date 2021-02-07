package com.duzhaokun123.bilibilihd.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.Application.runOnUiThread
import com.duzhaokun123.bilibilihd.R
import com.duzhaokun123.bilibilihd.databinding.LayoutIvOverlayBinding
import com.github.chrisbanes.photoview.PhotoView
import com.hiczp.bilibili.api.web.model.VideoShot
import com.stfalcon.imageviewer.StfalconImageViewer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.ExecutionException
import kotlin.math.max
import kotlin.math.min

object ImageViewUtil {
    fun autoAspectRation(imageView: ImageView, drawable: Drawable) {
        val params = imageView.layoutParams
        params.height = imageView.width / drawable.intrinsicWidth * drawable.intrinsicHeight + imageView.paddingBottom + imageView.paddingTop
        imageView.layoutParams = params
    }

    fun setLevelDrawable(imageView: ImageView, level: Int) {
        when (level) {
            0 -> imageView.setImageResource(R.drawable.ic_user_level_0)
            1 -> imageView.setImageResource(R.drawable.ic_user_level_1)
            2 -> imageView.setImageResource(R.drawable.ic_user_level_2)
            3 -> imageView.setImageResource(R.drawable.ic_user_level_3)
            4 -> imageView.setImageResource(R.drawable.ic_user_level_4)
            5 -> imageView.setImageResource(R.drawable.ic_user_level_5)
            6 -> imageView.setImageResource(R.drawable.ic_user_level_6)
        }
    }

    fun setSixDrawable(imageView: ImageView, sex: String) {
        when (sex) {
            "男" -> imageView.setImageResource(R.drawable.ic_m)
            "女" -> imageView.setImageResource(R.drawable.ic_f)
            else -> imageView.setImageDrawable(null)
        }
    }

    fun setPreview(imageView: ImageView, videoShot: VideoShot?, index: Int) {
        if (index >= 0 && videoShot != null) {
            Thread {
                try {
                    val urlIndex = index / (videoShot.data.imgXLen * videoShot.data.imgYLen)
                    val source: Bitmap = Glide.with(Application.getInstance()).asBitmap().load("https:" + videoShot.data.image[urlIndex]).submit().get()
                    val startX = videoShot.data.imgXSize * (index % videoShot.data.imgYLen)
                    val startY = videoShot.data.imgYSize * (index / videoShot.data.imgXLen - urlIndex * videoShot.data.imgYLen)
                    val newBitmap = Bitmap.createBitmap(source, startX, startY, videoShot.data.imgXSize, videoShot.data.imgYSize)
                    runOnUiThread {
                        val params = imageView.layoutParams
                        params.width = videoShot.data.imgXSize * 2
                        params.height = videoShot.data.imgYSize * 2
                        imageView.layoutParams = params
                        imageView.setImageBitmap(newBitmap)
                    }
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: IndexOutOfBoundsException) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    fun viewImage(context: Context, pImage: String?, pImageView: ImageView? = null, share: Boolean = true) {
        if (pImage == null) return
        var siv: StfalconImageViewer<*>? = null
        val overlayBinding =
                DataBindingUtil.inflate<LayoutIvOverlayBinding>(LayoutInflater.from(context), R.layout.layout_iv_overlay, null, false)
        overlayBinding.tb.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> {
                    if (share) {
                        GlobalScope.launch(Dispatchers.IO) {
                            val shareUri: Uri
                            val srcFile = Glide.with(context).asFile().load(pImage).submit().get()
                            val shareFile = File(context.cacheDir, "shareImg${File.separatorChar}share.jpeg").apply { parentFile!!.mkdirs() } // FIXME: 20-11-2 你凭什么认为一定是 jpeg 格式
                            srcFile.copyTo(shareFile, overwrite = true)
                            shareUri = FileProvider.getUriForFile(context, "com.duzhaokun123.bilibilihd.fileprovider", shareFile)
                            context.startActivity(Intent.createChooser(Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, shareUri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                setDataAndType(shareUri, context.contentResolver.getType(shareUri))
                            }, context.getText(R.string.share_to)))
                        }
                    } else {
                        TipUtil.showTip(context, R.string.unshareable)
                    }
                    true
                }
                R.id.download -> {
                    DownloadUtil.downloadPicture(context, pImage)
                    true
                }
                R.id.close -> {
                    siv?.close()
                    true
                }
                else -> false
            }
        }
        StfalconImageViewer.Builder(context, arrayOf(pImage)) { imageView, image ->
            if (imageView is PhotoView) {
                Glide.with(context).load(image).addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        TipUtil.showToast(e?.message)
                        return true
                    }

                    override fun onResourceReady(resource: Drawable, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (resource is BitmapDrawable) {
                            Thread {
                                val bitmap = Glide.with(context).asBitmap().load(image).submit().get()
                                val sX = imageView.measuredWidth / bitmap.width.toFloat()
                                val sY = imageView.measuredHeight / bitmap.height.toFloat()
                                val scale = min(sX, sY)
                                kRunOnUiThread {
                                    imageView.apply {
                                        scaleType = ImageView.ScaleType.CENTER
                                        setImageBitmap(bitmap)
                                        maximumScale = max(5F, scale)
                                        mediumScale = (maximumScale + scale) / 2
                                        minimumScale = scale
                                        this@apply.scale = scale
                                    }
                                }
                            }.start()
                        }
                        return false
                    }

                }).into(imageView)
            } else {
                Glide.with(context).load(image).into(imageView)
            }
        }
                .withTransitionFrom(pImageView)
                .withOverlayView(overlayBinding.root)
                .show().also { siv = it }
    }
}
