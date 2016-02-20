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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

import practice.xujichang.com.androidmedia.R;

/**
 * 通过指定自己的Surface
 *
 * @author 许继昌
 * @version 1.0 2016/2/18
 */
public class mySurfaceFragment extends Fragment implements SurfaceHolder.Callback, View.OnClickListener {
    private Button startBt, pauseBt, stopBt;
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private ImageView thumbnailImage;
    private File videoFile;
    //视频播放的宽高比
    private float ratio;
    //视频的宽高
    private int videoWidth;
    private int videoHeight;
    //surfaceView 的宽高
    private int surfaceWidth;
    private int surfaceHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.play_video_surface_layout, null);
        //获取控件的引用
        videoFile = new File(Environment.getExternalStorageDirectory(), "test.mp4");

        startBt = (Button) view.findViewById(R.id.surface_start);
        stopBt = (Button) view.findViewById(R.id.surface_stop);
        pauseBt = (Button) view.findViewById(R.id.surface_pause);
        surfaceView = (SurfaceView) view.findViewById(R.id.play_video_surface_surfaceView);
        thumbnailImage = (ImageView) view.findViewById(R.id.video_thumbnail);
        //设置监听事件
        startBt.setOnClickListener(this);
        stopBt.setOnClickListener(this);
        pauseBt.setOnClickListener(this);

        initSurfaceView();
        surfaceView.setVisibility(View.GONE);
        thumbnailImage.setVisibility(View.VISIBLE);

        if (videoFile.exists()) {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(videoFile.getAbsolutePath());
            Bitmap bitmap = media.getFrameAtTime();
            thumbnailImage.setImageBitmap(bitmap);
        } else {
            Toast.makeText(getContext(), "文件不存在", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void initSurfaceView() {
        surfaceView.setKeepScreenOn(true);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (videoFile.exists()) {
            player = MediaPlayer.create(getContext(), Uri.fromFile(videoFile), holder);
            initWH();
            player.start();
        } else {
            Toast.makeText(getContext(), "文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

    private void initWH() {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        videoWidth = player.getVideoWidth();
        videoHeight = player.getVideoHeight();
        surfaceWidth = surfaceView.getWidth();
        surfaceHeight = surfaceView.getHeight();
        int mWidth;
        int mHeight;
        mWidth = surfaceHeight * videoWidth / videoHeight;
        mHeight = surfaceWidth * videoHeight / videoWidth;
        if (mWidth < surfaceWidth) {
            params.width = mWidth;
        }
        if (mHeight < surfaceHeight) {
            params.height = mHeight;
        }
        surfaceView.setLayoutParams(params);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        player.release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.surface_pause:
                player.pause();
                break;
            case R.id.surface_stop:
                player.seekTo(player.getDuration());
                break;
            case R.id.surface_start:
                if (player == null) {
                    surfaceView.setVisibility(View.VISIBLE);
                    thumbnailImage.setVisibility(View.GONE);
                } else {
                    player.start();
                }
                break;

        }
    }
}
