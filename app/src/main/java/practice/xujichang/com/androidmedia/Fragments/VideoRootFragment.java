package practice.xujichang.com.androidmedia.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import practice.xujichang.com.androidmedia.R;

/**
 * 视频播放
 *
 * @author 许继昌
 * @version 1.0 2016/2/17
 */
public class VideoRootFragment extends Fragment implements View.OnClickListener {
    private Button useVideoViewBt, useSurfaceBt, useIntentBt, useMediaRecorderBt;
    private VideoViewFragment videoViewFragment;
    private mySurfaceFragment surfaceFragment;
    private MediaRecorderFragment recorderFragment;
    private Uri recordUri;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_root_layout, null);

        //获取控件的引用
        useIntentBt = (Button) view.findViewById(R.id.fragment_video_root_bt_intent);
        useMediaRecorderBt = (Button) view.findViewById(R.id.fragment_video_root_bt_mediarecorder);
        useVideoViewBt = (Button) view.findViewById(R.id.fragment_video_root_bt_videoview);
        useSurfaceBt = (Button) view.findViewById(R.id.fragment_video_root_bt_surface);

        //设置监听事件
        useIntentBt.setOnClickListener(this);
        useMediaRecorderBt.setOnClickListener(this);
        useVideoViewBt.setOnClickListener(this);
        useSurfaceBt.setOnClickListener(this);
        //默认显示VideoViewFragment
        if (savedInstanceState == null) {
            videoViewFragment = new VideoViewFragment();
            getFragmentManager().beginTransaction().replace(R.id.fragment_video_root_fl, videoViewFragment).commit();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_video_root_bt_intent:
                videoRecordUseIntent();
                break;
            case R.id.fragment_video_root_bt_mediarecorder:
                videoRecordUseMediaRecorder();
                break;
            case R.id.fragment_video_root_bt_videoview:
                playVideoUseVideoView();
                break;
            case R.id.fragment_video_root_bt_surface:
                playVideoUseSurface();
                break;
        }
    }

    /**
     * 使用官方Intent录制视频
     */
    private void videoRecordUseIntent() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, 0);
    }

    /**
     * 使用MediaRecorder录制视频
     */
    private void videoRecordUseMediaRecorder() {
        if (recorderFragment == null) {
            recorderFragment = new MediaRecorderFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.fragment_video_root_fl, recorderFragment).commit();
    }

    /**
     * 指定自己Surface并直接操作底层MediaPlayer
     */
    private void playVideoUseSurface() {
        if (surfaceFragment == null) {
            surfaceFragment = new mySurfaceFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.fragment_video_root_fl, surfaceFragment).commit();
    }

    /**
     * 使用VideoView播放视频
     */
    private void playVideoUseVideoView() {
        if (videoViewFragment == null) {
            videoViewFragment = new VideoViewFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.fragment_video_root_fl, videoViewFragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            recordUri = data.getData();
            if (videoViewFragment != null) {
                videoViewFragment.setVideoUri(recordUri);
            }
        }
    }
}
