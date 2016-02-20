package practice.xujichang.com.androidmedia;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import practice.xujichang.com.androidmedia.Fragments.AudioRootFragment;
import practice.xujichang.com.androidmedia.Fragments.VideoRootFragment;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private Button audioBt, videoBt;
    private AudioRootFragment audioRootFragment;
    private VideoRootFragment videoRootFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams
                    .FLAG_FULLSCREEN);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        setContentView(R.layout.activity_main);
        //获取控件引用
        audioBt = (Button) findViewById(R.id.main_button_audio_bt);
        videoBt = (Button) findViewById(R.id.main_button_video_bt);
        //设置点击事件
        audioBt.setOnClickListener(this);
        videoBt.setOnClickListener(this);
        //默认显示音频Fragment
        if (savedInstanceState == null) {
            audioRootFragment = new AudioRootFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fl, audioRootFragment).commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_button_audio_bt:
                if (audioRootFragment == null) {
                    audioRootFragment = new AudioRootFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fl, audioRootFragment).commit();
                break;
            case R.id.main_button_video_bt:
                if (videoRootFragment == null) {
                    videoRootFragment = new VideoRootFragment();
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fl, videoRootFragment).commit();
                break;
        }
    }
}
