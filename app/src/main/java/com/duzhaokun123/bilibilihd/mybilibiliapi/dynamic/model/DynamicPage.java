package com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model;

import java.util.List;

public class DynamicPage {
    private int code;
    private String msg;
    private int ttl;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    private Data data;

    public int getTtl() {
        return ttl;
    }

    public void setTtl(int ttl) {
        this.ttl = ttl;
    }

    public class Data {
        private List<Card> cards;
        private Attention attention;

        public List<Card> getCards() {
            return cards;
        }

        public void setCards(List<Card> cards) {
            this.cards = cards;
        }

        public Attention getAttention() {
            return attention;
        }

        public void setAttention(Attention attention) {
            this.attention = attention;
        }


        public class Card {
            private Desc desc;
            private String card;
            private String extend_json;
            private Extra extra;

            public Desc getDesc() {
                return desc;
            }

            public void setDesc(Desc desc) {
                this.desc = desc;
            }

            public String getCard() {
                return card;
            }

            public void setCard(String card) {
                this.card = card;
            }

            public String getExtend_json() {
                return extend_json;
            }

            public void setExtend_json(String extend_json) {
                this.extend_json = extend_json;
            }

            public Extra getExtra() {
                return extra;
            }

            public void setExtra(Extra extra) {
                this.extra = extra;
            }

            public Display getDisplay() {
                return display;
            }

            public void setDisplay(Display display) {
                this.display = display;
            }

            private Display display;

            public class Desc {
                private long uid;
                private int type;
                private long rid;
                private int acl;
                private long view;
                private int repost;
                private int comment;
                private long like;
                private int is_liked;
                private long dynamic_id;
                private long timestamp;
                private long pre_by_id;
                private long orig_by_id;
                private long orig_type;

                public long getUid() {
                    return uid;
                }

