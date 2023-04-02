package com.empty.yourapp;

public class LinkModal {

    private String urlName;
    private String urlLink;
    //private Bitmap urlIcon;

    public LinkModal(String urlName, String urlLink) {
        this.urlName = urlName;
        this.urlLink = urlLink;
        //this.urlIcon = urlIcon;
    }

    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public void setUrlLink(String urlLink) {
        this.urlLink = urlLink;
    }

//    public Bitmap getUrlIcon() {
//        return urlIcon;
//    }
//
//    public void setUrlIcon(Bitmap urlIcon) {
//        this.urlIcon = urlIcon;
//    }
}

