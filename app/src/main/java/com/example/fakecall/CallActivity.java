package com.example.fakecall ;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
//import android.support.v4.view.accessibility.AccessibilityEventCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.InterstitialAd;

import static android.view.WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;

public class CallActivity extends AppCompatActivity {
    ImageView accept;
    RelativeLayout attend;
    boolean attended = false;
    LinearLayout background;
    ImageView callerImg;
    RelativeLayout calling;
    Boolean check = Boolean.FALSE;
    Boolean checkCalender = Boolean.TRUE;
    Boolean checkNote = Boolean.TRUE;
    Boolean checkAddcall = Boolean.TRUE;
    Boolean checkPerson = Boolean.TRUE;
    Boolean checkMute = Boolean.TRUE;
    Context context;
    ImageView imageView ;
    ImageView hold , calenda , note , addcall , person , mute ;
    TextView inCall;
    TextView incomming;
    boolean locked;
    AudioManager mAudioManager;
    String mPath;
    Thread main;
    int min = 0;
    MediaPlayer mp;
    TextView nameText;
    int originalVolume;
    TextView phoneText;
    Ringtone r;
    ImageView reject;
    int sec = 0;
    SharedPreferences sharedPref;
    Thread t;
//    InterstitialAd mInterstitialAd;
//    AdRequest adRequestint;
    @SuppressLint("MissingInflatedId")
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        //locked = ((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode();
        context = this;
        Window window = getWindow();
        //window.addFlags(AccessibilityEventCompat.TYPE_WINDOWS_CHANGED);
        window.addFlags(FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call2);
       // loadInterstitialAd();
        context = this;
        calling = (RelativeLayout) findViewById(R.id.calling);
        callerImg = (ImageView) findViewById(R.id.caller_image);
        attend = (RelativeLayout) findViewById(R.id.attend);
        incomming = (TextView) findViewById(R.id.timer);
        inCall = (TextView) findViewById(R.id.text_in_call);
        imageView = (ImageView) findViewById(R.id.speaker);
        hold = (ImageView) findViewById(R.id.imgHold) ;
        calenda = (ImageView) findViewById(R.id.imgCalender) ;
        note = (ImageView) findViewById(R.id.imgNote) ;
        addcall = (ImageView) findViewById(R.id.imgaddCall) ;
        person = (ImageView) findViewById(R.id.imgcontacts) ;
        mute = (ImageView) findViewById(R.id.imgmute) ;
        //hold = (TextView) findViewById(R.id.hold);
        sharedPref = getSharedPreferences("file", 0);
        nameText = (TextView) findViewById(R.id.caller_name);
        phoneText = (TextView) findViewById(R.id.caller_number);
        accept = (ImageView) findViewById(R.id.gif_answer);
        reject = (ImageView) findViewById(R.id.gif_reject);
        main = Thread.currentThread();
        main.setName("Main Thread");
        setCaller();

    }

    void ring() {
        try {
            String path = sharedPref.getString("ring", "");
            if (path.equals("")) {
                r = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(1));
                r.play();
                return;
            }
            ringFromMedia(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void answer() {
        if (sharedPref.getString("ring", "").equals("")) {
            r.stop();
        } else {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.setStreamVolume(3, originalVolume, 0);
            if (mp != null) {
                mp.stop();
                mp.release();
            }
        }
        calling.setVisibility(View.GONE);
        attend.setVisibility(View.VISIBLE);
        inCall.setVisibility(View.VISIBLE);
        incomming.setVisibility(View.VISIBLE);
        audioPlayer(sharedPref.getString("audio", ""));
        attended = true;
        t = new Thread() {
            public void run() {
                try {
                    Log.v("NewMyThread", t.getName());
                    while (true) {
                        Thread.sleep(1000);
                        if (main.isAlive()) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    incomming.setText(updateTime());
                                }
                            });
                        } else {
                            return;
                        }
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    private String updateTime() {
        sec++;
        String time = "";
        if (sec < 60) {
            if (min < 10) {
                time = time + "0" + min;
            } else {
                time = time + min;
            }
            time = time + ":";
            if (sec < 10) {
                return time + "0" + sec;
            }
            return time + sec;
        }
        min++;
        sec = 0;
        if (min < 10) {
            time = time + "0" + min;
        } else {
            time = time + min;
        }
        time = time + ":";
        if (sec < 10) {
            return time + "0" + sec;
        }
        return time + sec;
    }

    public void decline() {
        if (sharedPref.getString("ring", "").equals("") && r != null) {
            r.stop();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    public  void OnClickHold(View view)
    {
        if (check) {
            hold.setImageResource(R.drawable.ic_phone_paused_black_24dp);
            check = Boolean.FALSE;
            return;
        }
        hold.setImageResource(R.drawable.ic_baseline_call_24);
        check = Boolean.TRUE;
    }
    public void OnClickSpeaker(View view) {
        if (check) {
            mp.pause();
            int length = mp.getCurrentPosition();
            mp.stop();
            mp.release();
            audioResume(length);
            imageView.setImageResource(R.drawable.speaker);
            check = Boolean.FALSE;
            return;
        }
        mp.pause();
        int length = mp.getCurrentPosition();
        mp.stop();
        mp.release();
        audioResume(length);
        imageView.setImageResource(R.drawable.speaker2);
        check = Boolean.TRUE;
    }

    void audioResume(int len) {
        mp = new MediaPlayer();
        try {
            if (check) {
                mp.setAudioStreamType(0);
            } else {
                mp.setAudioStreamType(3);
            }
            mp.setDataSource(mPath);
            mp.prepare();
            mp.seekTo(len);
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void audioPlayer(String path) {
        mPath = path;
        mp = new MediaPlayer();
        if(mp!=null){
            try {
                mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                originalVolume = mAudioManager.getStreamVolume(3);
                mAudioManager.setStreamVolume(3, mAudioManager.getStreamMaxVolume(3), 0);
                mp.setAudioStreamType(0);
                mp.setDataSource(path);
                mp.prepare();
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ringFromMedia(String path) {
        mPath = path;
        mp = new MediaPlayer();
        try {
            mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            originalVolume = mAudioManager.getStreamVolume(3);
            mAudioManager.setStreamVolume(3, mAudioManager.getStreamMaxVolume(3), 0);
            mp.setAudioStreamType(3);
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mAudioManager.setStreamVolume(3, originalVolume, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(16)
    void setCaller() {
        String name = sharedPref.getString("name", "");
        String phone = sharedPref.getString("number", "");
        String image = sharedPref.getString("image", "");
        nameText.setText(name);
        phoneText.setText(phone);
        int obj = -1;
        switch (image.hashCode()) {
            case 0:
                if (image.equals("")) {
                    obj = 0;
                    break;
                }
                break;
            case 48:
                if (image.equals("0")) {
                    obj = 1;
                    break;
                }
                break;
            case 49:
                if (image.equals("1")) {
                    obj = 2;
                    break;
                }
                break;
            case 50:
                if (image.equals("2")) {
                    obj = 3;
                    break;
                }
                break;
            case 51:
                if (image.equals("3")) {
                    obj = 4;
                    break;
                }
                break;
            case 52:
                if (image.equals("4")) {
                    obj = 5;
                    break;
                }
                break;
        }
        switch (obj) {
            case 0:
                callerImg.setImageResource(R.drawable.person);
                return;
            case 1:
                callerImg.setImageResource(R.drawable.gallery_btn_0);
                return;
            case 2:
                callerImg.setImageResource(R.drawable.gallery_btn_1);
                return;
            case 3:
                callerImg.setImageResource(R.drawable.gallery_btn_2);
                return;
            case 4:
                callerImg.setImageResource(R.drawable.gallery_btn_3);
                return;
            case 5:
                callerImg.setImageResource(R.drawable.gallery_btn_4);
                return;
            default:
                callerImg.setImageDrawable(Drawable.createFromPath(image));
        }
    }

    public void onBackPressed() {
    }

    protected void onStop() {
        super.onStop();
        String path = sharedPref.getString("ring", "");
        if (r != null && path.equals("")) {
            r.stop();
        }
        if (mp != null) {
            try {
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(3, originalVolume, 0);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onStart() {
        super.onStart();
        ring();
    }

    public void reciveCall(View view) {
        answer();
    }

    public void rejectCall(View view) {
        decline();
    }

    public void endCall(View view) {
        startActivity(new Intent(this, MainActivity.class));
        //showInterstitial();
        finish();
    }


    public void OnClickcalende(View view) {
       // boolean checkCalender = true ;
        if (checkCalender) {
            calenda.setImageResource(R.drawable.ic_baseline_perm_contact_calendar_24);
            checkCalender = Boolean.FALSE;
            return;
        }
        calenda.setImageResource(R.drawable.ic_perm_contact_calendar_black_24dp);
        checkCalender = Boolean.TRUE;
    }

    public void OnClickNote(View view) {
        if (checkNote) {
            note.setImageResource(R.drawable.note);
            checkNote = Boolean.FALSE;
            return;
        }
        note.setImageResource(R.drawable.ic_note_add_black_24dp);
        checkNote = Boolean.TRUE;
    }

    public void OnClickAddcal(View view) {
        if (checkAddcall) {
            addcall.setImageResource(R.drawable.addcallll);
            checkAddcall = Boolean.FALSE;
            return;
        }
        addcall.setImageResource(R.drawable.addcall);
        checkAddcall = Boolean.TRUE;
    }

    public void OnClickContacs(View view) {
        if (checkPerson) {
            person.setImageResource(R.drawable.ic_baseline_person_pin_24);
            checkPerson = Boolean.FALSE;
            return;
        }
        person.setImageResource(R.drawable.ic_baseline_person_pin_24444);
        checkPerson = Boolean.TRUE;
    }

    public void OnClickMic(View view) {
        if (checkMute) {
            mute.setImageResource(R.drawable.ic_baseline_mic_none_244444);
            checkMute = Boolean.FALSE;
            return;
        }
        mute.setImageResource(R.drawable.ic_mic_black_24dp);
        checkMute = Boolean.TRUE;
    }
}
