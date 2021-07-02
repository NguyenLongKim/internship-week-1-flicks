package com.example.flicks


import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.flicks.databinding.ActivityVideoBinding
import com.example.flicks.models.Item
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer


class VideoActivity : YouTubeBaseActivity() {
    companion object {
        private const val youtube_api_key = "AIzaSyBbXkcIdPg_Ydoksnwq6tDg-P873TuDgpo"
    }

    private lateinit var activityVideoBinding: ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityVideoBinding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        val videoKey: String? = intent.getStringExtra("video_key")
        val item = intent.getParcelableExtra<Item>("item")
        activityVideoBinding.tvTitle.text = item!!.title
        activityVideoBinding.tvOverview.text = item.overview
        activityVideoBinding.tvReleaseDate.text = item.release_date
        activityVideoBinding.rtBar.rating = item.vote_average / 10 * 5
        activityVideoBinding.player.initialize(youtube_api_key,
            object : YouTubePlayer.OnInitializedListener {
                override fun onInitializationSuccess(
                    provider: YouTubePlayer.Provider,
                    youTubePlayer: YouTubePlayer, b: Boolean
                ) {
                    if (item.vote_average > 8.0) {
                        youTubePlayer.loadVideo(videoKey)
                    } else {
                        youTubePlayer.cueVideo(videoKey)
                    }
                }

                override fun onInitializationFailure(
                    provider: YouTubePlayer.Provider,
                    youTubeInitializationResult: YouTubeInitializationResult
                ) {}
            })
    }
}