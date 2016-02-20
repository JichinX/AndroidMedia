package practice.xujichang.com.androidmedia.Fragments;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import practice.xujichang.com.androidmedia.R;

/**
 * 音频的根Fragment
 *
 * @author 许继昌
 * @version 1.0 2016/2/17
 */
public class AudioRootFragment extends Fragment implements View.OnClickListener {
    private Button localAudio, resourceAudio, onlineAudio;
    private Button recordAudio;
    private Button playAudio;
    private Button stopTask;
    private MediaPlayer filePlayer, resourcePlayer;
    private MediaRecorder mediaRecorder;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    /**
     * 播放本地音频
     */
    private static final int CURRENT_PLAY_LOCAL_AUDIO = 0;
    /**
     * 播放资源音频
     */
    private static final int CURRENT_PLAY_RESOURCE_AUDIO = 1;
    /**
     * 使用Audio录音
     */
    private static final int CURRENT_PLAY_RECORD_AUDIO = 2;
    /**
     * 使用Media录音
     */
    private static final int CURRENT_PLAY_RECORD_MEDIA = 3;
    /**
     * 播放网络音频
     */
    private static final int CURRENT_PLAY_ONLINE_AUDIO = 4;
    /**
     * 使用AudioTrack播放原始音频
     */
    private static final int CURRENT_PLAY_WITH_AUDIOTRACK = 5;
    /**
     * 程序当前执行的任务
     */
    private int currentTask = -1;
    private int currentState = -1;
    private int recordState = -1;

