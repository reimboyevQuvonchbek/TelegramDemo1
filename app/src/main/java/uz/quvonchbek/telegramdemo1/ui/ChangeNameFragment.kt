package uz.quvonchbek.telegramdemo1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import uz.quvonchbek.telegramdemo1.databinding.FragmentChangeNameBinding
import uz.quvonchbek.telegramdemo1.utilits.USER
import uz.quvonchbek.telegramdemo1.utilits.setFullNameFirebase
import uz.quvonchbek.telegramdemo1.utilits.showToast

class ChangeNameFragment : BaseChangeFragment() {
    lateinit var binding: FragmentChangeNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            FragmentChangeNameBinding.inflate(layoutInflater, container, false)
        binding.settingsChangeInputName.setText(USER.fullName)
        binding.settingsChangeInputSurname.setText(USER.surName)

        return binding.root
    }


    override fun change() {
        if (binding.settingsChangeInputName.text.isNotEmpty()) {
            val name = binding.settingsChangeInputName.text.toString()
            val surname = binding.settingsChangeInputSurname.text.toString()
            setFullNameFirebase(name, surname)
        }else {
                showToast("name kiritilmagan")
            }
        }
    }

