package com.example.streaming.streamcomponentoffline

import android.net.Uri
import android.util.Log
import com.example.streaming.activity.OfflineVideoActivity
import com.example.streaming.base.DemoUtil
import com.example.streaming.streamcomponentonline.ExoPlayState
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter

class OfflineLogicHelper(private val activity: OfflineVideoActivity) {
    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var playerView: PlayerView
    private lateinit var dataSourceFactory:DataSource.Factory
    private val offlineVideoLink = "https://milio-media.s3-ap-southeast-1.amazonaws.com/another-stream/1575271849439-649b8d93c8416bf0e0d269655b40b52f8987e918277699e973a265facc7862af.m3u8"

    fun initExoplayer() {
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()

        val trackSelector: TrackSelector = DefaultTrackSelector(activity.applicationContext)
//        val renderersFactory = DefaultRenderersFactory(
//            activity.applicationContext, null,
//            DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
//        )

        val renderersFactory: RenderersFactory = DemoUtil.buildRenderersFactory( /* context= */
            activity.applicationContext,
            true
        )
//        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
//            activity.applicationContext,
//            renderersFactory,
//            trackSelector,
//            StreamController.getDefaultController()
//        )

        simpleExoPlayer = SimpleExoPlayer.Builder( /* context= */activity.applicationContext, renderersFactory)
            .setTrackSelector(trackSelector)
            .build()
        playerView = activity.mViewBinding.playerView
        playerView.useController = true
        playerView.requestFocus()
        playerView.player = simpleExoPlayer
        simpleExoPlayer.repeatMode = Player.REPEAT_MODE_OFF
        simpleExoPlayer.playWhenReady = true //run file/link when ready to play.
//        simpleExoPlayer.setVideoDebugListener(this) //for listening to resolution change and  outputing the resolution

        val downloadRequest: DownloadRequest? = DemoUtil.getDownloadTracker(activity.applicationContext).getDownloadRequest(Uri.parse(offlineVideoLink))
        dataSourceFactory = DemoUtil.getDataSourceFactory(activity.applicationContext)
        val mediaSource = DownloadHelper.createMediaSource(
            downloadRequest ?: return,
            dataSourceFactory
        )

//         mediaSource = buildMediaSource(Uri.parse(offlineVideoLink));
        simpleExoPlayer.prepare(mediaSource, false, true)
        simpleExoPlayer.addListener(object : Player.EventListener {
            override fun onTimelineChanged(timeline: Timeline, manifest: Any?, reason: Int) {}
            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
                Log.v(
                    OfflineVideoActivity().TAG,
                    "Listener-onTracksChanged..."
                )
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                Log.v(
                    OfflineVideoActivity().TAG,
                    "Listener-onLoadingChanged...isLoading:$isLoading"
                )
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                Log.d("state", playbackState.toString())
                when (playbackState) {
                    Player.STATE_READY -> {
                        Log.d("state", "STATE_READY")
                        if (playerView.player?.playWhenReady == true) {
                            activity.mViewHelper.setupUIButtonPlay(
                                activity = activity,
                                exoPlayState = ExoPlayState.EXO_PAUSE
                            )
                        } else {
                            activity.mViewHelper.setupUIButtonPlay(
                                activity = activity,
                                exoPlayState = ExoPlayState.EXO_PLAY
                            )
                        }
                    }
                    Player.STATE_BUFFERING -> {
                        Log.d("state", "STATE_BUFFERING")
                        activity.mViewHelper.setupUIButtonPlay(
                            activity = activity,
                            exoPlayState = ExoPlayState.EXO_HIDE
                        )
                    }
                    Player.STATE_IDLE -> {
                        Log.d("state", "STATE_IDLE")
                        activity.mViewHelper.setupUIButtonPlay(
                            activity = activity,
                            exoPlayState = ExoPlayState.EXO_HIDE
                        )
                    }
                    Player.STATE_ENDED -> {
                        Log.d("state", "STATE_ENDED")
                        activity.mViewHelper.setupUIButtonPlay(
                            activity = activity,
                            exoPlayState = ExoPlayState.EXO_REPLAY
                        )
                    }
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                Log.v(
                    OfflineVideoActivity().TAG,
                    "Listener-onRepeatModeChanged..."
                )
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
            override fun onPlayerError(error: ExoPlaybackException) {
                Log.v(OfflineVideoActivity().TAG, "Listener-onPlayerError...")
                simpleExoPlayer.stop()
                simpleExoPlayer.prepare(mediaSource)
                simpleExoPlayer.setPlayWhenReady(true)
            }

            override fun onPositionDiscontinuity(reason: Int) {}
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                Log.v(
                    OfflineVideoActivity().TAG,
                    "Listener-onPlaybackParametersChanged..."
                )
            }

            /**
             * Called when all pending seek requests have been processed by the simpleExoPlayer. This is guaranteed
             * to happen after any necessary changes to the simpleExoPlayer state were reported to
             * [.onPlayerStateChanged].
             */
            override fun onSeekProcessed() {}
        })

    }

    fun startPlayer(){
        if (playerView.player?.isPlaying == true){
            activity.mViewHelper.setupUIButtonPlay(
                activity = activity,
                exoPlayState = ExoPlayState.EXO_PAUSE
            )
            playerView.player?.playWhenReady = false
        }else{
            activity.mViewHelper.setupUIButtonPlay(
                activity = activity,
                exoPlayState = ExoPlayState.EXO_PLAY
            )
            playerView.player?.playWhenReady = true
        }
    }

}