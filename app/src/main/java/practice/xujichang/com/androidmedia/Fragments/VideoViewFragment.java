package practice.xujichang.com.androidmedia.Fragments;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

import practice.xujichang.com.androidmedia.R;

/**
 * 使用VideoView播放
 *
 * @author 许继昌
 * @version 1.0 2016/2/18
 */
public class VideoViewFragment extends Fragment implements View.OnClickListener {
    private Button playBt, pauseBt, stopBt;
    private VideoView videoView;
    private static final int PLAY_ERROR_NOFILE = 0;
    private static final int NO_ERROR = -1;
    private int playError = -1;
    private int currentPosition = -1;
    private Uri recordUri = null;
    private ImageView thumbnailImage;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.play_video_use_videoview, null);

        //获取控件引用
        playBt = (Button) view.findViewById(R.id.video_view_start);
        pauseBt = (Button) view.findViewById(R.id.video_view_pause);
        stopBt = (Button) view.findViewById(R.id.video_view_stop);
        videoView = (VideoView) view.findViewById(R.id.video_view_main);
        thumbnailImage = (ImageView) view.findViewById(R.id.video_thumbnail);
        //设置监听
        playBt.setOnClickListener(this);
        pauseBt.setOnClickListener(this);
        stopBt.setOnClickListener(this);

        initVideoView();
        return view;
    }

    /**
     * 初始化VideoView
     */
    private void initVideoView() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.seekTo(0);
            }
        });
        MediaController controller = new MediaController(getContext(), true);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        //保持屏幕常亮
        videoView.setKeepScreenOn(true);

        File videoFile = new File(Environment.getExternalStorageDirectory(), "test.mp4");
        if (videoFile.exists()) {
            videoView.setVideoPath(videoFile.getAbsolutePath());
            videoView.setVisibility(View.GONE);
            thumbnailImage.setVisibility(View.VISIBLE);
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoFile.getAbsolutePath());
            Bitmap bitmap = media.getFrameAtTime();
            thumbnailImage.setImageBitmap(bitmap);
        } else {
            playError = PLAY_ERROR_NOFILE;
            showError();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.video_view_pause:
                videoView.pause();
//                currentPosition = videoView.getCurrentPosition();
                break;
            case R.id.video_view_start:
                if (recordUri != null) {
                    videoView.setVideoURI(recordUri);
                }
                videoView.setVisibility(View.VISIBLE);
                thumbnailImage.setVisibility(View.GONE);
                videoView.start();
                break;
            case R.id.video_view_stop:
                videoView.seekTo(videoView.getDuration());
                break;

        }
    }

    /**
     * 提示播放错误
     */
    private void showError() {
        String msg = null;
        switch (playError) {
            case PLAY_ERROR_NOFILE:
                msg = "文件不存在";
                break;
            default:
                msg = "未知错误";
                break;
        }
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void setVideoUri(Uri recordUri) {
        this.recordUri = recordUri;
    }
}
