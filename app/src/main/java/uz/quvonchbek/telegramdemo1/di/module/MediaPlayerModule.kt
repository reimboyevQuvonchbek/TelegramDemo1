package uz.quvonchbek.telegramdemo1.di.module

import android.media.MediaPlayer
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MediaPlayerModule {
    @Singleton
    @Provides
    fun provideMediaPlayer():MediaPlayer{
        return MediaPlayer()
    }
}