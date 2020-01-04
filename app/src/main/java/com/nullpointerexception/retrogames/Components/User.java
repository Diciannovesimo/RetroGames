package com.nullpointerexception.retrogames.Components;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.auth.FirebaseUser;

/**
 *      User
 *
 *      Stores informations of an account.
 *
 *      @author Luca
 */
@Entity(tableName = "scoreboard")
public class User
{

    //PROVA PER ROOM DATABASE
    @PrimaryKey
    @NonNull   String id;
    public String email;
    public String profileImageUrl;
    public String displayName;

    public User() {}

    /** Construct object from a FireBase user and set fields from it */
    public User(@NonNull FirebaseUser user)
    {
        id = user.getUid();
        email = user.getEmail();
        displayName = user.getDisplayName();

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

    public String getProfileImageUrl()
    {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

    public void restoreId(String id)
    {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }


}
