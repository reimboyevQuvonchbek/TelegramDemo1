package uz.quvonchbek.telegramdemo1.adapters.holder

import android.media.MediaPlayer
import javax.inject.Inject

class MediaplayerServise @Inject constructor(var mediaPlayer: MediaPlayer) {

    fun getMediaPlayer1():MediaPlayer{
        return mediaPlayer
    }
}