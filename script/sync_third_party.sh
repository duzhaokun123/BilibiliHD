#!/bin/sh

BILIBILI_API_DIR="third_party/bilibili-api"

if [ -d $BILIBILI_API_DIR ]; then
  git -C $BILIBILI_API_DIR pull
else
  git clone https://github.com/duzhaokun123/bilibili-api.git $BILIBILI_API_DIR --depth 1
fi
