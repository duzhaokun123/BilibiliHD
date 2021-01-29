package com.duzhaokun123.bilibilihd.utils

import android.os.Build
import com.duzhaokun123.bilibilihd.BuildConfig
import java.io.*

object Logcat {
    fun saveLog(outputStream: OutputStream) {
        outputStream.writer().use { outputStreamWriter ->
            outputStreamWriter.write("""
                            ========= beginning of info
                            versionName:    ${BuildConfig.VERSION_NAME}
                            versionCode:	${BuildConfig.VERSION_CODE}
                            buildType:		${BuildConfig.BUILD_TYPE}
                            buildTime:      ${BuildConfig.BUILD_TIME}
                            gitVersion:		${BuildConfig.IS_GIT_VERSION}
                            sdkVersion:		${Build.VERSION.SDK_INT}
                            ========= beginning of log
                        """.trimIndent())
            outputStreamWriter.write("\n")
            outputStreamWriter.flush()
            Runtime.getRuntime().exec("logcat -d").inputStream.use {
                it.copyTo(outputStream)
            }
        }
    }
}