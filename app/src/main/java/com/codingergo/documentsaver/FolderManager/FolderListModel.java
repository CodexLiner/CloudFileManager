package com.codingergo.documentsaver.FolderManager;

public class FolderListModel {
    String name ,id, url ;

    public FolderListModel(String name, String id, String url) {
        this.name = name;
        this.id = id;
        this.url = url;
    }

    public FolderListModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
