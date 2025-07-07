package com.example.rtsp_player;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log; // 用于日志输出
import android.widget.Toast; // 用于显示提示信息

import androidx.media3.ui.PlayerView; // ✅ 使用 AndroidX Media3 的 PlayerView
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Metadata;
import java.util.Collections;
import androidx.media3.common.Player; // 用于播放器状态监听
import androidx.media3.exoplayer.ExoPlaybackException; // ✅ 添加缺失的导入

import android.content.pm.ActivityInfo;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    // 替换为你自己的 RTSP 地址
//    private static final String RTSP_URL = "rtsp://192.168.43.132:8554/test";
    // 使用 rtspt:// 强制使用 TCP 协议
    private static final String RTSP_URL = "rtspt://192.168.3.64:8554/output";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化播放器组件
        playerView = findViewById(R.id.player_view);

        // 设置全屏模式
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

        // 初始化 ExoPlayer
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        playerView.setUseController(false); // 确保控制器不可见

        // 使用标准 rtsp:// 协议，让 ExoPlayer 自动协商 TCP/UDP
//        MediaItem mediaItem = MediaItem.fromUri("rtsp://192.168.3.64:8554/test");
        MediaItem mediaItem = MediaItem.fromUri("rtsp://192.168.1.102:8553/output");
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play(); // 自动开始播放

        // 可选：设置播放准备就绪后自动播放
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.prepare();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}