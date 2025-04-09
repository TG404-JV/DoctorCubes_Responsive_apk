package com.tvm.doctorcube.adminpannel.databsemanager;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "students")
public class StudentEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String collection;
    public String data;
    public long lastUpdated;
}
