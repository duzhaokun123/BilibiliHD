package com.duzhaokun123.bilibilihd.utils

import com.hiczp.bilibili.api.BilibiliClientProperties

class CustomBilibiliClientProperties : BilibiliClientProperties {
    private val mDefaultUserAgent = Settings.bilibiliApi.defaultUserAgent.notEmptyOrNull() ?: super.defaultUserAgent
    private val mAppKey = Settings.bilibiliApi.appKey.notEmptyOrNull() ?: super.appKey
    private val mAppSecret = Settings.bilibiliApi.appSecret.notEmptyOrNull() ?: super.appSecret
    private val mPlatform = Settings.bilibiliApi.platform.notEmptyOrNull() ?: super.platform
    private val mChannel = Settings.bilibiliApi.channel.notEmptyOrNull() ?: super.channel
    private val mHardwareId = Settings.bilibiliApi.hardwareId.notEmptyOrNull() ?: super.hardwareId
    private val mVersion = Settings.bilibiliApi.version.notEmptyOrNull() ?: super.version
    private val mBuild = Settings.bilibiliApi.build.notEmptyOrNull() ?: super.build
    private val mBuildVersionId = Settings.bilibiliApi.buildVersionId.notEmptyOrNull() ?: super.buildVersionId

    override val defaultUserAgent: String
        get() = mDefaultUserAgent
    override val appKey: String
        get() = mAppKey
    override val appSecret: String
        get() = mAppSecret
    override val platform: String
        get() = mPlatform
    override val channel: String
        get() = mChannel
    override val hardwareId: String
        get() = mHardwareId
    override val version: String
        get() = mVersion
    override val build: String
        get() = mBuild
    override val buildVersionId: String
        get() = mBuildVersionId
}