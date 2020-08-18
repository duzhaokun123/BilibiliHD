package com.duzhaokun123.bilibilihd.utils

import android.app.Activity
import android.os.Build
import android.os.Environment
import com.duzhaokun123.bilibilihd.Application
import com.duzhaokun123.bilibilihd.BuildConfig
import com.duzhaokun123.bilibilihd.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter

object Logcat {
    @JvmStatic
    fun saveLog(activity: Activity) {
        Thread {
            try {
                val file = File(activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "" + System.currentTimeMillis() + ".log")
                FileOutputStream(file).use { fileOutputStream ->
                    OutputStreamWriter(fileOutputStream).use { outputStreamWriter ->
                        outputStreamWriter.write("""
                            ========= beginning of info
                            versionName:    ${BuildConfig.VERSION_NAME}
                            versionCode:	${BuildConfig.VERSION_CODE}
                            buildType:		${BuildConfig.BUILD_TYPE}
                            gitVersion:		${BuildConfig.IS_GIT_VERSION}
                            sdkVersion:		${Build.VERSION.SDK_INT}
                            ========= beginning of log
                        """.trimIndent())
                        outputStreamWriter.write("\n")
                        outputStreamWriter.flush()
                        val exec = Runtime.getRuntime().exec("logcat -d")
                        IOUtil.copy(exec.inputStream, fileOutputStream)
                    }
                }
                Application.runOnUiThread { TipUtil.showToast(Application.getInstance().getString(R.string.saved_to_s, file.absolutePath)) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }


}