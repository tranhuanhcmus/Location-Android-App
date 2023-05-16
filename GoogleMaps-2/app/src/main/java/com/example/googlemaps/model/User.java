package com.example.googlemaps.model;
import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String email;
    private String user_id;
    private String username;
    private String avatar;

    private String telephone;

    private String bio;

    public User(String email, String user_id, String username, String avatar,String telephone) {
        this.email = email;
        this.user_id = user_id;
        this.username = username;
        this.avatar = avatar;
        this.telephone = telephone;
    }
    public User(){
        this.avatar = "default";
    }
    public User(String email, String user_id, String username, String avatar,String telephone,String bio) {
        this.email = email;
        this.user_id = user_id;
        this.username = username;
        this.avatar = avatar;
        this.telephone = telephone;
        this.bio = bio;
    }



    protected User(Parcel in) {
        email = in.readString();
        user_id = in.readString();
        username = in.readString();
        avatar = in.readString();
        telephone = in.readString();
        bio = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public static Creator<User> getCREATOR() {
        return CREATOR;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", avatar='" + avatar + '\'' +
                ", telephone='" + telephone + '\'' +
                ", bio='" + bio + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(avatar);
        dest.writeString(telephone);
        dest.writeString(bio);
    }
}
