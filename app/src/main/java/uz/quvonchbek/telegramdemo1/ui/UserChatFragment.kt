package uz.quvonchbek.telegramdemo1.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.mikepenz.iconics.Iconics.applicationContext
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.quvonchbek.telegramdemo1.App
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.adapters.ChatMessageAdapter
import uz.quvonchbek.telegramdemo1.adapters.holder.MediaplayerServise
import uz.quvonchbek.telegramdemo1.databinding.FragmentUserChatBinding
import uz.quvonchbek.telegramdemo1.models.ContactModel
import uz.quvonchbek.telegramdemo1.models.Message
import uz.quvonchbek.telegramdemo1.models.User
import uz.quvonchbek.telegramdemo1.utilits.*
import javax.inject.Inject

class UserChatFragment : BaseFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).applicationComponent.inject(this)
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }
    @Inject lateinit var mediaplayerServise: MediaplayerServise
    private lateinit var binding: FragmentUserChatBinding
    private lateinit var receivedUser: User
    private lateinit var contact1: ContactModel
    private lateinit var listenerInfoToolbar: AppValueEventListener
    private lateinit var listenerChat: AppChildEventListener
    private lateinit var refUser: DatabaseReference
    private lateinit var refChatUser: DatabaseReference
    private lateinit var toolbarFullName: TextView
    private lateinit var toolbarStatus: TextView
    private lateinit var toolbarAvatar: CircleImageView
    private lateinit var adapter: ChatMessageAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var appVoiceRecorder: AppVoiceRecorder


    private var isScrolling = false
    private var isSmoothScrollToPosition = true
    private var count = 15
    private var listListenerChat = mutableListOf<AppChildEventListener>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserChatBinding.inflate(inflater, container, false)
        contact1 = arguments?.getSerializable("contact") as ContactModel
        APP_ACTIVITY.toolbar_info.visibility = View.VISIBLE
        initFields()
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        listenerInfoToolbar = AppValueEventListener {
            receivedUser = it.getUser()
            initToolbarInfo()
        }
        refUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact1.id)
        refUser.addValueEventListener(listenerInfoToolbar)
        binding.chatBtnSendMessage.setOnClickListener {
            val textMessage = binding.chatInputMessage.text.toString()
            isSmoothScrollToPosition = true
            if(textMessage.isNotEmpty()){
                sendMessage(textMessage,contact1.id) {
                    binding.chatInputMessage.setText("")
                    setMainList(contact1.id,"chat")
                }
            }

        }
        CoroutineScope(Dispatchers.IO).launch {
            binding.chatBtnVoice.setOnTouchListener { v, event ->
                if(checkPermission(RECORD_AUDIO)){
                    if(event.action==MotionEvent.ACTION_DOWN){
                        binding.voiceIcon.setColorFilter(ContextCompat.getColor(APP_ACTIVITY,R.color.primary))
                        val mkey= getMessageKey(contact1.id)
                        appVoiceRecorder.startRecord(mkey)
                    }else if(event.action==MotionEvent.ACTION_UP){
                        binding.voiceIcon.colorFilter = null
                        appVoiceRecorder.stopRecord { file, messageKey ->
                            uploadFileStorage(Uri.fromFile(file),messageKey,contact1.id,
                                MESSAGE_TYPE_VOICE)
                        }
                    }
                }
                true
            }
        }

        binding.photoMessage.setOnClickListener {
            CropImage.activity()
                .setAspectRatio(1,1)
                .setRequestedSize(600,600)
                .start(APP_ACTIVITY,this)
        }
        initChatRv()
        initEdittext()
    }




    private fun initEdittext() {
        binding.chatInputMessage.addTextChangedListener(AppTextWatcher{
                if(binding.chatInputMessage.text.isNotEmpty()){
                    binding.chatBtnVoice.visibility=View.GONE
                    binding.photoMessage.visibility=View.GONE
                    binding.chatBtnSendMessage.visibility=View.VISIBLE
                }else{
                    binding.chatBtnVoice.visibility=View.VISIBLE
                    binding.photoMessage.visibility=View.VISIBLE
                    binding.chatBtnSendMessage.visibility=View.GONE

                }
        })
    }

    private fun initChatRv() {
        adapter = ChatMessageAdapter()
        binding.chatRecycleView.adapter = adapter
        layoutManager= LinearLayoutManager(this.context)
        binding.chatRecycleView.layoutManager=layoutManager
        binding.chatRecycleView.setHasFixedSize(true)
        binding.chatRecycleView.isNestedScrollingEnabled=false
        listenerChat = AppChildEventListener {
            val sms = Message()
            sms.isRead = it.child("isRead").value as Boolean
            sms.id = it.child("id").value as String
            sms.from = it.child("from").value as String
            sms.message_type = it.child("message_type").value as String
            sms.timeStamp = it.child("timeStamp").value as Any
            if(sms.message_type=="text"){
                sms.text = it.child("text").value as String
            }else if(sms.message_type=="image"){
                sms.file_url=it.child(CHILD_FILE_URL).value as String
            }

            if (isSmoothScrollToPosition) {
                adapter.addItemToBottom(sms) {
                }
                binding.chatRecycleView.smoothScrollToPosition(adapter.itemCount)
            } else {
                binding.swipe.isRefreshing = false
                adapter.addItemToTop(sms) {
                }
            }
        }
        binding.chatRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isScrolling && dy < 0 && layoutManager.findFirstVisibleItemPosition()<=3) {
                    updateData()
                }
            }
        })

        binding.swipe.setOnRefreshListener { updateData() }
        refChatUser =
            REF_DATABASE_ROOT.child("$NODE_MESSAGES/$CURRENT_UID/${contact1.id}")
        refChatUser.limitToLast(count).addChildEventListener(listenerChat)
        listListenerChat.add(listenerChat)
    }

    private fun updateData() {
        isSmoothScrollToPosition = false
        isScrolling = false
        count += 10
        refChatUser.limitToLast(count).addChildEventListener(listenerChat)
        listListenerChat.add(listenerChat)
    }


    override fun onPause() {
        super.onPause()
        APP_ACTIVITY.toolbar_info.visibility = View.GONE
        hideKeyboard()
        appVoiceRecorder.releaseRecorder()
        refUser.removeEventListener(listenerInfoToolbar)
        listListenerChat.forEach {
            refChatUser.removeEventListener(it)
        }
    }

    private fun initToolbarInfo() {
        toolbarFullName.text = contact1.fullName
        toolbarAvatar.setUriImage(receivedUser.photo_url)
        toolbarStatus.text = receivedUser.state
    }

    private fun initFields() {
        appVoiceRecorder= AppVoiceRecorder()
        toolbarFullName =
            APP_ACTIVITY.toolbar_info.findViewById(R.id.toolbar_user_fullname)
        toolbarStatus =
            APP_ACTIVITY.toolbar_info.findViewById(R.id.toolbar_user_status)
        toolbarAvatar =
            APP_ACTIVITY.toolbar_info.findViewById(R.id.toolbar_avatar_photo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==
            Activity.RESULT_OK && data!=null){
            val uri= CropImage.getActivityResult(data).uri
            val key = getMessageKey(contact1.id)
            uploadFileStorage(uri,key,contact1.id, MESSAGE_TYPE_IMAGE)
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(contact: ContactModel) =
            UserChatFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("contact", contact)
                }
            }
    }
}