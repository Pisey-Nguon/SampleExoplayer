package com.example.streaming.streamcomponentonline

import android.Manifest
import android.R
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import com.example.streaming.activity.MainActivity
import com.example.streaming.base.DemoUtil
import com.example.streaming.streamcomponentonline.StreamGenerator.listExoBitrate
import com.example.streaming.streamcomponentonline.StreamGenerator.listExoHeightResolution
import com.example.streaming.streamservice.DemoDownloadService
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.source.*
import com.google.android.exoplayer2.text.TextOutput
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector.ParametersBuilder
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.util.Util
import java.io.*


class StreamLogicHelper(private val activity: MainActivity) {
    private lateinit var playerView: PlayerView
    private lateinit var subtitleView: SubtitleView
    private lateinit var dataSourceFactory: DataSource.Factory
    private lateinit var player: SimpleExoPlayer
    private lateinit var videoMediaSource: MediaSource
    private lateinit var subtitleMediaSource: MediaSource
    private lateinit var trackSelector: DefaultTrackSelector
    private lateinit var trackSelectorParameters: DefaultTrackSelector.Parameters
    private lateinit var lastSeenTrackGroupArray: TrackGroupArray
    private lateinit var downloadTracker: DownloadTracker
    private lateinit var downloadManager: DownloadManager
    private lateinit var listHeightResolution: ArrayList<Int>
    private lateinit var myDownloadHelper: DownloadHelper
//    private var trackKeys: ArrayList<TrackKey> = ArrayList()
//    private var optionsToDownload: ArrayList<String> = ArrayList()

//    private lateinit var downloadedVideoList: ArrayList<Download>
    private val startAutoPlay = true
    private val startWindow = 0
//    val streamUrl = "https://milio-media.s3-ap-southeast-1.amazonaws.com/another-stream/1575271849439-649b8d93c8416bf0e0d269655b40b52f8987e918277699e973a265facc7862af.m3u8"
    val streamUrl = "https://d2cqvl54b1gtkt.cloudfront.net/PRODUCTION/5d85da3fa81ada4c66211a07/media/post/video/1613121797855-35ad0c18-ba1a-4cb1-97b9-fe307269ecbc/1613121797856-7b6ee2437302d02c26d14d325fd7f56a0ce51591e690.mp4"
    val subtitleUrl = "https://cdn.filesend.jp/private/OCoJQeyjKgKvBlqBsRlQFMkx1SYBeREmssE5I1t5QJzlcqSNGDj4Ipct_wq8qomh/The.Wandering.Earth.2019.720p.BluRay.x264.%5BYTS.MX%5D-English.srt"


    fun initComponentStream(){
        initializePlayer()

//        CastButtonFactory.setUpMediaRouteButton(activity.applicationContext,  activity.mMediaRouteButton);
        downloadTracker = DemoUtil.getDownloadTracker( /* context= */activity.applicationContext)
        downloadManager = DemoUtil.getDownloadManager(activity.applicationContext)


        // Start the download service if it should be running but it's not currently.
        // Starting the service in the foreground causes notification flicker if there is no scheduled
        // action. Starting it in the background throws an exception if the app is in the background too
        // (e.g. if device screen is locked).
        try {
            DownloadService.start(activity.applicationContext, DemoDownloadService::class.java)
        } catch (e: IllegalStateException) {
            DownloadService.startForeground(
                    activity.applicationContext,
                    DemoDownloadService::class.java
            )
        }
    }

