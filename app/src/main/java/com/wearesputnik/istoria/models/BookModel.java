package com.wearesputnik.istoria.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by admin on 08.06.17.
 */
@Table(name = "Books")
public class BookModel extends Model{
    @Column(name = "IdDbServer")
    public String IdDbServer;
    @Column(name = "Name")
    public String Name;
    @Column(name = "Author")
    public String Author;
    @Column(name = "Description")
    public String Description;
    @Column(name = "TextInfoList")
    public String TextInfoList;
    @Column(name = "IsView")
    public boolean IsView;
    @Column(name = "IsViewCount")
    public Integer IsViewCount;
    @Column(name = "IsViewTapCount")
    public Integer IsViewTapCount;
    @Column(name = "PathCoverFile")
    public String PathCoverFile;
    @Column(name = "PathCoverFileStorage")
    public String PathCoverFileStorage;
    @Column(name = "TimerStopMin")
    public String TimerStopMin;
    @Column(name = "TapStooBool")
    public boolean TapStooBool;

}
