package uz.quvonchbek.telegramdemo1.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.databinding.FragmentSettingsBinding
import uz.quvonchbek.telegramdemo1.utilits.*

class SettingsFragment : BaseFragment() {
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding= FragmentSettingsBinding.inflate(inflater, container, false)
        binding.settingsBtnChangeUsername.setOnClickListener{
            replaceFragment(ChangeUsernameFragment())
        }
        setHasOptionsMenu(true)
        initfields()
        binding.settingsBtnChangeBio.setOnClickListener{
            replaceFragment(ChangeBioFragment())
        }
        binding.settingsAddPhoto.setOnClickListener {
            changePhotoProfil()
        }
        return binding.root
    }

    private fun changePhotoProfil() {
        CropImage.activity()
            .setAspectRatio(1,1)
            .setRequestedSize(600,600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY,this)
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.toolbar.title="Settings"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== RESULT_OK && data!=null){
            val uri= CropImage.getActivityResult(data).uri
            val path= REF_STORAGE_ROOT.child(FOLDER_PROFIL_IMAGE).child(CURRENT_UID)
//            path.putFile(uri).addOnCompleteListener{task1->
//                if(task1.isSuccessful){
//                    path.downloadUrl.addOnCompleteListener { task2->
//                        if(task2.isSuccessful){
//                            val p_uri=task2.result.toString()
//                            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
//                                .child(CHILD_PHOTO_URL).setValue(p_uri).addOnCompleteListener { task3->
//                                    if(task3.isSuccessful){
//                                        USER.photoUrl=p_uri
//                                        showToast("ok")
//                                        binding.settingsUserAvatarPhoto.setUriImage(p_uri)
//                                    }
//
//                                }
//                        }
        //             }
//                }
//            }
            putFileStorage(uri,path){
                getUrlFromStorage(path){
                    putUrlDatabase(it){
                        USER.photo_url=it
                        showToast("ok")
                        binding.settingsUserAvatarPhoto.setUriImage(it)
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

    }

    private fun initfields() {
        binding.settingsBioText.text= USER.bio
        binding.settingsFullname.text= "${USER.fullName} ${USER.surName}"
        binding.settingsPhoneNumber.text= USER.phoneNumber
        binding.settingsUsername.text= USER.username
        binding.settingsUserAvatarPhoto.setUriImage(USER.photo_url)
        binding.settingsStatus.text= USER.state
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        activity?.menuInflater?.inflate(R.menu.settings_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings_menu_exit->{
                AppState.uptadeState(AppState.OFFLINE)
                AUTH.signOut()
                reStartActivity()
            }
            R.id.settings_menu_edit_name->{
                replaceFragment(ChangeNameFragment())
            }
        }
        return true
    }

}