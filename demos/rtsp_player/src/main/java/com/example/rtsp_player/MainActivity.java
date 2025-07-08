package com.example.rtsp_player;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.app.AlertDialog;
import androidx.media3.common.PlaybackException;
import android.widget.LinearLayout;
import android.util.Log;
import android.widget.Toast;

import androidx.media3.ui.PlayerView;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Metadata;
import java.util.Collections;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlaybackException;

import android.content.pm.ActivityInfo;

public class MainActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView; // 添加此行：声明 playerView
    private String rtspUrl = "rtsp://192.168.3.64:8554/test"; // 将其改为成员变量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化播放器
        player = new ExoPlayer.Builder(this).build();
        
        // 确保 playerView 被正确初始化
        playerView = findViewById(R.id.player_view);
        if (playerView == null) {
            Log.e("MainActivity", "playerView 为 null，请检查布局文件中的 ID 是否匹配");
        } else {
            playerView.setPlayer(player);
        }

        // 添加播放错误监听器
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerErrorChanged(PlaybackException error) {
                if (error != null) {
                    Toast.makeText(MainActivity.this, "播放错误: " + error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    Log.e("ExoPlayer", "播放错误", error);
                }
            }
        });

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
        // 加载自定义布局
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_rtsp_input, null);
        EditText input = dialogView.findViewById(R.id.edit_text_rtsp);
        Button buttonPlay = dialogView.findViewById(R.id.button_play);
        Button buttonCancel = dialogView.findViewById(R.id.button_cancel);

        // 声明并初始化 rtspUrl
        String rtspUrl = "rtsp://192.168.3.64:8554/test"; // 默认地址，也可以从成员变量获取
        input.setText(rtspUrl);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        // 设置点击外部不可取消
        dialog.setCancelable(false);

        // 播放按钮逻辑
        buttonPlay.setOnClickListener(v -> {
            String url = input.getText().toString();
            if (!url.isEmpty()) {
                MediaItem mediaItem = MediaItem.fromUri(url);
                player.setMediaItem(mediaItem);
                player.prepare();
                player.play();
                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "地址不能为空", Toast.LENGTH_SHORT).show();
            }
        });

        // 取消按钮逻辑
        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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