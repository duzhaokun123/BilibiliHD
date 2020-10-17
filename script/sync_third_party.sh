#!/bin/sh

BILIBILI_API_DIR="third_party/bilibili-api"
DFM_DIR="third_party/DanmakuFlameMaster"

if [ -d $BILIBILI_API_DIR ]; then
  git -C $BILIBILI_API_DIR pull
else
  git clone https://github.com/duzhaokun123/bilibili-api.git $BILIBILI_API_DIR --depth 1
fi

if [ -d $DFM_DIR ]; then
  git -C $DFM_DIR pull
else
  git clone https://github.com/duzhaokun123/DanmakuFlameMaster.git $DFM_DIR --depth 1
fi
