package com.nullpointerexception.retrogames.Components;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

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
    protected String id,
    /** Email of account */
    email,
    /** URL of profile picture */
    profileImageUrl,
    /** Name of user */
    name,
    /** Surname of user */
    surname;

    public User() {}

    /** Construct object from a FireBase user and set fields from it */
    public User(@NonNull FirebaseUser user)
    {
        id = user.getUid();
        email = user.getEmail();

        if(user.getPhotoUrl() != null)
            profileImageUrl = user.getPhotoUrl().toString();
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    /**
     *      @return Return name and surname concatenated
     */
    public String getDisplayName()
    {
        return name + " " + surname;
    }

    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl)
    {
        this.profileImageUrl = profileImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }

    public String getFieldId()
    {
        return id;
    }

    public Map<String, String> getSubFields()
    {
        Map<String, String> map = new HashMap<>();

        String displayName = getDisplayName();
        String imageUrl = getProfileImageUrl();

        if(displayName != null)
            map.put("displayName", displayName);

        if(imageUrl != null)
            map.put("profileImageUrl", imageUrl);

        return map;
    }

    public void restoreId(String id)
    {
        this.id = id;
    }

}
