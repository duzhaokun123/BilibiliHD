package com.duzhaokun123.bilibilihd.mybilibiliapi.history.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class History {
    private int code;
    private String message;
    private int ttl;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private Data data;

    public class Data {
        private List<Tab> tab;
        private List<List2> list;

        public List<Tab> getTab() {
            return tab;
        }

        public void setTab(List<Tab> tab) {
            this.tab = tab;
        }

        public List<List2> getList() {
            return list;
        }

        public void setList(List<List2> list) {
            this.list = list;
        }

        public Cursor getCursor() {
            return cursor;
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        private Cursor cursor;

        public class Tab{
            public String getBusiness() {
                return business;
            }

            public void setBusiness(String business) {
                this.business = business;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            private String business;
            private String name;
        }

        public class List2 {
            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getCover() {
                return cover;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getUri() {
                return uri;
            }

            public void setUri(String uri) {
                this.uri = uri;
            }

            public History2 getHistory() {
                return history;
            }

            public void setHistory(History2 history) {
                this.history = history;
            }

            public int getVideos() {
                return videos;
            }

            public void setVideos(int videos) {
                this.videos = videos;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public long getMid() {
                return mid;
            }

            public void setMid(long mid) {
                this.mid = mid;
            }

            public long getView_at() {
                return view_at;
            }

            public void setView_at(long view_at) {
                this.view_at = view_at;
            }

            public int getProgress() {
                return progress;
            }

            public void setProgress(int progress) {
                this.progress = progress;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getDisplay_attention() {
                return display_attention;
            }

            public void setDisplay_attention(int display_attention) {
                this.display_attention = display_attention;
            }

            private String title;
            private String cover;
            private String uri;
            private History2 history;
            private int videos;
            private String name;
            private long mid;
            @SerializedName("goto")
            private String goto_;
            private long view_at;
            private int progress;
            private int duration;
            private int display_attention;

            public String getGoto_() {
                return goto_;
            }

            public void setGoto_(String goto_) {
                this.goto_ = goto_;
            }

            public class History2 {
                public long getOid() {
                    return oid;
                }

                public void setOid(long oid) {
                    this.oid = oid;
                }

                public int getTp() {
                    return tp;
                }

                public void setTp(int tp) {
                    this.tp = tp;
                }

                public long getCid() {
                    return cid;
                }

                public void setCid(long cid) {
                    this.cid = cid;
                }

                public int getPage() {
                    return page;
                }

                public void setPage(int page) {
                    this.page = page;
                }

                public String getPart() {
                    return part;
                }

                public void setPart(String part) {
                    this.part = part;
                }

                public String getBusiness() {
                    return business;
                }

                public void setBusiness(String business) {
                    this.business = business;
                }

                private long oid;
                private int tp;
                private long cid;
                private int page;
                private String part;
                private String business;
            }
        }

        public class Cursor {
            public long getMax() {
                return max;
            }

            public void setMax(long max) {
                this.max = max;
            }

            public int getMax_tp() {
                return max_tp;
            }

            public void setMax_tp(int max_tp) {
                this.max_tp = max_tp;
            }

            public int getPs() {
                return ps;
            }

            public void setPs(int ps) {
                this.ps = ps;
            }

            private long max;
            private int max_tp;
            private int ps;
        }
    }
}
