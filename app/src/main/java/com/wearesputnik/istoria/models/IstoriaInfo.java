package com.wearesputnik.istoria.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

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
}
