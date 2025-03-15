package com.antispam.phone.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object for the whitelist table
 */
@Dao
public interface WhitelistDao {
    
    @Insert
    void insert(WhitelistEntry entry);
    
    @Update
    void update(WhitelistEntry entry);
    
    @Delete
    void delete(WhitelistEntry entry);
    
    @Query("DELETE FROM whitelist")
    void deleteAll();
    
    @Query("SELECT * FROM whitelist ORDER BY name ASC")
    LiveData<List<WhitelistEntry>> getAllEntries();
    
    @Query("SELECT * FROM whitelist WHERE phoneNumber = :phoneNumber LIMIT 1")
    WhitelistEntry getEntryByPhoneNumber(String phoneNumber);
    
    @Query("SELECT COUNT(*) FROM whitelist WHERE phoneNumber = :phoneNumber")
    int isNumberInWhitelist(String phoneNumber);
}