package com.antispam.phone.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.antispam.phone.data.WhitelistEntry;
import com.antispam.phone.data.WhitelistRepository;

import java.util.List;

/**
 * ViewModel for the whitelist functionality
 */
public class WhitelistViewModel extends AndroidViewModel {
    
    private WhitelistRepository repository;
    private LiveData<List<WhitelistEntry>> allEntries;
    
    public WhitelistViewModel(@NonNull Application application) {
        super(application);
        repository = new WhitelistRepository(application);
        allEntries = repository.getAllEntries();
    }
    
    public void insert(WhitelistEntry entry) {
        repository.insert(entry);
    }
    
    public void update(WhitelistEntry entry) {
        repository.update(entry);
    }
    
    public void delete(WhitelistEntry entry) {
        repository.delete(entry);
    }
    
    public void deleteAll() {
        repository.deleteAll();
    }
    
    public LiveData<List<WhitelistEntry>> getAllEntries() {
        return allEntries;
    }
    
    public boolean isNumberInWhitelist(String phoneNumber) {
        return repository.isNumberInWhitelist(phoneNumber);
    }
}