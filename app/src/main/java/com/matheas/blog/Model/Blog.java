package com.matheas.blog.Model;

public class Blog {
    public String title;
    public String desc;
    public String image;
    public String timestamp;
    public String userid;
    public String blogid;

    public Blog() {
    }

    public Blog(String title, String description, String image, String timestamp, String userid) {
        this.title = title;
        this.desc = description;
        this.image = image;
        this.timestamp = timestamp;
        this.userid = userid;
    }

    public String getBlogid() {
        return blogid;
    }

    public void setBlogid(String blogid) {
        this.blogid = blogid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return desc;
    }

    public void setDescription(String description) {
        this.desc = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
