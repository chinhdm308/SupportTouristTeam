package dmc.supporttouristteam.Models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String email;
    private String displayName;
    private String profileImg;

    public User() { }

    public User(String id, String email, String displayName, String profileImg) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.profileImg = profileImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
