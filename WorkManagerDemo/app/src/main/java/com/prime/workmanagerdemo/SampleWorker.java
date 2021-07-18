package com.prime.workmanagerdemo;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SampleWorker extends Worker {

  private static final String TAG = "SampleWorker";

  public SampleWorker(@NonNull Context context,
      @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    Data inputData=getInputData();
    int number=inputData.getInt("number",-1);
    Log.d(TAG, "doWork: inside");

    return Result.success();
  }
}
