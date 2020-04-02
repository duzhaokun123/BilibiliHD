package com.duzhaokun123.bilibilihd.mybilibiliapi.medialist.model;

import java.util.List;

public class Infos {
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


    private int code;
    private String  message;
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public class Data {
        public int getAttr() {
            return attr;
        }

        public void setAttr(int attr) {
            this.attr = attr;
        }

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

        public CntInfo getCnt_info() {
            return cnt_info;
        }

        public void setCnt_info(CntInfo cnt_info) {
            this.cnt_info = cnt_info;
        }

        public Coin getCoin() {
            return coin;
        }

        public void setCoin(Coin coin) {
            this.coin = coin;
        }

        public int getCopyright() {
            return copyright;
        }

        public void setCopyright(int copyright) {
            this.copyright = copyright;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public long getCtime() {
            return ctime;
        }

        public void setCtime(long ctime) {
            this.ctime = ctime;
        }

        public Dimension getDimension() {
            return dimension;
        }

        public void setDimension(Dimension dimension) {
            this.dimension = dimension;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getElec_open() {
            return elec_open;
        }

        public void setElec_open(int elec_open) {
            this.elec_open = elec_open;
        }

        public int getFav_state() {
            return fav_state;
        }

        public void setFav_state(int fav_state) {
            this.fav_state = fav_state;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public int getLike_state() {
            return like_state;
        }

        public void setLike_state(int like_state) {
            this.like_state = like_state;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public List<Pages> getPages() {
            return pages;
        }

        public void setPages(List<Pages> pages) {
            this.pages = pages;
        }

        public long getPubtime() {
            return pubtime;
        }

        public void setPubtime(long pubtime) {
            this.pubtime = pubtime;
        }

        public Rights getRights() {
            return rights;
        }

        public void setRights(Rights rights) {
            this.rights = rights;
        }

        public String getShort_link() {
            return short_link;
        }

        public void setShort_link(String short_link) {
            this.short_link = short_link;
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public Upper getUpper() {
            return upper;
        }

        public void setUpper(Upper upper) {
            this.upper = upper;
        }

        private int attr;
        private String bv_id;
        private String bvid;
        private CntInfo cnt_info;
        private Coin coin;
        private int copyright;
        private String cover;
        private long ctime;
        private Dimension dimension;
        private int duration;
        private int elec_open;
        private int fav_state;
        private long id;
        private String intro;
        private int like_state;
        private String link;
        private int page;
        private List<Pages> pages;
        private long pubtime;
        private Rights rights;
        private String short_link;
        private int tid;
        private String title;
        private int type;
        private Upper upper;

        public class CntInfo {
            public int getCoin() {
                return coin;
            }

            public void setCoin(int coin) {
                this.coin = coin;
            }

            public int getCollect() {
                return collect;
            }

            public void setCollect(int collect) {
                this.collect = collect;
            }

            public int getDanmaku() {
                return danmaku;
            }

            public void setDanmaku(int danmaku) {
                this.danmaku = danmaku;
            }

            public int getPlay() {
                return play;
            }

            public void setPlay(int play) {
                this.play = play;
            }

            public int getReply() {
                return reply;
            }

            public void setReply(int reply) {
                this.reply = reply;
            }

            public int getShare() {
                return share;
            }

            public void setShare(int share) {
                this.share = share;
            }

            public int getThumb_down() {
                return thumb_down;
            }

            public void setThumb_down(int thumb_down) {
                this.thumb_down = thumb_down;
            }

            public int getThumb_up() {
                return thumb_up;
            }

            public void setThumb_up(int thumb_up) {
                this.thumb_up = thumb_up;
            }

            private int coin;
            private int collect;
            private int danmaku;
            private int play;
            private int reply;
            private int share;
            private int thumb_down;
            private int thumb_up;
        }

        public class Coin {
            public int getCoin_number() {
                return coin_number;
            }

            public void setCoin_number(int coin_number) {
                this.coin_number = coin_number;
            }

            public int getMax_num() {
                return max_num;
            }

            public void setMax_num(int max_num) {
                this.max_num = max_num;
            }

            private int coin_number;
            private int max_num;
        }

        public class Dimension {
            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getRotate() {
                return rotate;
            }

            public void setRotate(int rotate) {
                this.rotate = rotate;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            private int height;
            private int rotate;
            private int width;
        }

        public class Pages {
            public Dimension getDimension() {
                return dimension;
            }

            public void setDimension(Dimension dimension) {
                this.dimension = dimension;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public long getId() {
                return id;
            }

            public void setId(long id) {
                this.id = id;
            }

            public List<Mates> getMates() {
                return mates;
            }

            public void setMates(List<Mates> mates) {
                this.mates = mates;
            }

            public int getPage() {
                return page;
            }

            public void setPage(int page) {
                this.page = page;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            private Dimension dimension;
            private int duration;
            private String from;
            private long id;
            private List<Mates> mates;
            private int page;
            private String title;

            public class Mates {
                public int getQuality() {
                    return quality;
                }

                public void setQuality(int quality) {
                    this.quality = quality;
                }

                public int getSize() {
                    return size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                private int quality;
                private int size;
            }
        }

        public class Rights {
            public int getAutoplay() {
                return autoplay;
            }

            public void setAutoplay(int autoplay) {
                this.autoplay = autoplay;
            }

            public int getBp() {
                return bp;
            }

            public void setBp(int bp) {
                this.bp = bp;
            }

            public int getDownload() {
                return download;
            }

            public void setDownload(int download) {
                this.download = download;
            }

            public int getElec() {
                return elec;
            }

            public void setElec(int elec) {
                this.elec = elec;
            }

            public int getHd5() {
                return hd5;
            }

            public void setHd5(int hd5) {
                this.hd5 = hd5;
            }

            public int getMovie() {
                return movie;
            }

            public void setMovie(int movie) {
                this.movie = movie;
            }

            public int getNo_background() {
                return no_background;
            }

            public void setNo_background(int no_background) {
                this.no_background = no_background;
            }

            public int getNo_reprint() {
                return no_reprint;
            }

            public void setNo_reprint(int no_reprint) {
                this.no_reprint = no_reprint;
            }

            public int getPay() {
                return pay;
            }

            public void setPay(int pay) {
                this.pay = pay;
            }

            public int getUgc_pay() {
                return ugc_pay;
            }

            public void setUgc_pay(int ugc_pay) {
                this.ugc_pay = ugc_pay;
            }

            private int autoplay;
            private int bp;
            private int download;
            private int elec;
            private int hd5;
            private int movie;
            private int no_background;
            private int no_reprint;
            private int pay;
            private int ugc_pay;
        }

        public class Upper {
            public String getFace() {
                return face;
            }

            public void setFace(String face) {
                this.face = face;
            }

            public int getFollowed() {
                return followed;
            }

            public void setFollowed(int followed) {
                this.followed = followed;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public long getVip_due_date() {
                return vip_due_date;
            }

            public void setVip_due_date(long vip_due_date) {
                this.vip_due_date = vip_due_date;
            }

            public int getVip_pay_type() {
                return vip_pay_type;
            }

            public void setVip_pay_type(int vip_pay_type) {
                this.vip_pay_type = vip_pay_type;
            }

            public int getVip_statue() {
                return vip_statue;
            }

            public void setVip_statue(int vip_statue) {
                this.vip_statue = vip_statue;
            }

            public int getVip_type() {
                return vip_type;
            }

            public void setVip_type(int vip_type) {
                this.vip_type = vip_type;
            }

            private String face;
            private int followed;
            private String name;
            private long vip_due_date;
            private int vip_pay_type;
            private int vip_statue;
            private int vip_type;
        }
    }
}
