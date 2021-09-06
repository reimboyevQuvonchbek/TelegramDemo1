package uz.quvonchbek.telegramdemo1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import uz.quvonchbek.telegramdemo1.databinding.ContactItemBinding
import uz.quvonchbek.telegramdemo1.databinding.FragmentContactsBinding
import uz.quvonchbek.telegramdemo1.models.ContactModel
import uz.quvonchbek.telegramdemo1.models.User
import uz.quvonchbek.telegramdemo1.utilits.*

class ContactsFragment : BaseFragment() {
    lateinit var binding: FragmentContactsBinding
    private lateinit var mAdapter: FirebaseRecyclerAdapter<ContactModel, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private  var mapListeners = hashMapOf<DatabaseReference,AppValueEventListener>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentContactsBinding.inflate(inflater, container, false)
        initRV()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.toolbar.title="Contact"
    }

    class ContactsHolder(var contactItemBinding: ContactItemBinding)
        :RecyclerView.ViewHolder(contactItemBinding.root) {
        fun onBind(user: User,name:String){
            contactItemBinding.contactPhoto.setUriImage(user.photo_url)
            contactItemBinding.contactFullname.text=name
            contactItemBinding.contactStatus.text=user.state

        }
    }



    private fun initRV() {
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)

        //Настройка для адаптера, где указываем какие данные и откуда получать
        val options = FirebaseRecyclerOptions.Builder<ContactModel>()
            .setQuery(mRefContacts, ContactModel::class.java)
            .build()

        mAdapter=object :FirebaseRecyclerAdapter<ContactModel,ContactsHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                return ContactsHolder(ContactItemBinding.inflate(layoutInflater,parent,false))
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: ContactModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)
                mRefUsersListener = AppValueEventListener {
                    val user = it.getValue(User::class.java) ?: User()
                    holder.onBind(user,model.fullName)
                    holder.contactItemBinding.root.setOnClickListener{
                        replaceFragment(UserChatFragment.newInstance(model))
                    }
                }
                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers] = mRefUsersListener
            }

        }

        binding.contactRv.adapter = mAdapter
        mAdapter.startListening()
    }
    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
        println()
    }

}