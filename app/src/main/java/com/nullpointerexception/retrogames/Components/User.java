package com.nullpointerexception.retrogames.Components;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 *      User
 *
 *      Stores informations of an account.
 */
public class User
{
    /**  Id of account (generally provided by FireBase)   */
    protected String id;
    protected String /** Email of account */
    email;
    protected String /** URL of profile picture */
    profileImageUrl;
    /** Name of user */
    protected String nickname;

    public User() {}

    /** Costruttore user */
    public User(@NonNull FirebaseUser user) {
        id = user.getUid();
        email = user.getEmail();
        nickname = user.getDisplayName();

        if(user.getPhotoUrl() != null)
            profileImageUrl = user.getPhotoUrl().toString();
    }

    public String getEmail() {
        return email;
    }

    //Getters e Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFieldId() {
        return id;
    }

    public void restoreId(String id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }


}