    private val playerEventListener = object: Player.EventListener{
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            super.onPlayerStateChanged(playWhenReady, playbackState)
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

        override fun onPlayerError(error: ExoPlaybackException) {
            super.onPlayerError(error)
            player.stop()
            player.setMediaSource(videoMediaSource)
            player.playWhenReady = true
        }

        override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
        ) {
            super.onTracksChanged(trackGroups, trackSelections)
            listHeightResolution = trackSelections.listExoHeightResolution()
            val listBitrate = trackSelections.listExoBitrate()


        }

        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
            super.onPlaybackParametersChanged(playbackParameters)
        }

    }

    private val analyticsListener = object: AnalyticsListener{
        override fun onVideoSizeChanged(
                eventTime: AnalyticsListener.EventTime,
                width: Int,
                height: Int,
                unappliedRotationDegrees: Int,
                pixelWidthHeightRatio: Float
        ) {
            super.onVideoSizeChanged(
                    eventTime,
                    width,
                    height,
                    unappliedRotationDegrees,
                    pixelWidthHeightRatio
            )
            val aspectRatio:Float = width.toFloat()/height.toFloat()
            val exoWidth = activity.mViewHelper.getPlayerWidth()
            val exoHeight:Float = exoWidth/aspectRatio
            DistantConverter.setAnimationHeight(
                    view = activity.mViewHelper.getPlayerView(),
                    toHeight = exoHeight.toInt()
            )
        }
    }

    private val playbackPreparer = PlaybackPreparer {

    }

    private val textOutputListener = TextOutput {
        subtitleView.setCues(it)
//        subtitleView.setStyle(CaptionStyleCompat())
        Log.d("checkSub","sub"+it)

    }

    private fun initializePlayer() {

        val renderersFactory =
            DemoUtil.buildRenderersFactory( /* context= */activity.applicationContext, true)

        trackSelectorParameters = ParametersBuilder( /* context= */activity.applicationContext).build()
        trackSelector = DefaultTrackSelector(activity.applicationContext)
        trackSelector.parameters = trackSelectorParameters
        playerView = activity.mViewBinding.playerView
        subtitleView = activity.mViewBinding.subtitleView

        player = SimpleExoPlayer.Builder( /* context= */activity.applicationContext,
                renderersFactory
        ).setTrackSelector(trackSelector).setLoadControl(StreamController.getDefaultController()).build()

        player.addListener(playerEventListener)
        player.addAnalyticsListener(analyticsListener)
//        player.addTextOutput(textOutputListener)
        player.playWhenReady = startAutoPlay
//        player.addAnalyticsListener(EventLogger(trackSelector))


        //init player to view of PlayerView
        playerView.player = player
        playerView.setPlaybackPreparer(playbackPreparer)
        dataSourceFactory = DemoUtil.getDataSourceFactory(activity.applicationContext)
        videoMediaSource = StreamBuilder.buildVideoMediaSource(uri = Uri.parse(streamUrl), dataSourceFactory = dataSourceFactory) ?: return
        subtitleMediaSource = StreamBuilder.buildSubtitleMediaSource(uri = Uri.parse(subtitleUrl),dataSourceFactory = dataSourceFactory)
        val mergingMediaSource = MergingMediaSource(videoMediaSource, subtitleMediaSource)
        player.setMediaSource(mergingMediaSource)
        player.prepare()

    }

    fun requestPermission(){
        ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                168
        )
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
    fun getHeightResolution():ArrayList<Int>{
        return listHeightResolution
    }
    private fun shouldDownload(track: Format): Boolean {
        return track.height != 240 && track.sampleMimeType.equals("video/avc", ignoreCase = true)
    }

    fun fetchDownloadOptions(videoUrl: String) {
        val trackKeys: ArrayList<TrackKey> = ArrayList()
        val pDialog = ProgressDialog(activity)
        pDialog.setTitle(null)
        pDialog.setCancelable(false)
        pDialog.setMessage("Preparing Download Options...")
        pDialog.show()
        val downloadHelper = DownloadHelper.forMediaItem(
                activity.applicationContext,
                MediaItem.fromUri(Uri.parse(streamUrl)),
                DefaultRenderersFactory(
                        activity.applicationContext
                ),
                dataSourceFactory

        )

//        val downloadHelper = DownloadHelper.forHls(
//            activity.applicationContext,
//            Uri.parse(streamUrl),
//            dataSourceFactory,
//            DefaultRenderersFactory(activity.applicationContext)
//        )
        downloadHelper.prepare(object : DownloadHelper.Callback {
            override fun onPrepared(helper: DownloadHelper) {
                // Preparation completes. Now other DownloadHelper methods can be called.
                myDownloadHelper = helper
                for (i in 0 until helper.periodCount) {
                    val trackGroups = helper.getTrackGroups(i)
                    for (j in 0 until trackGroups.length) {
                        val trackGroup = trackGroups[j]
                        for (k in 0 until trackGroup.length) {
                            val track = trackGroup.getFormat(k)
                            if (shouldDownload(track)) {
                                trackKeys.add(TrackKey(trackGroups, trackGroup, track))
                            }
                        }
                    }
                }
                if (pDialog.isShowing) {
                    pDialog.dismiss()
                }
                showDownloadOptionsDialog(myDownloadHelper, trackKeys, videoUrl)
            }

            override fun onPrepareError(helper: DownloadHelper, e: IOException) {}
        })
    }
    private fun showDownloadOptionsDialog(
            helper: DownloadHelper?,
            trackKeyss: List<TrackKey>,
            videoUrl: String
    ) {
        val optionsToDownload: ArrayList<String> = ArrayList()
        var qualityParams: DefaultTrackSelector.Parameters
        if (helper == null) {
            return
        }
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Download Format")
        val checkedItem = 1
        for (i in trackKeyss.indices) {
            val trackKey = trackKeyss[i]
//            val bitrate = trackKey.trackFormat.bitrate.toLong()
//            val getInBytes: Long = bitrate * videoDurationInSeconds / 8
//            val getInMb: String = AppUtil.formatFileSize(getInBytes)
            val videoResoultionDashSize = trackKey.trackFormat.height.toString()
//            activity.mViewHelper.showListDownload(itemListDownload = )
            optionsToDownload.add(i, videoResoultionDashSize)
        }

        // Initialize a new array adapter instance
        val arrayAdapter: ArrayAdapter<*> = ArrayAdapter(
                activity.applicationContext,  // Context
                R.layout.simple_list_item_single_choice,  // Layout
                optionsToDownload // List
        )
        val trackKey = trackKeyss[0]
        qualityParams = trackSelector.parameters.buildUpon()
                .setMaxVideoSize(trackKey.trackFormat.width, trackKey.trackFormat.height)
                .setMaxVideoBitrate(trackKey.trackFormat.bitrate)
                .build()
        builder.setSingleChoiceItems(arrayAdapter, 0) { dialogInterface, i ->
            val trackKey = trackKeyss[i]
            qualityParams = trackSelector.parameters.buildUpon()
                    .setMaxVideoSize(trackKey.trackFormat.width, trackKey.trackFormat.height)
                    .setMaxVideoBitrate(trackKey.trackFormat.bitrate)
                    .build()
        }
        // Set the a;ert dialog positive button
        builder.setPositiveButton(
                "Download",
                DialogInterface.OnClickListener { dialogInterface, which ->
                    for (periodIndex in 0 until helper.periodCount) {
                        val mappedTrackInfo = helper.getMappedTrackInfo( /* periodIndex= */periodIndex)
                        helper.clearTrackSelections(periodIndex)
                        for (i in 0 until mappedTrackInfo.rendererCount) {
//                        TrackGroupArray rendererTrackGroups = mappedTrackInfo.getTrackGroups(i);
                            helper.addTrackSelection(
                                    periodIndex,
                                    qualityParams
                            )
                        }
                    }
                    val downloadRequest = helper.getDownloadRequest(Util.getUtf8Bytes("Hello"))
                    if (downloadRequest.streamKeys.isEmpty()) {
                        // All tracks were deselected in the dialog. Don't start the download.
                        return@OnClickListener
                    }
                    startDownload(downloadRequest)
                    dialogInterface.dismiss()
                })
        val dialog = builder.create()
        dialog.setCancelable(true)
        dialog.show()
    }
    private fun startDownload(downloadRequest: DownloadRequest) {
        DownloadService.sendAddDownload(
                activity.applicationContext,
                DemoDownloadService::class.java,
                downloadRequest,  /* foreground= */
                false
        )
    }

    fun pauseDownload(downloadRequest: DownloadRequest){
        DownloadService.sendPauseDownloads(
                activity.applicationContext,
                DemoDownloadService::class.java,  /* foreground= */
                false
        )
//        downloadManager.addDownload(downloadRequest, Download.STATE_STOPPED)
    }
    fun resumeDownload(downloadRequest: DownloadRequest){
        DownloadService.sendResumeDownloads(
                activity.applicationContext,
                DemoDownloadService::class.java,  /* foreground= */
                false
        )
//        downloadManager.addDownload(downloadRequest, Download.STOP_REASON_NONE)
    }
    fun deleteDownload(downloadRequest: DownloadRequest){
        DownloadService.sendRemoveDownload(
                activity.applicationContext,
                DemoDownloadService::class.java,
                downloadRequest.id,
                /* foreground= */ false
        )
//        downloadManager.removeDownload(downloadRequest.id)
    }
}