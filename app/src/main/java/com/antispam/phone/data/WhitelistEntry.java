package com.antispam.phone.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a phone number in the whitelist
 */
@Entity(tableName = "whitelist")
public class WhitelistEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String phoneNumber;
    private String name;
    private long dateAdded;

    public WhitelistEntry(String phoneNumber, String name) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.dateAdded = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }
}