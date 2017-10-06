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

    public static void AddEditUser(UserInfo userInfo) {
        UserModel userModel = new Select().from(UserModel.class).where("Id=?", 1).executeSingle();
        if (userModel != null) {
            userModel.DisplayName = userInfo.firs_name;
            userModel.Email = userInfo.email;
            userModel.Photo = userInfo.photo;
            userModel.save();
        }
        else {
            UserModel newUserModel = new UserModel();
            newUserModel.Email = userInfo.email;
            newUserModel.DisplayName = userInfo.firs_name;
            newUserModel.Photo = userInfo.photo;
            newUserModel.save();
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
