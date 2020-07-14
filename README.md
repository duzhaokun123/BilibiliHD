# Bilibili HD
非官方哔哩哔哩客户端

主要是为了解决官方客户端无法横屏和在大屏幕设备上的表现不好的问题

~~下载 demo https://pan.baidu.com/s/1usDwGbrNi9zAZ1AWU8G7dQ 密码: v9oa~~

下载 demo https://github.com/duzhaokun123/BilibiliHD/releases

不再打包32位原生库, 有需求请自行编译

建议配合[web端哔哩哔哩](https://www.bilibili.com)使用

暂且不建议屏幕较小或没有触摸屏的用户使用

# B站迟早要完

### 将支持的功能
- [x] 多用户登录 (不完全, 无法通过验证, 不总是影响登录, 这因该是`bilibli-api`的问题, 多用户登录理论上实现)
- [x] 横屏
- [ ] 用户信息页
    - [ ] 主页
    - [ ] 动态
    - [ ] 投稿 (?)
    - [x] 收藏
- [x] 首页推荐
- [x] 动态 (WebView)
- [x] 专栏 (WebView)
- [ ] 视频
    - [ ] 播放
        - [x] 能播出来
        - [x] 控制清晰度
        - [x] 播放器控制换p
        - [ ] 拖动时预览
        - [ ] 倍速
    - [ ] 弹幕
        - [x] 显示普通弹幕
        - [x] 显示高级弹幕 (可能有部分无法显示)
        - [ ] 发送 (可能永远无法实现)
        - [ ] 配置 (部分)
        - [ ] 历史弹幕
    - [ ] 评论
        - [ ] 查看
        - [ ] 评论
        - [ ] 发布
    - [x] 稍后再看
    - [ ] 三连相关
- [x] 直播 (WebView, 移动ua)
- [ ] 私信
- [x] 视频下载 (仅获取链接)
- [x] 视频封面下载, 用户头像下载
- [ ] 移动网络警告
- [x] 添加历史记录
- [ ] 广告
    - [ ] 普通广告
        - [ ] 显示 (部分)
        - [ ] 屏蔽
    - [x] 开屏广告
        - [x] 显示
        - [x] 屏蔽
- [ ] 搜索
- [ ] 键盘鼠标友好
- [ ] 关注列表
- [ ] 全站广播

### 将***不***支持的功能
- 注册
- 直播弹幕发送
- ~~开屏广告~~
- 会员购相关
- 修改用户信息
- 游戏相关
- 互动视频
- 兼容电视和 Wear OS
- 高级弹幕发送
- 付费视频

明显还有为提及的，未提及的基本不会支持

### 已知 BUG
- 任何快速操作都有肯能导致崩溃
- 在大屏幕设备上容易崩溃
- 试图播放某些视频时崩溃, 因为服务器返回与期望不同 (也可能不是, 它就是要崩)
- 至少在 2020年4月4日 首页无法加载封面 (返回`404`)
- 暗色模式下不会在标题栏显示视频标题
- 弹幕有时不显示
- 打开空的收藏夹可能崩溃
- 在 x86 设备上更容易崩溃
- 再次查看视频详情时, 推荐视频的封面不会被加载
- 弹幕仅加载前六分钟
- 如果视频未正常播放, 返回***可能***会导致应用无响应
- 有时后台播放通知不会自动清除
- 视频评论无法加载更多
- 更多 BUG 等你发现

### TODO
- 提高代码可读性
- 解决代码重复的问题
- ~~DanmakuFlameMaster 64位原生库~~ 因为 https://github.com/bilibili/DanmakuFlameMaster/blob/e2846461a09e33720a049f628f09c653f55531f0/DanmakuFlameMaster/src/main/java/tv/cjump/jni/NativeBitmapFactory.java#L38
在 API >= 23 的设备上没有必要
- 重作界面

### 从源代码构建注意
```shell script
git clone https://github.com/duzhaokun123/BilibiliHD.git
git clone https://github.com/duzhaokun123/bilibili-api.git
git clone https://github.com/duzhaokun123/DanmakuFlameMaster.git
```

在`BilibiliHD/settings.gradle`中

```groovy
//...
includeBuild '../../Kotlin/bilibili-api'
includeBuild '../DanmakuFlameMaster'
```

改为

```groovy
//...
includeBuild 'path/to/bilibili-api'
includeBuild 'path/to/DanmakuFlameMaster'
```

### 鸣谢
[AOSP](https://source.android.com)

[AboutLibraries](https://mikepenz.github.io/AboutLibraries/)

[BilibiliAPIDocs](https://github.com/fython/BilibiliAPIDocs)

[CircleImageView](https://github.com/hdodenhof/CircleImageView)

[DanmakuFlameMaster](https://github.com/bilibili/DanmakuFlameMaster)

[ExoPlayer](https://exoplayer.dev/)

[Material](https://material.io)

[PhotoView](https://github.com/chrisbanes/PhotoView)

[XRecyclerView](https://github.com/XRecyclerView/XRecyclerView)

[bilibili-API-collect](https://github.com/SocialSisterYi/bilibili-API-collect)

[bilibili-api](https://github.com/czp3009/bilibili-api)

[geetest-validator](https://github.com/kuresaru/geetest-validator)

[glide](https://bumptech.github.io/glide/)

[gson](https://github.com/google/gson)

[okttp](https://square.github.io/okhttp/)
