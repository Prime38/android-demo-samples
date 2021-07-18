package com.ep.files;

import static com.ammarptn.gdriverest.DriveServiceHelper.getGoogleDriveService;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import com.ammarptn.gdriverest.DriveServiceHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.DriveScopes;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
public class MainActivity extends AppCompatActivity {
  Button startbtn, stopBtn;
  private String recordPermission = Manifest.permission.RECORD_AUDIO;
  private int PERMISSION_CODE = 21;
  private MediaRecorder mediaRecorder;
  private String recordFile;
  private boolean isRecording = false;
  File[] allFiles;
  private static final String TAG = "MainActivity";
  DriveServiceHelper mDriveServiceHelper;
  private static final int REQUEST_CODE_SIGN_IN = 100;
  private GoogleSignInClient mGoogleSignInClient;
  private String appName="filesApp";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    startbtn= findViewById(R.id.startBtn);
    stopBtn=findViewById(R.id.stopBtn);

    startbtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if(isRecording) {
          //Stop Recording
          stopRecording();

          // Change button image and set Recording state to false
//          recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_stopped, null));
          isRecording = false;
        } else {
          //Check permission to record audio
          if(checkPermissions()) {
            //Start Recording
            startRecording();

            // Change button image and set Recording state to false
//            recordBtn.setImageDrawable(getResources().getDrawable(R.drawable.record_btn_recording, null));
            isRecording = true;
          }
        }

      }
    });

    stopBtn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

      }
    });

  }
  private boolean checkPermissions() {
    //Check permission
    if (ActivityCompat.checkSelfPermission(this, recordPermission) == PackageManager.PERMISSION_GRANTED) {
      //Permission Granted
      return true;
    } else {
      //Permission not granted, ask for permission
      ActivityCompat.requestPermissions(this, new String[]{recordPermission}, PERMISSION_CODE);
      return false;
    }
  }
  private void stopRecording() {
    //Stop Timer, very obvious
//    timer.stop();

    //Change text on page to file saved
//    filenameText.setText("Recording Stopped, File Saved : " + recordFile);

    //Stop media recorder and set it to null for further use to record new audio
    mediaRecorder.stop();
    mediaRecorder.release();
    mediaRecorder = null;
  }

  private void startRecording() {
    //Start timer from 0
//    timer.setBase(SystemClock.elapsedRealtime());
//    timer.start();

    //Get app external directory path
    String recordPath = this.getExternalFilesDir("/").getAbsolutePath();

    //Get current date and time
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.CANADA);
    Date now = new Date();

    //initialize filename variable with date and time at the end to ensure the new file wont overwrite previous file
    recordFile = "Recording_" + formatter.format(now) + ".3gp";

//    filenameText.setText("Recording, File Name : " + recordFile);

    //Setup Media Recorder for recording
    mediaRecorder = new MediaRecorder();
    mediaRecorder.setAudioSource(AudioSource.DEFAULT);
    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
    mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    try {
      mediaRecorder.prepare();
    } catch (IOException e) {
      e.printStackTrace();
    }

    //Start Recording
    mediaRecorder.start();
  }

   void sendToDrive(){
     String path = this.getExternalFilesDir("/").getAbsolutePath();
     File directory = new File(path);
     allFiles = directory.listFiles();
     for (File file:allFiles
     ) {
       Log.d(TAG, "sendToDrive: "+file.getName());
     }
   }

   void googleSignIn(){
     GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

     if(account == null){
       signIn();
     }
     else {

       GoogleAccountCredential credential =
           GoogleAccountCredential.usingOAuth2(
               this, Collections.singleton(DriveScopes.DRIVE_FILE));
       credential.setSelectedAccount(account.getAccount());
       com.google.api.services.drive.Drive googleDriveService =
           new com.google.api.services.drive.Drive.Builder(
               AndroidHttp.newCompatibleTransport(),
               new GsonFactory(),
               credential)
               .setApplicationName(appName)
               .build();
       mDriveServiceHelper = new DriveServiceHelper(googleDriveService);

     }
   }
  private void signIn() {

    mGoogleSignInClient = buildGoogleSignInClient();
    startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
  }

  private GoogleSignInClient buildGoogleSignInClient() {
    GoogleSignInOptions signInOptions =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Drive.SCOPE_FILE)
            .requestEmail()
            .build();
    return GoogleSignIn.getClient(getApplicationContext(), signInOptions);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
    switch (requestCode) {
      case REQUEST_CODE_SIGN_IN:
        if (resultCode == Activity.RESULT_OK && resultData != null) {
          handleSignInResult(resultData);
        }
        break;


    }

    super.onActivityResult(requestCode, resultCode, resultData);
  }

  public void test() {
    System.out.println("test");
  }

  private void handleSignInResult(Intent result) {
    GoogleSignIn.getSignedInAccountFromIntent(result)
        .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
          @Override
          public void onSuccess(GoogleSignInAccount googleSignInAccount) {
            Log.d(TAG, "Signed in as " + googleSignInAccount.getEmail());
//            email.setText(googleSignInAccount.getEmail());

            mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getApplicationContext(), googleSignInAccount, appName));

            Log.d(TAG, "handleSignInResult: " + mDriveServiceHelper);
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {
            Log.e(TAG, "Unable to sign in.", e);
          }
        });
  }

  @Override
  protected void onStart() {
    super.onStart();
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
    if (account == null) {
      signIn();
    } else {
//      email.setText(account.getEmail());
      mDriveServiceHelper = new DriveServiceHelper(getGoogleDriveService(getApplicationContext(), account, appName));
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if(isRecording){
      stopRecording();
    }
  }
}