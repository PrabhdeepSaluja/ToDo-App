package com.example.prabh.udacityarchitecturetodoapp;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import java.util.List;

import static android.widget.LinearLayout.VERTICAL;

public class MainActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener {

    // Constant for logging
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        //DividerItemDecoration is a RecyclerView.ItemDecoration that can be used as a
        // divider between items of a LinearLayoutManager.
        // It supports both HORIZONTAL and VERTICAL orientations.

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {

                        // get the position from the viewHolder parameter
                        int position=viewHolder.getAdapterPosition();
                        List<TaskEntry> tasks=mAdapter.getTasks();
                        // Call deleteTask in the taskDao with the task at that position
                        mDb.taskDao().deleteTask(tasks.get(position));
                        // Call retrieveTasks method to refresh the UI
                        //retrieveTasks();
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });
        mDb=AppDatabase.getInstance(getApplicationContext());
        setUpViewModel();
    }


    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent=new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this re-queries the database data for any changes.
     */

    // <--------------------------------OPTIONAL-------------------------------->

    /*@Override
    protected void onResume(){
        super.onResume();

        // Get the diskIO Executor from the instance of AppExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        AppExecutors.getInstance().diskIO().execute(new Runnable(){
            @Override
            public void run(){

                // Move the logic into the run method and
                // Extract the list of tasks to a final variable
                final List<TaskEntry> tasks=mDb.taskDao().loadAllTasks();

                // Wrap the setTask call in a call to runOnUiThread
                // We will be able to simplify this once we learn more
                // about Android Architecture Components
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setTasks(tasks);
                    }
                });
            }
        });
        // mAdapter.setTasks(mDb.taskDao().loadAllTasks());
    }*/

    private void setUpViewModel(){
        // Declare a ViewModel variable and initialize it by calling ViewModelProviders.of
        MainViewModel viewModel= ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasks().observe(this, new Observer<List<TaskEntry>>(){

            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries){
                Log.d(TAG, "Updating list of tasks from livedata from viewmodel");
                mAdapter.setTasks(taskEntries);
            }
        });
    }

    // <------------------ This method does not receive tasks anymore------------>
    // <--------------We are usine setUpViewModel instead-------------->
    /*private void retrieveTasks(){

        Log.d(TAG, "Actively retrievig tasks from the database");

        LiveData<List<TaskEntry>> tasks=mDb.taskDao().loadAllTasks();

        tasks.observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(@Nullable List<TaskEntry> taskEntries) {

                Log.d(TAG, "Receiving live updates from database");

                mAdapter.setTasks(taskEntries);
            }
        });

        //  <!----------------------OPTIONAL------------------->
        *//*AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<TaskEntry> tasks=mDb.taskDao().loadAllTasks();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setTasks(tasks);
                    }
                });
            }
        });*//*
    }*/
}
