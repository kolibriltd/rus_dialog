package com.wearesputnik.istoria.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by admin on 24.07.17.
 */
@Table(name = "Books")
public class TypeBook extends Model {
    @Column(name = "TypeId")
    public String TypeId;
    @Column(name = "Name")
    public String Name;
}
