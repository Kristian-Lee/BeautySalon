package com.example.beautysalon;

/**
 * @author Lee
 * @date 2021.3.23  17:34
 * @description
 */
public class ImagesBean {

    private int img_id;
    private int user_id;
    private String title;
    private String excerpt;
    private int width;
    private int height;
    private String description;
    private boolean isAuthorTK;

    public int getImg_id() {
        return img_id;
    }

    public void setImg_id(int img_id) {
        this.img_id = img_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isIsAuthorTK() {
        return isAuthorTK;
    }

    public void setIsAuthorTK(boolean isAuthorTK) {
        this.isAuthorTK = isAuthorTK;
    }
}
