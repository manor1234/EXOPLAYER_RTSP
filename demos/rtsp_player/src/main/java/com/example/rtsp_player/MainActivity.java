package com.example.rtsp_player;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // 添加 EditText 的导入语句
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
    private static final String RTSP_URL = "rtsp://192.168.3.64:8554/test";


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

        // 查找 "视频流" 按钮
        Button buttonVideoStream = findViewById(R.id.button_video_stream);

        // 设置按钮点击监听器
        buttonVideoStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRtspInputDialog();
            }
        });

        // 可选：设置播放准备就绪后自动播放
        player.setPlayWhenReady(true);
    }

    /**
     * 显示输入对话框以获取 RTSP 地址
     */
    private void showRtspInputDialog() {
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_URI);
        input.setHint("输入 RTSP 地址");

        new android.app.AlertDialog.Builder(this)
            .setTitle("配置 RTSP 视频流地址")
            .setView(input)
            .setPositiveButton("播放", new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int whichButton) {
                    String rtspUrl = RTSP_URL;
                    rtspUrl = input.getText().toString();
                    if (!rtspUrl.isEmpty()) {
                        MediaItem mediaItem = MediaItem.fromUri(rtspUrl);
                        player.setMediaItem(mediaItem);
                        player.prepare();
                        player.play(); // 开始播放
                    } else {
                        Toast.makeText(MainActivity.this, "请输入有效的 RTSP 地址", Toast.LENGTH_SHORT).show();
                    }
                }
            })
            .setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
                public void onClick(android.content.DialogInterface dialog, int which) {
                    // 用户取消操作
                }
            })
            .show();
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