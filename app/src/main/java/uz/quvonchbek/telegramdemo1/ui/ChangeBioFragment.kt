package uz.quvonchbek.telegramdemo1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.quvonchbek.telegramdemo1.databinding.FragmentChangeBioBinding
import uz.quvonchbek.telegramdemo1.utilits.*

class ChangeBioFragment : BaseChangeFragment() {

    lateinit var binding: FragmentChangeBioBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentChangeBioBinding.inflate(inflater, container, false)
        binding.settingsChangeInputBio.setText(USER.bio)
        return binding.root
    }

    override fun change() {
        val bio=binding.settingsChangeInputBio.text.toString()
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_BIO).setValue(bio).addOnCompleteListener {
            parentFragmentManager.popBackStack()
        }

    }


}