    private static final int RECORDING = 0;
    private static final int RECORD_SUCESS = 1;
    private static final int RECORD_STOP = 2;
    private static final int STATE_STOP = 10;
    private static final int STATE_PLAYING = 11;
    private static final int STATE_PAUSE = 12;
    private Button recordMedia;
    /*********
     * 录制原始音频的一些参数
     * /
     * /**
     * 频率
     */
    private int frequency = 11025;
    /**
     * 声道
     */
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    /**
     * 编码
     */
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private boolean isRecording = false;
    private DataOutputStream dos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_audio_root_layout, null);
        //获取控件引用
        localAudio = (Button) view.findViewById(R.id.fragment_audio_root_local_audio);
        resourceAudio = (Button) view.findViewById(R.id.fragment_audio_root_resource_audio);
        onlineAudio = (Button) view.findViewById(R.id.fragment_audio_root_online_audio);
        recordAudio = (Button) view.findViewById(R.id.fragment_audio_root_record_audio);
        recordMedia = (Button) view.findViewById(R.id.fragment_audio_root_record_media);
        playAudio = (Button) view.findViewById(R.id.fragment_audio_root_play_audio);
        stopTask = (Button) view.findViewById(R.id.stop_current_task);
        //设置监听
        localAudio.setOnClickListener(this);
        resourceAudio.setOnClickListener(this);
        onlineAudio.setOnClickListener(this);
        recordAudio.setOnClickListener(this);
        playAudio.setOnClickListener(this);
        stopTask.setOnClickListener(this);
        recordMedia.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragment_audio_root_local_audio:
                if (currentTask != CURRENT_PLAY_LOCAL_AUDIO) {
                    stopTask(false);
                }
                currentTask = CURRENT_PLAY_LOCAL_AUDIO;
                playAudioForLocal();
                break;
            case R.id.fragment_audio_root_resource_audio:
                if (currentTask != CURRENT_PLAY_RESOURCE_AUDIO) {
                    stopTask(false);
                }
                currentTask = CURRENT_PLAY_RESOURCE_AUDIO;
                playAudioResource();
                break;
            case R.id.fragment_audio_root_online_audio:
                break;
            case R.id.fragment_audio_root_record_audio:
                if (currentTask != CURRENT_PLAY_RECORD_AUDIO) {
                    stopTask(false);
                }
                currentTask = CURRENT_PLAY_RECORD_AUDIO;
                recordWithAudioRecorder();
                break;
            case R.id.fragment_audio_root_record_media:
                if (currentTask != CURRENT_PLAY_RECORD_MEDIA) {
                    stopTask(false);
                }
                currentTask = CURRENT_PLAY_RECORD_MEDIA;
                recordWithMediaRecorder();
                break;
            case R.id.fragment_audio_root_play_audio:
                if (currentTask != CURRENT_PLAY_WITH_AUDIOTRACK) {
                    stopTask(false);
                }
                currentTask = CURRENT_PLAY_WITH_AUDIOTRACK;
                //使用AudioTrack播放
                playAudioWithAudioTrack();
                break;
            case R.id.stop_current_task:
                stopTask(true);
                break;
            default:
                break;
        }
    }

    /**
     * 使用AudioTrack播放音频
     */
    private void playAudioWithAudioTrack() {
        if (audioTrack == null) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "raw.pcm");
                int audioLength = (int) (file.length());
                short[] audio = new short[audioLength];
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
                int i = 0;
                while (dis.available() > 0) {
                    audio[i++] = dis.readShort();
                }
                dis.close();
                audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, frequency, channelConfig, audioEncoding,
                        audioLength, AudioTrack.MODE_STREAM);
                audioTrack.write(audio, 0, audioLength);
                audioTrack.play();
                audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                    @Override
                    public void onMarkerReached(AudioTrack track) {

                    }

                    @Override
                    public void onPeriodicNotification(AudioTrack track) {

                    }
                });
                showMsg("开始播放音频");
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                audioTrack.pause();
                showMsg("暂停播放音频");
                return;
            }
            if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                audioTrack.play();
                showMsg("继续播放音频");
                return;
            }
        }
    }

    /**
     * 使用AudioRecorder录制音频
     */
    private void recordWithAudioRecorder() {
        try {
            if (audioRecord == null) {
                File file = new File(Environment.getExternalStorageDirectory(), "raw.pcm");
                if (!file.exists()) {
                    file.createNewFile();
                }
                OutputStream os = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                dos = new DataOutputStream(bos);
                //取缓冲区大小
                final int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfig, audioEncoding);
                final short[] buffer = new short[bufferSize];
                //实例化AudioRecord对象
                audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfig, audioEncoding,
                        bufferSize);
                //开始录制
                audioRecord.startRecording();
                isRecording = true;
                new Thread() {
                    @Override
                    public void run() {
                        while (isRecording) {
                            try {
                                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                                for (int i = 0; i < bufferReadResult; i++) {

                                    dos.writeShort(buffer[i]);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                isRecording = false;
                            }
                        }
                    }
                }.start();
                showMsg("开始录音");
            } else {
                isRecording = false;
                audioRecord.stop();
                audioRecord.release();
                audioRecord = null;
                dos.close();
                showMsg("停止录音");
                currentTask = -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showMsg(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 使用MediaRecorder录制音频
     */
    private void recordWithMediaRecorder() {

        if (mediaRecorder == null) {
            try {
                //创建MediaRecorder实例
                mediaRecorder = new MediaRecorder();
                //设置媒体源
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                //设置输出文件格式以及编码
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                //设置保存路径
                File file = new File(Environment.getExternalStorageDirectory(), "test.amr");
                if (!file.exists()) {
                    file.createNewFile();
                }
                mediaRecorder.setOutputFile(file.getAbsolutePath());
                mediaRecorder.prepare();
                mediaRecorder.start();
                showMsg("开始录制音频");

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            showMsg("停止录制音频");
            currentTask = -1;
        }
    }

    /**
     * 播放资源文件中音频
     */
    private void playAudioResource() {
        if (resourcePlayer != null) {
            if (resourcePlayer.isPlaying()) {
                resourcePlayer.pause();
                currentState = STATE_PAUSE;
                showMsg("暂停播放资源音频");

            } else {
                if (currentState == STATE_STOP) {
                    try {
                        resourcePlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    showMsg("继续播放资源音频");
                    resourcePlayer.start();
                    currentState = STATE_PLAYING;
                }

            }
            return;
        }
        resourcePlayer = MediaPlayer.create(getActivity(), R.raw.test);
        resourcePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                resourcePlayer.release();
                resourcePlayer = null;
                showMsg("资源音频播放完毕");
                currentTask = -1;
            }
        });
        resourcePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                resourcePlayer.start();
                resourcePlayer.seekTo(0);
                currentState = STATE_PLAYING;
                showMsg("开始播放资源音频");

            }
        });
    }

    /**
     * 停止当前正在执行的任务
     */
    private void stopTask(boolean isStopBt) {
        switch (currentTask) {
            case CURRENT_PLAY_LOCAL_AUDIO:
                if (filePlayer != null) {
                    filePlayer.stop();
                    filePlayer.release();
                    filePlayer = null;
                    showMsg("停止播放本地音频");
                    currentTask = -1;
                }
                break;
            case CURRENT_PLAY_RESOURCE_AUDIO:
                if (resourcePlayer != null) {
                    resourcePlayer.stop();
                    resourcePlayer.release();
                    resourcePlayer = null;
                    showMsg("停止播放资源音频");
                    currentTask = -1;

                }
                break;
            case CURRENT_PLAY_ONLINE_AUDIO:
                break;
            case CURRENT_PLAY_RECORD_AUDIO:
                if (audioRecord != null) {
                    audioRecord.stop();
                    audioRecord.release();
                    audioRecord = null;
                    showMsg("停止录音");
                    currentTask = -1;

                }
                break;
            case CURRENT_PLAY_RECORD_MEDIA:
                if (mediaRecorder != null) {
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                    showMsg("停止录音");
                    currentTask = -1;

                }
                break;
            case CURRENT_PLAY_WITH_AUDIOTRACK:
                if (audioTrack != null) {
                    audioTrack.stop();
                    audioTrack.release();
                    audioTrack = null;
                    showMsg("停止播放");
                    currentTask = -1;

                }
                break;
            default:
                if (isStopBt) showMsg("没有正在执行的任务");
                break;
        }
        currentState = STATE_STOP;
    }

    /**
     * 播放本地音频
     */
    private void playAudioForLocal() {
        if (filePlayer != null) {
            if (filePlayer.isPlaying()) {
                filePlayer.pause();
                currentState = STATE_PAUSE;
                showMsg("暂停播放本地音频");
            } else {

                if (currentState == STATE_PAUSE) {
                    filePlayer.start();
                    currentState = STATE_PLAYING;
                    showMsg("继续播放本地音频");
                }
                if (currentState == STATE_STOP) {
                    try {
                        filePlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
        Log.v("SD_PATH", Environment.getExternalStorageDirectory().getPath());
        File file = new File(Environment.getExternalStorageDirectory(), "test.amr");
        if (file.exists()) {
            Log.v("SD_PATH", Uri.fromFile(file).toString());
            filePlayer = MediaPlayer.create(getActivity(), Uri.fromFile(file));
            filePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    filePlayer.release();
                    filePlayer = null;
                    showMsg("本地音频播放完毕");
                    currentTask = -1;

                }
            });
            filePlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    filePlayer.seekTo(0);
                    filePlayer.start();
                    showMsg("开始播放本地音频");
                    currentState = STATE_PLAYING;
                }
            });
        }
    }
}
