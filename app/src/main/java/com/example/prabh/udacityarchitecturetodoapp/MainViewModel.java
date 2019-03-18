package com.example.prabh.udacityarchitecturetodoapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MainViewModel extends AndroidViewModel {

    // Constant for logging
    private static final String TAG = MainViewModel.class.getSimpleName();

    // COMPLETED (2) Add a tasks member variable for a list of TaskEntry objects wrapped in a LiveData
    private LiveData<List<TaskEntry>> tasks;

    public MainViewModel(@NonNull Application application) {
        super(application);
        // COMPLETED (4) In the constructor use the loadAllTasks of the taskDao to initialize the tasks variable
        AppDatabase database = AppDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.taskDao().loadAllTasks();
    }

    // COMPLETED (3) Create a getter for the tasks variable
    public LiveData<List<TaskEntry>> getTasks() {
        return tasks;
    }
}
