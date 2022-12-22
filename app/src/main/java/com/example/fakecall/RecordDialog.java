package com.example.fakecall;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Random;

public class RecordDialog extends Dialog implements OnClickListener  {
    LinearLayout buttomLayout;
    public Context c;
    private Button cancel;
    private Drawable img;
    private MediaPlayer mPlayer = null;
    private MediaRecorder mRecorder = null;
    public String path;
    private Button play;
    boolean playStarted = false;
    private Button record;
    boolean recordStarted = false;
    private Button save;
    SharedPreferences sharedPref;
    private  String fileName = "/Download/callervoice.3gp" ;
    RecordDialog(Context context) {
        super(context);
        this.c = context;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.recording_dialog);
        setCanceledOnTouchOutside(false);
        buttomLayout = (LinearLayout) findViewById(R.id.bottum_layout);
        buttomLayout.setVisibility(View.GONE);
        sharedPref = c.getSharedPreferences("file", 0);
        //play = (Button) findViewById(R.id.btn_play);
        record = (Button) findViewById(R.id.btn_record);
        cancel = (Button) findViewById(R.id.cancel);
        save = (Button) findViewById(R.id.save);
        //play.setOnClickListener(this);
        record.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        //play.setClickable(false);
    }

    private void onRecord(boolean started) {
        Toast.makeText(c,"on record:"+ started, Toast.LENGTH_SHORT).show();
        if (started) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        // write on SD card file data from the text box
//        File dir = new File(c.getFilesDir(), "mydir");
//        if(!dir.exists()){
//            dir.mkdir();
//        }
//
//        try {
//            File gpxfile = new File(dir, sFileName);
//            FileWriter writer = new FileWriter(gpxfile);
//            writer.append(sBody);
//            writer.flush();
//            writer.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download";

        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            Log.d("Files", "FileName:" + file[i].getName());
        }

        recordStarted = true;
        mRecorder = new MediaRecorder();
        File audioFolder = new File(Environment.getExternalStorageDirectory() + "/Download",
                "newfolder");
        Toast.makeText(c, " new folder  " + audioFolder,  Toast.LENGTH_SHORT).show();
        if (!audioFolder.exists()) {
            boolean success = audioFolder.mkdir();
            if (success) {
                Toast.makeText(c, " new folder  ", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(c, " new folder 123  ", Toast.LENGTH_SHORT).show();
            }
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(Environment.getExternalStorageDirectory() + "/temp.3gp");
       // mRecorder.setOutputFile(Environment.getExternalStorageDirectory() +fileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
            record.setText("recording");
            // Toast.makeText(c, " đang ghi âm  ", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            // Toast.makeText(c, " đang ghi âm  " + e, Toast.LENGTH_SHORT).show();
            Log.e("APP", "prepare() failed");
        }
    }

    private void stopRecording() {
        recordStarted = false;
        try {
            if (mRecorder != null) {
                mRecorder.release();
                Toast.makeText(c, "đã dừng ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
        record.setText("record");
    }

    private void onPlay(boolean started) {
        if (started) {
            stopPlaying();
            Drawable d = c.getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp);
            d.setBounds(0, 0, 100, 100);
            //play.setCompoundDrawables(d, null, null, null);
            return;
        }
        startPlaying();
        Drawable d = c.getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp);
        d.setBounds(0, 0, 100, 100);
        //Toast.makeText(c, "đang thì ghi âm ", Toast.LENGTH_SHORT).show();
        //play.setCompoundDrawables(d, null, null, null);
    }

    private void startPlaying() {
        playStarted = true;

        mPlayer = new MediaPlayer();
        try {
            // Toast.makeText(c, " bắt đầu ghi âm " + Environment.getExternalStorageDirectory().getAbsolutePath(), Toast.LENGTH_SHORT).show();
            //mPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.3gp");
            mPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() +  "/temp.3gp");
            Toast.makeText(c, " " + Environment.getExternalStorageDirectory().getAbsolutePath() +  "/temp.3gp", Toast.LENGTH_SHORT).show();
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("APP", "prepare() failed");
            //Toast.makeText(c, " bắt đầu ghi âm "+e, Toast.LENGTH_SHORT).show();
        }
        mPlayer.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                RecordDialog.this.onPlay(true);
            }
        });
    }

    private void stopPlaying() {
        playStarted = false;
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
                mPlayer.release();
            }
        } catch (Exception e) {
        }
    }

    public void onBackPressed() {
        onPlay(true);
        onRecord(true);
        Toast.makeText(c, "back", Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_record :
                buttomLayout.setVisibility(View.VISIBLE);
                if (playStarted) {
                    onPlay(playStarted);
                }
                onRecord(recordStarted);
               // play.setClickable(true);
                return;
//            case R.id.btn_play :
//                if (recordStarted) {
//                    onRecord(recordStarted);
//                }
//                onPlay(playStarted);
//                return;
            case R.id.cancel :
                onPlay(true);
                onRecord(true);
                dismiss();
                return;
            case R.id.save :
                onPlay(true);
                onRecord(true);
                copyFile();
                dismiss();
                return;
            default:
                dismiss();
                return;
        }
    }

    private void copyFile() {
        FileNotFoundException fnfe1;
        InputStream inputStream;
        Exception e;
        OutputStream outputStream;
        try {
            OutputStream out = null;
            File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + fileName);
            //InputStream in = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.3gp");
            InputStream in = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() +  "/temp.3gp");
            try {
                out = new FileOutputStream(dir);
            } catch (FileNotFoundException e2) {
                fnfe1 = e2;
                inputStream = in;
                Log.e("tag", fnfe1.getMessage());
                Toast.makeText(c, "falid e2"+fnfe1.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e3) {
                e = e3;
                inputStream = in;
                Log.e("tag", e.getMessage());
                Toast.makeText(c, "falid e3"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            try {
                byte[] buffer = new byte[1024];
                while (true) {
                    int read = in.read(buffer);
                    if (read != -1) {
                        out.write(buffer, 0, read);
                    } else {
                        in.close();
                        try {
                            out.flush();
                            out.close();
                            //new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/temp.3gp");
                            new File(Environment.getExternalStorageDirectory().getAbsolutePath() +  "/temp.3gp");
                            Editor editor = sharedPref.edit();
                            editor.putString("audio", Environment.getExternalStorageDirectory().getAbsolutePath() + fileName);
                            editor.apply();
                            Toast.makeText(c, "Recorded Audio set to caller Voice", Toast.LENGTH_SHORT).show();
                            return;
                        } catch (FileNotFoundException e4) {
                            fnfe1 = e4;
                            outputStream = out;
                            Log.e("tag", fnfe1.getMessage());
                            Toast.makeText(c, "falid 34"+fnfe1.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e5) {
                            e = e5;
                            outputStream = out;
                            Log.e("tag", e.getMessage());
                            Toast.makeText(c, "falid catch e5"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } catch (FileNotFoundException e6) {
                fnfe1 = e6;
                outputStream = out;
                inputStream = in;
                Log.e("tag", fnfe1.getMessage());
                Toast.makeText(c, "falid e6"+fnfe1.getMessage(), Toast.LENGTH_SHORT).show();
            } catch (Exception e7) {
                e = e7;
                outputStream = out;
                inputStream = in;
                Log.e("tag", e.getMessage());
                Toast.makeText(c, "falid e7"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e8) {
            fnfe1 = e8;
            Log.e("tag", fnfe1.getMessage());
            Toast.makeText(c, "falid e8"+fnfe1.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (Exception e9) {
            e = e9;
            Log.e("tag", e.getMessage());
            Toast.makeText(c, "falid e9"+e9.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }




}
