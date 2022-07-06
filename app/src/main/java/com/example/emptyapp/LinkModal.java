package com.example.emptyapp;

public class LinkModal {

    private String urlName;
    private String urlLink;

    public LinkModal(String urlName, String urlLink) {
        this.urlName = urlName;
        this.urlLink = urlLink;
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

}

