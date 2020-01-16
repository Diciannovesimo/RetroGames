package com.nullpointerexception.retrogames.Components;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;

/**
 *      User
 *
 *      Memorizza le informazioni dell'account
 */
public class User
{
    /** Id of account (generally provided by FireBase)   */
    protected String id;
    /** Email of account */
    private String email;
    /** URL of profile picture */
    private String
    profileImageUrl;
    /** Name of user */
    protected String nickname;

    /** Costruttore user */
    public User() {}

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

    /** Getters and setters**/
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

    public String getNickname() {
        return nickname;
    }

}
