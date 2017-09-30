package com.hay.developer.remoteytcontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hay.developer.R;
import com.hay.developer.remoteytcontroller.model.Video;
import com.hay.developer.remoteytcontroller.utils.Constant;
import com.hay.developer.remoteytcontroller.utils.LogUtils;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by Hay Tran on 16-Aug-17.
 */

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
    private static final String TAG = "MainActivity";
    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView youTubeView;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    public static YouTubePlayer player;
    private DatabaseReference mData = FirebaseDatabase.getInstance().getReference();
    private LogUtils mLogUtils;
    private Video mVideo = Video.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        init();
        addEvent();
    }

    private void addControls() {
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    private void init() {
        /* set up for auto restart when app crash*/
//        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(this));
//        if (getIntent().getBooleanExtra("crash", false)) {
//            mLogUtils.crash("Already restart app after crashing");
//        }

        mVideo.setmLink(Constant.mDefaultLink);
        /* initialize youtube */
        youTubeView.initialize(Constant.YOUTUBE_API_KEY, this);
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

        mLogUtils = new LogUtils(getApplicationContext());

    }

    private void addEvent() {
        mData.child("Link").addValueEventListener(mValueListener);
    }

    private ValueEventListener mValueListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mVideo.setmLink(dataSnapshot.getValue().toString());
            mLogUtils.info("onDataChange with link: " + mVideo.getmLink());
            if (mVideo.getmLink().contains("https://www.youtube.com/watch")) {
                mVideo.setmLink(mVideo.getmLink().substring(32));
            }
            try {
                if (player != null ) {
                    player.cueVideo(mVideo.getmLink());
                    mLogUtils.info("Loading new link...");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
        this.player.setFullscreen(true);
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        if (!wasRestored) {
            player.cueVideo(mVideo.getmLink()); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
        mLogUtils.success("onInitializationSuccess");
    }

    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
        mLogUtils.error("onInitializationFailure");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Constant.YOUTUBE_API_KEY, this);
        }
        mLogUtils.info("onActivityResult");
    }

    protected Provider getYouTubePlayerProvider() {
        return youTubeView;
    }

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
            mLogUtils.playBackStatus("onPlaying");
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            mLogUtils.playBackStatus("onPaused");
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            mLogUtils.playBackStatus("onStopped");
            if (player.isPlaying() == false) {
                player.play();
            }
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
            mLogUtils.playBackStatus("onBuffering");
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
            mLogUtils.playBackStatus("onSeekTo");
        }
    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
            mLogUtils.playBackStatus("onLoading");
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
            Log.d(TAG, "onLoaded: " + s);
            player.play();
            mVideo.setDurationMilisecond(player.getDurationMillis());
            mLogUtils.playBackStatus("onLoaded");
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
            mLogUtils.playBackStatus("onAdStarted");
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
            mLogUtils.playBackStatus("onVideoStarted");
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
            mLogUtils.playBackStatus("onVideoEnded");
            player.play();
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
            mLogUtils.error("onError: " + errorReason.toString());
        }
    }
}
