package com.example.prabh.udacityarchitecturetodoapp;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class AddTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDb;
    private final int mTaskID;

    public AddTaskViewModelFactory(AppDatabase database, int TaskID){
        mDb=database;
        mTaskID=TaskID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass){
        return (T) new AddTaskViewModel(mDb, mTaskID);
    }
}
