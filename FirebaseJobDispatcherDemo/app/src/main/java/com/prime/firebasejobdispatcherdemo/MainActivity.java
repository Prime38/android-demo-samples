package com.prime.firebasejobdispatcherdemo;

import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.prime.firebasejobdispatcherdemo.services.MyFirebaseJobService;

public class MainActivity extends AppCompatActivity {
  private static final String JOB_TAG="my_job_tag";
  FirebaseJobDispatcher firebaseJobDispatcher;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    firebaseJobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(getApplicationContext()));
  }
  public void startJob(View view) {
    Job job=firebaseJobDispatcher.newJobBuilder()
        .setService(MyFirebaseJobService.class)
        .setLifetime(Lifetime.FOREVER)
        .setRecurring(true)
        .setTag(JOB_TAG)
        .setTrigger(Trigger.executionWindow(60,150))
        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
        .setReplaceCurrent(false)
        .setConstraints(Constraint.ON_ANY_NETWORK)
        .build();
    firebaseJobDispatcher.mustSchedule(job);
    Toast.makeText(this, "JOB SCHEDULED", Toast.LENGTH_LONG).show();
  }

  public void stopJob(View view) {
    firebaseJobDispatcher.cancel(JOB_TAG);
    Toast.makeText(this, "JOB CANCELLED", Toast.LENGTH_LONG).show();

  }


}