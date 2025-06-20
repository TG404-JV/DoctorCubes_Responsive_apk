package com.tvm.doctorcube.adminpannel.databsemanager;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StudentDao {
    @Query("SELECT * FROM students")
    List<StudentEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<StudentEntity> students);

    @Query("DELETE FROM students WHERE collection = :collection")
    void deleteByCollection(String collection);

    @Query("SELECT * FROM students WHERE id = :id")
    StudentEntity getById(String id);

    @Query("DELETE FROM students WHERE id = :id")
    void deleteById(String id);
}
