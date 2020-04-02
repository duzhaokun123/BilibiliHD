package com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model;

import java.util.List;

public class Ids {
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private List<Data> data;
    private String message;

    public class Data {
        public String getBv_id() {
            return bv_id;
        }

        public void setBv_id(String bv_id) {
            this.bv_id = bv_id;
        }

        public String getBvid() {
            return bvid;
        }

        public void setBvid(String bvid) {
            this.bvid = bvid;
        }

        public int getFav_state() {
            return fav_state;
        }

        public void setFav_state(int fav_state) {
            this.fav_state = fav_state;
        }

        public long getFav_time() {
            return fav_time;
        }

        public void setFav_time(long fav_time) {
            this.fav_time = fav_time;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getLike_state() {
            return like_state;
        }

        public void setLike_state(int like_state) {
            this.like_state = like_state;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public String getShort_link() {
            return short_link;
        }

        public void setShort_link(String short_link) {
            this.short_link = short_link;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        private String bv_id;
        private String bvid;
        private int fav_state;
        private long fav_time;
        private long id; //其实就是 aid
        private int like_state;
        private int page;
        private String short_link;
        private int type;
    }
}
