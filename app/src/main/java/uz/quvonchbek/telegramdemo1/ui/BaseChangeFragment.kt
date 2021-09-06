package uz.quvonchbek.telegramdemo1.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import uz.quvonchbek.telegramdemo1.MainActivity
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.utilits.APP_ACTIVITY
import uz.quvonchbek.telegramdemo1.utilits.hideKeyboard

open class BaseChangeFragment : Fragment() {
    override fun onStart() {
        super.onStart()
        (activity as MainActivity).mAppDrawer.disableDrawer()
        setHasOptionsMenu(true)
    }

    override fun onStop() {
        super.onStop()
        hideKeyboard()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.settings_confirm_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_change -> {
                change()
            }
        }
        return true
    }

    open fun change() {
        TODO("Not yet implemented")
    }

}