                public void setUid(long uid) {
                    this.uid = uid;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                public long getRid() {
                    return rid;
                }

                public void setRid(long rid) {
                    this.rid = rid;
                }

                public int getAcl() {
                    return acl;
                }

                public void setAcl(int acl) {
                    this.acl = acl;
                }

                public long getView() {
                    return view;
                }

                public void setView(long view) {
                    this.view = view;
                }

                public int getRepost() {
                    return repost;
                }

                public void setRepost(int repost) {
                    this.repost = repost;
                }

                public int getComment() {
                    return comment;
                }

                public void setComment(int comment) {
                    this.comment = comment;
                }

                public long getLike() {
                    return like;
                }

                public void setLike(long like) {
                    this.like = like;
                }

                public int getIs_liked() {
                    return is_liked;
                }

                public void setIs_liked(int is_liked) {
                    this.is_liked = is_liked;
                }

                public long getDynamic_id() {
                    return dynamic_id;
                }

                public void setDynamic_id(long dynamic_id) {
                    this.dynamic_id = dynamic_id;
                }

                public long getTimestamp() {
                    return timestamp;
                }

                public void setTimestamp(long timestamp) {
                    this.timestamp = timestamp;
                }

                public long getPre_by_id() {
                    return pre_by_id;
                }

                public void setPre_by_id(long pre_by_id) {
                    this.pre_by_id = pre_by_id;
                }

                public long getOrig_by_id() {
                    return orig_by_id;
                }

                public void setOrig_by_id(long orig_by_id) {
                    this.orig_by_id = orig_by_id;
                }

                public long getOrig_type() {
                    return orig_type;
                }

                public void setOrig_type(long orig_type) {
                    this.orig_type = orig_type;
                }

                public UserProfile getUser_profile() {
                    return user_profile;
                }

                public void setUser_profile(UserProfile user_profile) {
                    this.user_profile = user_profile;
                }

                private UserProfile user_profile;

                public class UserProfile{
                    private Info info;
                    private Card2 card;
                    private Vip vip;
                    private Pendant pendant;
                    private String  rank;
                    private String sign;
                    private LevelInfo level_info;
                    private int uid_type;
                    private int stype;
                    private int r_type;
                    private int status;
                    private String dynamic_id_str;
                    private String pre_dy_id_str;
                    private String orig_by_id_str;
                    private String rid_str;

                    public Info getInfo() {
                        return info;
                    }

                    public void setInfo(Info info) {
                        this.info = info;
                    }

                    public Card2 getCard() {
                        return card;
                    }

                    public void setCard(Card2 card) {
                        this.card = card;
                    }

                    public Vip getVip() {
                        return vip;
                    }

                    public void setVip(Vip vip) {
                        this.vip = vip;
                    }

                    public Pendant getPendant() {
                        return pendant;
                    }

                    public void setPendant(Pendant pendant) {
                        this.pendant = pendant;
                    }

                    public String getRank() {
                        return rank;
                    }

                    public void setRank(String rank) {
                        this.rank = rank;
                    }

                    public String getSign() {
                        return sign;
                    }

                    public void setSign(String sign) {
                        this.sign = sign;
                    }

                    public LevelInfo getLevel_info() {
                        return level_info;
                    }

                    public void setLevel_info(LevelInfo level_info) {
                        this.level_info = level_info;
                    }

                    public int getUid_type() {
                        return uid_type;
                    }

                    public void setUid_type(int uid_type) {
                        this.uid_type = uid_type;
                    }

                    public int getStype() {
                        return stype;
                    }

                    public void setStype(int stype) {
                        this.stype = stype;
                    }

                    public int getR_type() {
                        return r_type;
                    }

                    public void setR_type(int r_type) {
                        this.r_type = r_type;
                    }

                    public int getStatus() {
                        return status;
                    }

                    public void setStatus(int status) {
                        this.status = status;
                    }

                    public String getDynamic_id_str() {
                        return dynamic_id_str;
                    }

                    public void setDynamic_id_str(String dynamic_id_str) {
                        this.dynamic_id_str = dynamic_id_str;
                    }

                    public String getPre_dy_id_str() {
                        return pre_dy_id_str;
                    }

                    public void setPre_dy_id_str(String pre_dy_id_str) {
                        this.pre_dy_id_str = pre_dy_id_str;
                    }

                    public String getOrig_by_id_str() {
                        return orig_by_id_str;
                    }

                    public void setOrig_by_id_str(String orig_by_id_str) {
                        this.orig_by_id_str = orig_by_id_str;
                    }

                    public String getRid_str() {
                        return rid_str;
                    }

                    public void setRid_str(String rid_str) {
                        this.rid_str = rid_str;
                    }

                    public Origin getOrigin() {
                        return origin;
                    }

                    public void setOrigin(Origin origin) {
                        this.origin = origin;
                    }

                    private Origin origin;

                    public class Info {
                        private long uid;
                        private String uname;

                        public long getUid() {
                            return uid;
                        }

                        public void setUid(long uid) {
                            this.uid = uid;
                        }

                        public String getUname() {
                            return uname;
                        }

                        public void setUname(String uname) {
                            this.uname = uname;
                        }

                        public String getFace() {
                            return face;
                        }

                        public void setFace(String face) {
                            this.face = face;
                        }

                        private String face;
                    }

                    public class Card2 {
                        private OfficeVerify office_verify;

                        public OfficeVerify getOffice_verify() {
                            return office_verify;
                        }

                        public void setOffice_verify(OfficeVerify office_verify) {
                            this.office_verify = office_verify;
                        }

                        public class OfficeVerify {
                            private int type;
                            private String desc;

                            public int getType() {
                                return type;
                            }

                            public void setType(int type) {
                                this.type = type;
                            }

                            public String getDesc() {
                                return desc;
                            }

                            public void setDesc(String desc) {
                                this.desc = desc;
                            }
                        }
                    }

                    public class Vip {
                        public int getVipType() {
                            return vipType;
                        }

                        public void setVipType(int vipType) {
                            this.vipType = vipType;
                        }

                        public long getVipDueDate() {
                            return vipDueDate;
                        }

                        public void setVipDueDate(long vipDueDate) {
                            this.vipDueDate = vipDueDate;
                        }

                        public String getDueRemark() {
                            return dueRemark;
                        }

                        public void setDueRemark(String dueRemark) {
                            this.dueRemark = dueRemark;
                        }

                        public int getAccessStatus() {
                            return accessStatus;
                        }

                        public void setAccessStatus(int accessStatus) {
                            this.accessStatus = accessStatus;
                        }

                        public int getVipStatus() {
                            return vipStatus;
                        }

                        public void setVipStatus(int vipStatus) {
                            this.vipStatus = vipStatus;
                        }

                        public String getVipStatusWarn() {
                            return vipStatusWarn;
                        }

                        public void setVipStatusWarn(String vipStatusWarn) {
                            this.vipStatusWarn = vipStatusWarn;
                        }

                        public int getThemeType() {
                            return themeType;
                        }

                        public void setThemeType(int themeType) {
                            this.themeType = themeType;
                        }

                        public Label getLabel() {
                            return label;
                        }

                        public void setLabel(Label label) {
                            this.label = label;
                        }

                        private int vipType;
                        private long vipDueDate;
                        private String dueRemark;
                        private int accessStatus;
                        private int vipStatus;
                        private String vipStatusWarn;
                        private int themeType;
                        private Label label;

                        public class Label {
                            private String path;

                            public String getPath() {
                                return path;
                            }

                            public void setPath(String path) {
                                this.path = path;
                            }
                        }
                    }

                    public class Pendant {
                        private int pid;
                        private String name;
                        private String image;

                        public int getPid() {
                            return pid;
                        }

                        public void setPid(int pid) {
                            this.pid = pid;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }

                        public String getImage() {
                            return image;
                        }

                        public void setImage(String image) {
                            this.image = image;
                        }

                        public int getExpire() {
                            return expire;
                        }

                        public void setExpire(int expire) {
                            this.expire = expire;
                        }

                        private int expire;
                    }

                    public class LevelInfo {
                        private int current_level;
                        private int current_min;
                        private int current_exp;

                        public int getCurrent_level() {
                            return current_level;
                        }

                        public void setCurrent_level(int current_level) {
                            this.current_level = current_level;
                        }

                        public int getCurrent_min() {
                            return current_min;
                        }

                        public void setCurrent_min(int current_min) {
                            this.current_min = current_min;
                        }

                        public int getCurrent_exp() {
                            return current_exp;
                        }

                        public void setCurrent_exp(int current_exp) {
                            this.current_exp = current_exp;
                        }

                        public String getNext_exp() {
                            return next_exp;
                        }

                        public void setNext_exp(String next_exp) {
                            this.next_exp = next_exp;
                        }

                        private String next_exp;
                    }

                    public class Origin {
                        private long uid;
                        private int type;
                        private long rid;
                        private int acl;
                        private int view;
                        private int repost;
                        private int like;
                        private long dynamic_id;
                        private long timestamp;
                        private int pre_by_id;
                        private int orig_by_id;
                        private int uid_type;
                        private int stype;
                        private int inner_id;
                        private int status;
                        private String dynamic_id_str;
                        private String pre_by_id_str;
                        private String orig_by_id_str;

                        public long getUid() {
                            return uid;
                        }

                        public void setUid(long uid) {
                            this.uid = uid;
                        }

                        public int getType() {
                            return type;
                        }

                        public void setType(int type) {
                            this.type = type;
                        }

                        public long getRid() {
                            return rid;
                        }

                        public void setRid(long rid) {
                            this.rid = rid;
                        }

                        public int getAcl() {
                            return acl;
                        }

                        public void setAcl(int acl) {
                            this.acl = acl;
                        }

                        public int getView() {
                            return view;
                        }

                        public void setView(int view) {
                            this.view = view;
                        }

                        public int getRepost() {
                            return repost;
                        }

                        public void setRepost(int repost) {
                            this.repost = repost;
                        }

                        public int getLike() {
                            return like;
                        }

                        public void setLike(int like) {
                            this.like = like;
                        }

                        public long getDynamic_id() {
                            return dynamic_id;
                        }

                        public void setDynamic_id(long dynamic_id) {
                            this.dynamic_id = dynamic_id;
                        }

                        public long getTimestamp() {
                            return timestamp;
                        }

                        public void setTimestamp(long timestamp) {
                            this.timestamp = timestamp;
                        }

                        public int getPre_by_id() {
                            return pre_by_id;
                        }

                        public void setPre_by_id(int pre_by_id) {
                            this.pre_by_id = pre_by_id;
                        }

                        public int getOrig_by_id() {
                            return orig_by_id;
                        }

                        public void setOrig_by_id(int orig_by_id) {
                            this.orig_by_id = orig_by_id;
                        }

                        public int getUid_type() {
                            return uid_type;
                        }

                        public void setUid_type(int uid_type) {
                            this.uid_type = uid_type;
                        }

                        public int getStype() {
                            return stype;
                        }

                        public void setStype(int stype) {
                            this.stype = stype;
                        }

                        public int getInner_id() {
                            return inner_id;
                        }

                        public void setInner_id(int inner_id) {
                            this.inner_id = inner_id;
                        }

                        public int getStatus() {
                            return status;
                        }

                        public void setStatus(int status) {
                            this.status = status;
                        }

                        public String getDynamic_id_str() {
                            return dynamic_id_str;
                        }

                        public void setDynamic_id_str(String dynamic_id_str) {
                            this.dynamic_id_str = dynamic_id_str;
                        }

                        public String getPre_by_id_str() {
                            return pre_by_id_str;
                        }

                        public void setPre_by_id_str(String pre_by_id_str) {
                            this.pre_by_id_str = pre_by_id_str;
                        }

                        public String getOrig_by_id_str() {
                            return orig_by_id_str;
                        }

                        public void setOrig_by_id_str(String orig_by_id_str) {
                            this.orig_by_id_str = orig_by_id_str;
                        }

                        public String getRid_str() {
                            return rid_str;
                        }

                        public void setRid_str(String rid_str) {
                            this.rid_str = rid_str;
                        }

                        private String rid_str;
                    }
                }
            }

