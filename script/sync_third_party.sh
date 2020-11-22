#!/bin/sh

BILIBILI_API_DIR="third_party/bilibili-api"
DANMAKU_VIEW_DIR="third_party/DanmakuView"

if [ -d $BILIBILI_API_DIR ]; then
  git -C $BILIBILI_API_DIR pull
else
  git clone https://github.com/duzhaokun123/bilibili-api.git $BILIBILI_API_DIR --depth 1
fi

if [ -d DANMAKU_VIEW_DIR ]; then
  git -C DANMAKU_VIEW_DIR pull
else
  git clone https://github.com/duzhaokun123/DanmakuView.git DANMAKU_VIEW_DIR --depth 1
fi
