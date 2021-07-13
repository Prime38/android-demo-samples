package com.prime.firebasejobdispatcherdemo.services;

import android.os.AsyncTask;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class MyFirebaseJobService extends JobService {
  @Override
  public boolean onStartJob(@NonNull JobParameters job) {
    String s="Sdsad";
    BackgroundTask backgroundTask=new BackgroundTask(){
      @Override
      protected void onPostExecute(String s) {
        Toast.makeText(MyFirebaseJobService.this, "message from backgroundTask : " + s, Toast.LENGTH_LONG).show();
        // if you are using any separate background thread / asynctask to execute your scheduled job
        // you must call jobFinished method.
        //otherwise system assumes that your job is still running on the background
        //this will cause the battery to drain faster
        jobFinished(job,false);
      }
    };
    backgroundTask.execute();

//    if the job is long running type -> return true
    return true; // Answers the question: "Is there still work going on?"
  }

  @Override
  public boolean onStopJob(@NonNull com.firebase.jobdispatcher.JobParameters job) {
    //if the job is failed -> retry the job again
    return true; // Answers the question: "Should this job be retried?"
  }

  public static class BackgroundTask extends AsyncTask< Void, Void, String > {
    @Override
    protected String doInBackground(Void... voids) {
      return "hello from BackgroundService->BackgroundTask->doInBackground";
    }
  }
}
