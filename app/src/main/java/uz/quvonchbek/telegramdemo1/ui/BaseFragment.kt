package uz.quvonchbek.telegramdemo1.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.quvonchbek.telegramdemo1.MainActivity
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.objects.AppDrawer

open class BaseFragment() : Fragment() {




    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mAppDrawer.disableDrawer()
    }

    override fun onStop() {
        super.onStop()


    }
}