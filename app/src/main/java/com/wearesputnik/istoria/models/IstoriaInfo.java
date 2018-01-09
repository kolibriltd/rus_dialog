package com.wearesputnik.istoria.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.wearesputnik.istoria.helpers.UserInfo;

/**
 * Created by admin on 21.06.17.
 */
@Table(name = "IstoriaInfo")
public class IstoriaInfo extends Model {
    @Column(name = "IsViewTwoScreen")
    public Boolean IsViewTwoScreen;
    @Column(name = "IsTapViewScreen")
    public Boolean IsTapViewScreen;
    @Column(name = "AppKey")
    public String AppKey;
    @Column(name = "UserId")
    public Integer UserId;
    @Column(name = "IsPush")
    public Boolean IsPush;

    public static void AddEditIstoriInfo(UserInfo userInfo) {
        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            istoriaInfo.AppKey = userInfo.app_key;
            istoriaInfo.IsPush = false;
            istoriaInfo.save();
        }
        else {
            IstoriaInfo newIstoriaInfo = new IstoriaInfo();
            newIstoriaInfo.AppKey = userInfo.app_key;
            newIstoriaInfo.IsViewTwoScreen = false;
            newIstoriaInfo.IsTapViewScreen = false;
            newIstoriaInfo.IsPush = false;
            newIstoriaInfo.save();
        }
    }
}
