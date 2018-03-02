package com.wearesputnik.istoria.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.wearesputnik.istoria.helpers.UserInfo;

@Table(name = "UserModel")
public class UserModel extends Model{
    @Column(name = "Email")
    public String Email;
    @Column(name = "DisplayName")
    public String DisplayName;
    @Column(name = "Photo")
    public String Photo;
    @Column(name = "Subscription")
    public Boolean Subscription;

    public static void AddEditUser(UserInfo userInfo) {
        UserModel userModel = new Select().from(UserModel.class).where("Id=?", 1).executeSingle();
        if (userModel != null) {
            userModel.DisplayName = userInfo.firs_name;
            userModel.Email = userInfo.email;
            userModel.Photo = userInfo.photo;
            userModel.Subscription = userInfo.subscription;
            userModel.save();
        }
        else {
            UserModel newUserModel = new UserModel();
            newUserModel.Email = userInfo.email;
            newUserModel.DisplayName = userInfo.firs_name;
            newUserModel.Photo = userInfo.photo;
            newUserModel.Subscription = userInfo.subscription;
            newUserModel.save();
        }
    }

    public static void UserSubscrition (Boolean subscription) {
        UserModel userModel = new Select().from(UserModel.class).where("Id=?", 1).executeSingle();
        if (userModel != null) {
            userModel.Subscription = subscription;
            userModel.save();
        }
    }

    public static boolean GetSubscrition () {
        UserModel userModel = new Select().from(UserModel.class).where("Id=?", 1).executeSingle();
        if (userModel != null) {
            if (userModel.Subscription != null) {
                return userModel.Subscription;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }

    public static UserInfo SelectUser() {
        UserModel userModel = new Select().from(UserModel.class).where("Id=?", 1).executeSingle();
        if (userModel != null) {
            UserInfo resultUserInfo = new UserInfo();
            resultUserInfo.email = userModel.Email;
            resultUserInfo.firs_name = userModel.DisplayName;
            resultUserInfo.photo = userModel.Photo;
            return resultUserInfo;
        }
        else {
            return null;
        }
    }
}
