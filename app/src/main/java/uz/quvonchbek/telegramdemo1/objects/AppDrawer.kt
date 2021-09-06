package uz.quvonchbek.telegramdemo1.objects

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.mikepenz.materialdrawer.AccountHeader
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader
import com.mikepenz.materialdrawer.util.DrawerImageLoader
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.ui.ContactsFragment
import uz.quvonchbek.telegramdemo1.ui.CreateGroupsFragment
import uz.quvonchbek.telegramdemo1.ui.SettingsFragment
import uz.quvonchbek.telegramdemo1.utilits.USER
import uz.quvonchbek.telegramdemo1.utilits.replaceFragment
import uz.quvonchbek.telegramdemo1.utilits.setUriImage

class AppDrawer(val mainActivity: AppCompatActivity,val toolbar: Toolbar) {
    lateinit var drawer1: Drawer
    lateinit var header: AccountHeader
    lateinit var drawerLayout:DrawerLayout
    lateinit var mCurrentProfil:ProfileDrawerItem

    fun create(){
        initLoader()
        createHeader()
        creatDrawer()

        drawerLayout=drawer1.drawerLayout
    }

    private fun creatDrawer() {
        drawer1= DrawerBuilder()
            .withActivity(mainActivity)
            .withToolbar(toolbar)
            .withSelectedItem(-1)
            .withAccountHeader(header,false)
            .withActionBarDrawerToggle(true)
            .addDrawerItems(
                PrimaryDrawerItem().withIdentifier(101)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("New Group")
                    .withIcon(R.drawable.ic_menu_create_groups),
                PrimaryDrawerItem().withIdentifier(102)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("New Channel")
                    .withIcon(R.drawable.ic_menu_create_channel),
                PrimaryDrawerItem().withIdentifier(103)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("Sekretniy chat")
                    .withIcon(R.drawable.ic_menu_secret_chat),
                PrimaryDrawerItem().withIdentifier(104)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("Contacts")
                    .withIcon(R.drawable.ic_menu_contacts),
                PrimaryDrawerItem().withIdentifier(105)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("Calls")
                    .withIcon(R.drawable.ic_menu_phone),
                PrimaryDrawerItem().withIdentifier(106)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("Saved Messages")
                    .withIcon(R.drawable.ic_menu_favorites),
                PrimaryDrawerItem().withIdentifier(107)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("Settings")
                    .withIcon(R.drawable.ic_menu_settings),
                DividerDrawerItem(),
                PrimaryDrawerItem().withIdentifier(108)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("Invite Friends")
                    .withIcon(R.drawable.ic_menu_invate),
                PrimaryDrawerItem().withIdentifier(109)
                    .withIconTintingEnabled(true)
                    .withSelectable(false)
                    .withName("Telegram Features")
                    .withIcon(R.drawable.ic_menu_help)

            ).withOnDrawerItemClickListener(object :Drawer.OnDrawerItemClickListener{
                override fun onItemClick(
                    view: View?,
                    position: Int,
                    drawerItem: IDrawerItem<*>
                ): Boolean {
                    when(position){
                        1->{
                            replaceFragment(CreateGroupsFragment())

                        }
                        4->{
                            replaceFragment(ContactsFragment())
                        }
                        7->{
                            replaceFragment(SettingsFragment())
                        }
                    }
                    return false
                }
            }).build()
    }

    private fun createHeader() {
        mCurrentProfil=ProfileDrawerItem()
            .withName("${USER.fullName} ${USER.surName}")
            .withEmail(USER.phoneNumber)
            .withIdentifier(200)
            .withIcon(USER.photo_url)
        header= AccountHeaderBuilder()
            .withActivity(mainActivity)
            .withHeaderBackground(R.drawable.header_bacground)
            .addProfiles(
                mCurrentProfil
            ).build()
    }
    fun updateHeader(){
        mCurrentProfil=ProfileDrawerItem()
            .withName("${USER.fullName} ${USER.surName}")
            .withEmail(USER.phoneNumber)
            .withIcon(USER.photo_url)
        header.updateProfile(mCurrentProfil)
    }
    private fun initLoader(){
        DrawerImageLoader.init(object :AbstractDrawerImageLoader(){
            override fun set(imageView: ImageView, uri: Uri, placeholder: Drawable) {
                imageView.setUriImage(uri.toString())
            }
        })
    }
    fun disableDrawer(){
        drawer1.actionBarDrawerToggle?.isDrawerIndicatorEnabled=false
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        toolbar.setNavigationOnClickListener {
            mainActivity.supportFragmentManager.popBackStack()
        }
    }
    fun enableDrawer(){
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        drawer1.actionBarDrawerToggle?.isDrawerIndicatorEnabled=true
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        toolbar.setNavigationOnClickListener {
            drawer1.openDrawer()
        }
    }

}