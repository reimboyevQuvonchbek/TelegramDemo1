package uz.quvonchbek.telegramdemo1.ui

import android.os.Bundle
import android.view.*
import uz.quvonchbek.telegramdemo1.databinding.FragmentChangeUsernameBinding
import uz.quvonchbek.telegramdemo1.utilits.*

class ChangeUsernameFragment : BaseChangeFragment() {
    lateinit var binding: FragmentChangeUsernameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentChangeUsernameBinding.inflate(inflater, container, false)
        binding.settingsChangeInputUsername.setText(USER.username)
        return binding.root
    }

    override fun change() {
        if (binding.settingsChangeInputUsername.text.isNotEmpty()) {
            val name=binding.settingsChangeInputUsername.text.toString()
            REF_DATABASE_ROOT.child(NODE_USERNAMES).addListenerForSingleValueEvent(AppValueEventListener(){
                if(it.hasChild(name)){
                    showToast("Bunday username bor")
                }else{
                    changeUsername(name)
                }
            })
        } else {
            showToast("name kiritilmagan")
        }
    }
}