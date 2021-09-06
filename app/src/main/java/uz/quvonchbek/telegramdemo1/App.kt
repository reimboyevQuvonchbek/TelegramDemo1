package uz.quvonchbek.telegramdemo1

import android.app.Application
import uz.quvonchbek.telegramdemo1.di.component.ApplicationComponent
import uz.quvonchbek.telegramdemo1.di.component.DaggerApplicationComponent
import uz.quvonchbek.telegramdemo1.di.module.MediaPlayerModule


class App : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.builder()
            .mediaPlayerModule(MediaPlayerModule())
            .build()

    }
}