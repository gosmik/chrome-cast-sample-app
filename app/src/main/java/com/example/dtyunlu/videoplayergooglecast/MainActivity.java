package com.example.dtyunlu.videoplayergooglecast;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.libraries.cast.companionlibrary.cast.*;
import com.google.android.libraries.cast.companionlibrary.cast.callbacks.VideoCastConsumer;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.CastException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.NoConnectionException;
import com.google.android.libraries.cast.companionlibrary.cast.exceptions.TransientNetworkDisconnectionException;
import com.google.android.libraries.cast.companionlibrary.widgets.IntroductoryOverlay;
import com.google.android.libraries.cast.companionlibrary.widgets.MiniController;

import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.dtyunlu.videoplayergooglecast.MESSAGE";
    private VideoCastManager mCastManager;
    private VideoCastConsumer mCastConsumer;
    private MenuItem mMediaRouteMenuItem;
    private boolean mIsHoneyCombOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    private Toolbar mToolbar;
    private IntroductoryOverlay mOverlay;
    public String applicationId ;
    private MediaInfo mSelectedMedia;
    private MediaMetadata mMediaMetadata;
    private MiniController mMiniController;
    private String extraMediaName;
    public CastConfiguration options ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        applicationId = getString(R.string.app_id);
        LoadCastSettings ();
        setContentView(R.layout.activity_main);

        VideoView vidView = (VideoView)findViewById(R.id.myVideo);
        String vidAddress = "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4";
        Uri vidUri = Uri.parse(vidAddress);
        vidView.setVideoURI(vidUri);

        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);
        vidView.start();

        mMiniController = (MiniController) findViewById(R.id.miniController1);
        mCastManager.addMiniController(mMiniController);
    }

    public void LoadCastSettings () {
        VideoCastManager.checkGooglePlayServices(this);
        // Build a CastConfiguration object and initialize VideoCastManager
        CastConfiguration options = new CastConfiguration.Builder(applicationId)
                .enableAutoReconnect()
                .enableCaptionManagement()
                .enableDebug()
                .enableLockScreen()
                .enableNotification()
                .enableWifiReconnection()
                .setCastControllerImmersive(true)
                .setLaunchOptions(false, Locale.getDefault())
                .setNextPrevVisibilityPolicy(CastConfiguration.NEXT_PREV_VISIBILITY_POLICY_DISABLED)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_REWIND, false)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE, true)
                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_DISCONNECT, true)
                .setForwardStep(10)
                .build();
        VideoCastManager.initialize(this, options);
        mCastManager = VideoCastManager.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        mCastManager.addMediaRouterButton(menu, R.id.media_route_menu_item);
        return true;
    }

    public void loadVideo()
            throws TransientNetworkDisconnectionException, NoConnectionException {

        mMediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        mMediaMetadata.putString(MediaMetadata.KEY_TITLE, "Demo Video");

        JSONObject jsonObj = null;

        MediaInfo mSelectedMedia = new MediaInfo.Builder(
                "https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4")
                .setContentType("videos/mp4")
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mMediaMetadata)
                .setCustomData(jsonObj)
                .build();


        MediaQueueItem queueItem = new MediaQueueItem.Builder(mSelectedMedia).setAutoplay(
                true).setPreloadTime(20).build();
        MediaQueueItem[] newItemArray = new MediaQueueItem[]{queueItem};

        try {
            mCastManager.queueLoad(newItemArray, 0, MediaStatus.REPEAT_MODE_REPEAT_OFF, null);
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        }
        mCastManager.startVideoCastControllerActivity(this, mSelectedMedia, 0, true);

    }

    public void sendMessage1(View view) {
        Intent intent = new Intent(this, AndroidMediaPlayerExample.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void sendMessage2(View view) {
        Intent intent = new Intent(this, MediaPlayerExampleActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void startCast(View view) {
        try {
            loadVideo();
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopCast(View view) {
        try {
            mCastManager.stop();

            mCastManager.removeMiniController(mMiniController);
        } catch (CastException e) {
            e.printStackTrace();
        } catch (TransientNetworkDisconnectionException e) {
            e.printStackTrace();
        } catch (NoConnectionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume () {
        mCastManager = VideoCastManager.getInstance();
        mCastManager.incrementUiCounter();
        super.onResume();
    }
    @Override
    public void onPause () {
        mCastManager.decrementUiCounter();
        super.onPause();
    }
}
