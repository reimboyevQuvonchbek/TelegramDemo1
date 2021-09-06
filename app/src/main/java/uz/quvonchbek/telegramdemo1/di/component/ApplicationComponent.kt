package uz.quvonchbek.telegramdemo1.di.component

import dagger.Component
import uz.quvonchbek.telegramdemo1.MainActivity
import uz.quvonchbek.telegramdemo1.di.module.MediaPlayerModule
import uz.quvonchbek.telegramdemo1.ui.UserChatFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [MediaPlayerModule::class])
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)
    fun inject(chatFragment: UserChatFragment)
}