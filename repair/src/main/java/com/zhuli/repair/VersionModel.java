package com.zhuli.repair;

public class VersionModel {

    private String version;
    private int type;
    private String url;
    private String content;

    public int getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
