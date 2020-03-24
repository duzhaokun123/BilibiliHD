package com.duzhaokun123.bilibilihd.mybilibiliapi.dynamic.model;

import java.util.List;

public class NestedCard {
    private Item item;
    private long aid;
    private String title;
    private String pic;
    private String origin;
    private String desc;
    private Dimension dimension;
    private String dynamic;
    private String jump_url;

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    public long getAid() {
        return aid;
    }

    public void setAid(long aid) {
        this.aid = aid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public String getDynamic() {
        return dynamic;
    }

    public void setDynamic(String dynamic) {
        this.dynamic = dynamic;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public class Item {
        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

//        public Role getRole() {
//            return role;
//        }
//
//        public void setRole(Role role) {
//            this.role = role;
//        }

//        public Source getSource() {
//            return source;
//        }
//
//        public void setSource(Source source) {
//            this.source = source;
//        }

        public List<Pictures> getPictures() {
            return pictures;
        }

        public void setPictures(List<Pictures> pictures) {
            this.pictures = pictures;
        }

        public int getPictures_count() {
            return pictures_count;
        }

        public void setPictures_count(int pictures_count) {
            this.pictures_count = pictures_count;
        }

        public String getUpload_time() {
            return upload_time;
        }

        public void setUpload_time(String upload_time) {
            this.upload_time = upload_time;
        }

        public String getAt_control() {
            return at_control;
        }

        public void setAt_control(String at_control) {
            this.at_control = at_control;
        }

        public int getReply() {
            return reply;
        }

        public void setReply(int reply) {
            this.reply = reply;
        }

        public Settings getSettings() {
            return settings;
        }

        public void setSettings(Settings settings) {
            this.settings = settings;
        }

        public int getIs_fav() {
            return is_fav;
        }

        public void setIs_fav(int is_fav) {
            this.is_fav = is_fav;
        }

        public long getRp_id() {
            return rp_id;
        }

        public void setRp_id(long rp_id) {
            this.rp_id = rp_id;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getCtul() {
            return ctul;
        }

        public void setCtul(String ctul) {
            this.ctul = ctul;
        }

        public long getOrig_dy_id() {
            return orig_dy_id;
        }

        public void setOrig_dy_id(long orig_dy_id) {
            this.orig_dy_id = orig_dy_id;
        }

        public long getPre_dy_id() {
            return pre_dy_id;
        }

        public void setPre_dy_id(long pre_dy_id) {
            this.pre_dy_id = pre_dy_id;
        }

        public int getOrig_type() {
            return orig_type;
        }

        public void setOrig_type(int orig_type) {
            this.orig_type = orig_type;
        }

        private long rp_id;
        private long uid;
        private String ctul;
        private long orig_dy_id;
        private long pre_dy_id;
        private int reply;
        private int orig_type;
        private long id;
        private String title;
        private String description;
        private String category;
//        private Role role;
//        private Source source;
        private List<Pictures> pictures;
        private int pictures_count;
        private String upload_time;
        private String at_control;
        private Settings settings;
        private int is_fav;
        private String content;
        private String dynamic;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDynamic() {
            return dynamic;
        }

        public void setDynamic(String dynamic) {
            this.dynamic = dynamic;
        }

        public class Pictures {
            public String getImg_src() {
                return img_src;
            }

            public void setImg_src(String img_src) {
                this.img_src = img_src;
            }

            public int getImg_width() {
                return img_width;
            }

            public void setImg_width(int img_width) {
                this.img_width = img_width;
            }

            public int getImg_height() {
                return img_height;
            }

            public void setImg_height(int img_height) {
                this.img_height = img_height;
            }

            public int getImg_size() {
                return img_size;
            }

            public void setImg_size(int img_size) {
                this.img_size = img_size;
            }

            private String img_src;
            private int img_width;
            private int img_height;
            private int img_size;
        }

//        public class Role {
//
//        }

//        public class Source {
//
//        }

        public class Settings {
            private int copy_forbidden;

            public int getCopy_forbidden() {
                return copy_forbidden;
            }

            public void setCopy_forbidden(int copy_forbidden) {
                this.copy_forbidden = copy_forbidden;
            }
        }
    }

    public class User {
        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getHead_url() {
            return head_url;
        }

        public void setHead_url(String head_url) {
            this.head_url = head_url;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Vip getVip() {
            return vip;
        }

        public void setVip(Vip vip) {
            this.vip = vip;
        }

        private long uid;
        private String head_url;
        private String name;
        private Vip vip;

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
}
