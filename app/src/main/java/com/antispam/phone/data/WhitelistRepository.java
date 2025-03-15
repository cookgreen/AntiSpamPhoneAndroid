package com.antispam.phone.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * Repository class that abstracts access to the whitelist data sources
 */
public class WhitelistRepository {
    private WhitelistDao whitelistDao;
    private LiveData<List<WhitelistEntry>> allEntries;
    
    public WhitelistRepository(Application application) {
        WhitelistDatabase database = WhitelistDatabase.getInstance(application);
        whitelistDao = database.whitelistDao();
        allEntries = whitelistDao.getAllEntries();
    }
    
    public void insert(WhitelistEntry entry) {
        new InsertEntryAsyncTask(whitelistDao).execute(entry);
    }
    
    public void update(WhitelistEntry entry) {
        new UpdateEntryAsyncTask(whitelistDao).execute(entry);
    }
    
    public void delete(WhitelistEntry entry) {
        new DeleteEntryAsyncTask(whitelistDao).execute(entry);
    }
    
    public void deleteAll() {
        new DeleteAllEntriesAsyncTask(whitelistDao).execute();
    }
    
    public LiveData<List<WhitelistEntry>> getAllEntries() {
        return allEntries;
    }
    
    public boolean isNumberInWhitelist(final String phoneNumber) {
        try {
            return new IsNumberInWhitelistAsyncTask(whitelistDao).execute(phoneNumber).get() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static class InsertEntryAsyncTask extends AsyncTask<WhitelistEntry, Void, Void> {
        private WhitelistDao whitelistDao;
        
        private InsertEntryAsyncTask(WhitelistDao whitelistDao) {
            this.whitelistDao = whitelistDao;
        }
        
        @Override
        protected Void doInBackground(WhitelistEntry... entries) {
            whitelistDao.insert(entries[0]);
            return null;
        }
    }
    
    private static class UpdateEntryAsyncTask extends AsyncTask<WhitelistEntry, Void, Void> {
        private WhitelistDao whitelistDao;
        
        private UpdateEntryAsyncTask(WhitelistDao whitelistDao) {
            this.whitelistDao = whitelistDao;
        }
        
        @Override
        protected Void doInBackground(WhitelistEntry... entries) {
            whitelistDao.update(entries[0]);
            return null;
        }
    }
    
    private static class DeleteEntryAsyncTask extends AsyncTask<WhitelistEntry, Void, Void> {
        private WhitelistDao whitelistDao;
        
        private DeleteEntryAsyncTask(WhitelistDao whitelistDao) {
            this.whitelistDao = whitelistDao;
        }
        
        @Override
        protected Void doInBackground(WhitelistEntry... entries) {
            whitelistDao.delete(entries[0]);
            return null;
        }
    }
    
    private static class DeleteAllEntriesAsyncTask extends AsyncTask<Void, Void, Void> {
        private WhitelistDao whitelistDao;
        
        private DeleteAllEntriesAsyncTask(WhitelistDao whitelistDao) {
            this.whitelistDao = whitelistDao;
        }
        
        @Override
        protected Void doInBackground(Void... voids) {
            whitelistDao.deleteAll();
            return null;
        }
    }
    
    private static class IsNumberInWhitelistAsyncTask extends AsyncTask<String, Void, Integer> {
        private WhitelistDao whitelistDao;
        
        private IsNumberInWhitelistAsyncTask(WhitelistDao whitelistDao) {
            this.whitelistDao = whitelistDao;
        }
        
        @Override
        protected Integer doInBackground(String... strings) {
            return whitelistDao.isNumberInWhitelist(strings[0]);
        }
    }
}