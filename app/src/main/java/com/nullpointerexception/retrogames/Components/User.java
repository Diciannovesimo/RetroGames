package com.nullpointerexception.retrogames.Components;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 *      User
 *
 *      Stores informations of an account.
 *
 *      @author Luca
 */
public class User
{
    /**  Id of account (generally provided by FireBase)   */
    protected String id;
    protected String /** Email of account */
    email;
    protected String /** URL of profile picture */
    profileImageUrl;

    protected String /** Name of user */
    displayName;

    public User() {}

    /** Construct object from a FireBase user and set fields from it */
    public User(@NonNull FirebaseUser user) {
        id = user.getUid();
        email = user.getEmail();
        displayName = user.getDisplayName();

        if(user.getPhotoUrl() != null)
            profileImageUrl = user.getPhotoUrl().toString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

    /**
     *      @return Return name and surname concatenated
     */
    public String getDisplayName() {
        return displayName;
    }


}
