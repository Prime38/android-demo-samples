package com.prime.workmanagerdemo;

import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Data data=new Data.Builder().putInt( "number",10).build();
    Constraints constraints=new Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .setRequiresCharging(false)
        .build();
    OneTimeWorkRequest oneTimeWorkRequest=new OneTimeWorkRequest.Builder(SampleWorker.class)
        .setInputData(data)
        .setConstraints(constraints)
        .setInitialDelay(5 , TimeUnit.SECONDS)
        .addTag("oneTimeRequest")
        .build();
    PeriodicWorkRequest periodicWorkRequest=new PeriodicWorkRequest
        .Builder(SampleWorker.class,30,TimeUnit.SECONDS)
        .setInputData(data)
        .setConstraints(constraints)
        .addTag("periodicWork")
        .setInitialDelay(5, TimeUnit.SECONDS)
        .build();

//    WorkManager.getInstance(this).enqueue(oneTimeWorkRequest);
    WorkManager.getInstance(this).enqueue(periodicWorkRequest);
    WorkManager.getInstance(this).getWorkInfoByIdLiveData(periodicWorkRequest.getId())
        .observe(this, new Observer<WorkInfo>() {
          @Override
          public void onChanged(WorkInfo workInfo) {
            Log.d(TAG, "onChanged: "+ workInfo.toString());
            Toast.makeText(MainActivity.this, workInfo.getState().toString(), Toast.LENGTH_SHORT).show();
          }
        });

  }
}