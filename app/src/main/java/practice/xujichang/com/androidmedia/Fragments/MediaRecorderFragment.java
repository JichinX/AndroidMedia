package practice.xujichang.com.androidmedia.Fragments;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.List;

import practice.xujichang.com.androidmedia.R;

/**
 * XXX.java是趣猜App的XXX类。
 *
 * @author 许继昌
 * @version 1.0 2016/2/18
 */
public class MediaRecorderFragment extends Fragment implements SurfaceHolder.Callback {
    private Button startBt, finishBt, changeBt;
    private SurfaceView surfaceView;
    private MediaRecorder mediaRecorder;
    private SurfaceHolder holder;
    private Camera camera;
    private boolean isPreviewing;
    //后置摄像头ID
    private int cameraBackID = -1;
    //前置摄像头ID
    private int cameraFrontID = -1;
    private int cameraCount = -1;
    private boolean isCameraBack = true;
    private Camera.Size videoSize;

    private int videoWidth;
    private int videoHeight;
    private int surfaceWidth;
    private int surfaceHeight;
    private CamcorderProfile profile;
    private File file;
    private LinearLayout surfaceContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle
            savedInstanceState) {

        View view = inflater.inflate(R.layout.play_video_media_recorder, null);
        startBt = (Button) view.findViewById(R.id.start_record);
        finishBt = (Button) view.findViewById(R.id.finish_record);
        changeBt = (Button) view.findViewById(R.id.change_camera);
        surfaceView = (SurfaceView) view.findViewById(R.id.video_preview_surface);
        surfaceContainer = (LinearLayout) view.findViewById(R.id.surface_view_container);
        initSurfaceView();
        //默认停止按钮不可点击
        finishBt.setEnabled(false);
        startBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initMediaRecorder();
                startBt.setEnabled(false);
                finishBt.setEnabled(true);
                changeBt.setVisibility(View.GONE);
            }
        });
        finishBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                startBt.setEnabled(true);
                finishBt.setEnabled(false);
                changeBt.setVisibility(View.VISIBLE);
            }
        });
        changeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.stopPreview();
                camera.release();
                camera = null;
                if (isCameraBack) {
                    camera = Camera.open(cameraFrontID);
                    isCameraBack = false;
                } else {
                    camera = Camera.open(cameraBackID);
                    isCameraBack = true;
                }
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                initCamrea();
            }
        });
        getCameraInfo();
        return view;
    }

    /**
     * 获取系统中相机相关信息
     */
    private void getCameraInfo() {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraBackID = i;
            }
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraFrontID = i;
            }
        }

    }

    private void initSurfaceView() {
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
    }

    private void initMediaRecorder() {
        file = new File(Environment.getExternalStorageDirectory(), "testRecorder.mp4");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 创建MediaPlayer对象
        if (mediaRecorder == null) {
            mediaRecorder = new MediaRecorder();
        }
        camera.unlock();
        mediaRecorder.setCamera(camera);
        // 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        // 设置从摄像头采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        profile = null;
        if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
            profile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        }
        if (profile != null) {
            mediaRecorder.setProfile(profile);
        }
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.v("CAMERA_PREVIEW", "surfaceCreated");
        //获取摄像头对象,默认后置摄像头
        this.holder = holder;
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v("CAMERA_PREVIEW", "surfaceChanged");
        initCamrea();
    }

    private void initCamrea() {

        if (isPreviewing) {
            camera.stopPreview();
        }
        if (null != camera) {
            //获取Camera参数，并设置
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
            List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            List<Integer> previewFormats = parameters.getSupportedPreviewFormats();
            List<int[]> previewFrameRates = parameters.getSupportedPreviewFpsRange();
            List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
            Camera.Size psize;
            for (int i = 0; i < pictureSizes.size(); i++) {
                psize = pictureSizes.get(i);
                Log.v("CAMERA_PREVIEW", "SupportedPictureSizes:" + psize.width + "," + psize.height);
            }
            for (int i = 0; i < previewSizes.size(); i++) {
                psize = previewSizes.get(i);
                Log.v("CAMERA_PREVIEW", "SupportedPreviewSizes:" + psize.width + "," + psize.height);
            }
            videoSize = videoSizes.get(videoSizes.size() / 2);
            for (int i = 0; i < previewFormats.size(); i++) {
                Log.v("CAMERA_PREVIEW", "SupportedPreviewFormats:" + previewFormats.get(i));
            }
            for (int i = 0; i < previewFrameRates.size(); i++) {
                Log.v("CAMERA_PREVIEW", "SupportedPreviewFrameRates:" + previewFrameRates.get(i));

                for (int rate : previewFrameRates.get(i)) {
                    Log.v("CAMERA_PREVIEW", rate + ",");
                }
            }
            //设置自动对焦
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            // 设置拍照和预览图片大小

//            if (profile != null) {
//                parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
//            }

            // 横竖屏镜头自动调整
//            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "landscape"); //
            camera.setDisplayOrientation(90); // 在2.2以上可以使用
//            } else// 如果是横屏
//            {
//                parameters.set("orientation", "landscape"); //
//                camera.setDisplayOrientation(0); // 在2.2以上可以使用
//            }
            // 设定配置参数并开启预览
            camera.setParameters(parameters); // 将Camera.Parameters设定予Camera
            camera.startPreview(); // 打开预览画面
            isPreviewing = true;
            initWH();
        }
    }

    private void initWH() {
        ViewGroup.LayoutParams params = surfaceContainer.getLayoutParams();
        videoHeight = camera.getParameters().getPreviewSize().width;
        videoWidth = camera.getParameters().getPreviewSize().height;
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

        surfaceContainer.setLayoutParams(params);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v("CAMERA_PREVIEW", "surfaceDestroyed");
        camera.release();
    }
}