            public class Extra {
                private String biz_extra;

                public String getBiz_extra() {
                    return biz_extra;
                }

                public void setBiz_extra(String biz_extra) {
                    this.biz_extra = biz_extra;
                }
            }

            public class Display {
                private Origin origin;

                public Origin getOrigin() {
                    return origin;
                }

                public void setOrigin(Origin origin) {
                    this.origin = origin;
                }

                public class Origin {
                    private TopicInfo topic_info;

                    public TopicInfo getTopic_info() {
                        return topic_info;
                    }

                    public void setTopic_info(TopicInfo topic_info) {
                        this.topic_info = topic_info;
                    }

                    public class TopicInfo {
                        private List<TopicId> topic_details;

                        public List<TopicId> getTopic_details() {
                            return topic_details;
                        }

                        public void setTopic_details(List<TopicId> topic_details) {
                            this.topic_details = topic_details;
                        }

                        public class TopicId{
                            private long topic_id;
                            private String topic_name;
                            private int is_activity;

                            public long getTopic_id() {
                                return topic_id;
                            }

                            public void setTopic_id(long topic_id) {
                                this.topic_id = topic_id;
                            }

                            public String getTopic_name() {
                                return topic_name;
                            }

                            public void setTopic_name(String topic_name) {
                                this.topic_name = topic_name;
                            }

