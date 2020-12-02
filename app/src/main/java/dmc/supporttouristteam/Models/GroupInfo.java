package dmc.supporttouristteam.Models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupInfo implements Serializable {
    private String id;
    private String name;
    private String image;
    private List<String> chatList;
    private int type; // 1: group 2: couple

    public GroupInfo() {
        this.chatList = new ArrayList<>();
    }

    public GroupInfo(String id, String name, String image, int type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type = type;
        this.chatList = new ArrayList<>();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<String> getChatList() {
        return chatList;
    }

    public void setChatList(List<String> chatList) {
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
