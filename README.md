# Bilibili HD
非官方哔哩哔哩客户端

主要是为了解决官方客户端无法横屏和在大屏幕设备上的表现不好的问题

下载 demo https://pan.baidu.com/s/1usDwGbrNi9zAZ1AWU8G7dQ 密码: v9oa

不再打包32位原生库, 有需求请自行编译

推荐配合 web 哔哩哔哩版使用

### 将支持的功能
- [x] 多用户登录 (不完全, 无法通过验证, 不总是影响登录, 多用户登录理论上实现)
- [x] 横屏 (转屏就崩)
- [ ] 用户信息页 (由于 bilibili-api 的 BUG, 无法获取满级用户的信息, 重写了 Bilibili-api 的部分代码)
    - [ ] 主页
    - [ ] 动态
    - [ ] 投稿 (?)
    - [x] 收藏
- [x] 首页推荐
- [ ] 动态 (bilibili-api 里没有, 自己抓包实现的)
- [x] 专栏 (勉强)
- [ ] 视频播放
    - [x] 能播出来
    - [x] 控制清晰度
    - [x] 播放器控制换p
    - [ ] 拖动时预览
- [ ] 视频弹幕
    - [x] 显示普通弹幕
    - [x] 显示高级弹幕 (可能有部分无法显示)
    - [ ] 发送 (可能永远无法实现)
    - [ ] 配置
    - [ ] 历史弹幕
- [ ] 视频评论
    - [ ] 查看
    - [ ] 评论
    - [ ] 发布
- [ ] 直播 (可能无法实现)
- [ ] 三连相关
- [ ] 私信
- [x] 视频下载 (弹幕下载会乱码, 所以不下弹幕了)
- [x] 视频封面下载, 用户头像下载
- [ ] 移动网络警告
- [ ] 添加历史记录
- [ ] 广告
    - [ ] 显示
    - [ ] 屏蔽
- [ ] 搜索

### 将***不***支持的功能
- 注册
- 直播弹幕发送
- 开屏广告
- 会员购相关
- 修改用户信息
- 游戏相关
- 互动视频
- 兼容电视和 Wear OS
- 高级弹幕发送

明显还有为提及的，未提及的基本不会支持

### 已知 BUG
- 任何快速操作都有肯能导致崩溃
- 在大屏幕设备上容易崩溃
- `Download Manager`模式似乎只在连接了`VPN`时正常工作
- 试图播放某些视频时崩溃, 因为服务器返回与期望不同
- 至少在 2020年4月4日 首页无法加载封面 (返回`404`)
- 如果`PlayActivity`重构, 下面的信息会消失
- 暗色模式下不会在标题栏显示视频标题
- 弹幕有时不显示
- 更多 BUG 等你发现

### TODO
- 提高代码可读性
- 解决代码重复的问题
- DanmakuFlameMaster 64位原生库
- 在支持的设备上使导航栏透明

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

[glide](https://bumptech.github.io/glide/)

[gson](https://github.com/google/gson)

[mobile-ffmpeg](https://tanersener.github.io/mobile-ffmpeg)

[okdownload](https://github.com/lingochamp/okdownload)

[okttp](https://square.github.io/okhttp/)