                            public int getIs_activity() {
                                return is_activity;
                            }

                            public void setIs_activity(int is_activity) {
                                this.is_activity = is_activity;
                            }

                            public String getTopic_link() {
                                return topic_link;
                            }

                            public void setTopic_link(String topic_link) {
                                this.topic_link = topic_link;
                            }

                            private String topic_link;
                        }
                    }
                }
            }
        }

        public class Attention {
            private List<Long> uids;

            public List<Long> getUids() {
                return uids;
            }

            public void setUids(List<Long> uids) {
                this.uids = uids;
            }

            public List<Bangumis> getBangumis() {
                return bangumis;
            }

            public void setBangumis(List<Bangumis> bangumis) {
                this.bangumis = bangumis;
            }

            public long getMax_dynamic_id() {
                return max_dynamic_id;
            }

            public void setMax_dynamic_id(long max_dynamic_id) {
                this.max_dynamic_id = max_dynamic_id;
            }

            public long getHistory_offset() {
                return history_offset;
            }

            public void setHistory_offset(long history_offset) {
                this.history_offset = history_offset;
            }

            public int get_gt_() {
                return _gt_;
            }

            public void set_gt_(int _gt_) {
                this._gt_ = _gt_;
            }

            private List<Bangumis> bangumis;
            private long max_dynamic_id;
            private long history_offset;
            private int _gt_;

            public class Bangumis {
                public int getSeason_id() {
                    return season_id;
                }

                public void setSeason_id(int season_id) {
                    this.season_id = season_id;
                }

                public int getType() {
                    return type;
                }

                public void setType(int type) {
                    this.type = type;
                }

                private int season_id;
                private int type;
            }
        }
    }
}
