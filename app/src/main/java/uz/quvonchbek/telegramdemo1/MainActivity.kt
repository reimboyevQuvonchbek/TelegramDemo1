package uz.quvonchbek.telegramdemo1

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.quvonchbek.telegramdemo1.databinding.ActivityMainBinding
import uz.quvonchbek.telegramdemo1.objects.AppDrawer
import uz.quvonchbek.telegramdemo1.ui.MainFragment
import uz.quvonchbek.telegramdemo1.ui.register.PhoneNumberFragment
import uz.quvonchbek.telegramdemo1.utilits.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var mAppDrawer:AppDrawer
    lateinit var toolbar: Toolbar
    lateinit var toolbar_info:View
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        APP_ACTIVITY=this
        setSupportActionBar(binding.toolbar)
        initFirebase()

        initUser(){
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunction()
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        AppState.uptadeState(AppState.ONLINE)
        hideKeyboard()

    }

    override fun onStop() {
        super.onStop()
        AppState.uptadeState(AppState.OFFLINE)
    }


    private fun initFunction() {
        if(AUTH.currentUser!=null){
            replaceFragment(MainFragment())
            mAppDrawer.create()
        }else{
            replaceFragment(PhoneNumberFragment())
        }
    }


    private fun initFields() {
        toolbar=binding.toolbar
        mAppDrawer=AppDrawer(this,toolbar)
        toolbar_info=toolbar.findViewById(R.id.toolbar_info) as View
    }





}