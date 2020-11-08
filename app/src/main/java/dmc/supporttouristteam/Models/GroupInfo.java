package dmc.supporttouristteam.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupInfo implements Serializable {
    private String id;
    private String name;
    private String image;
    private int numberOfPeople;
    private List<String> chatList;

    public GroupInfo() {
        this.chatList = new ArrayList<>();
    }

    public GroupInfo(String id, String name, String image, int numberOfPeople) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.numberOfPeople = numberOfPeople;
        this.chatList = new ArrayList<>();
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

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public List<String> getChatList() {
        return chatList;
    }

    public void setChatList(List<String> chatList) {
        this.chatList = chatList;
    }
}
