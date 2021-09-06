package uz.quvonchbek.telegramdemo1.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import uz.quvonchbek.telegramdemo1.MainActivity
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.adapters.MainListAdapter
import uz.quvonchbek.telegramdemo1.databinding.FragmentMainBinding
import uz.quvonchbek.telegramdemo1.models.ChatModel
import uz.quvonchbek.telegramdemo1.models.Message
import uz.quvonchbek.telegramdemo1.utilits.*


class MainFragment : Fragment(R.layout.fragment_main) {
    lateinit var refMainList:DatabaseReference
    lateinit var binding: FragmentMainBinding
    lateinit var mainList:ArrayList<ChatModel>
    lateinit var mainListAdapter: MainListAdapter
    override fun onResume() {
        super.onResume()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMainBinding.inflate(inflater, container, false)
        initFields()
        initMainListRv()
        return binding.root
    }

    private fun initMainListRv() {
        refMainList.addListenerForSingleValueEvent(AppValueEventListener{
            val children = it.children
            children.forEach { data1->
                val chatModel=ChatModel()
                chatModel.id = data1.child(CHILD_ID).value as String
                chatModel.type=data1.child(CHILD_TYPE).value as String
                val lastmessage=Message()
                lastmessage.message_type=data1.child("$CHILD_LASTMESSAGE/$CHILD_TYPE").value as String
                lastmessage.timeStamp=data1.child("$CHILD_LASTMESSAGE/$CHILD_TIMESTAMP").value as Any
                if(lastmessage.message_type== MESSAGE_TYPE_TEXT){
                    lastmessage.text=data1.child("$CHILD_LASTMESSAGE/$CHILD_TEXT").value as String
                }

                chatModel.lastMessage=lastmessage

                var count=0
                REF_DATABASE_ROOT.child(NODE_USERS).child(chatModel.id)
                    .addListenerForSingleValueEvent(AppValueEventListener{data2->
                        chatModel.file_url=data2.child(CHILD_PHOTO_URL).value as String
                        chatModel.state=data2.child(CHILD_STATE).value as String
                        chatModel.name=data2.child(CHILD_FULNAME).value as String
                        REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(chatModel.id)
                            .addListenerForSingleValueEvent(AppValueEventListener{ data3->
                                val mlist = data3.children
                                mlist.forEach{m->
                                    if(m.child(CHILD_FROM).value as String == chatModel.id &&
                                        !(m.child(CHILD_READ).value as Boolean)){
                                        count++
                                    }
                                }
                             //   chatModel.lastMessage=mlist.first().getValue(Message::class.java) ?:Message()
                                chatModel.countMessage=count
                                mainList.add(chatModel)
                                mainListAdapter.setList(mainList)
                            })
                    })
                count=0
            }

        })

    }

    private fun initFields() {
        refMainList= REF_DATABASE_ROOT.child(NODE_MAIN_LIST).child(CURRENT_UID)
        mainList= ArrayList()
        mainListAdapter= MainListAdapter()
        binding.userChatRv.adapter=mainListAdapter

    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.toolbar.title="Telegram"
        (activity as MainActivity).mAppDrawer.enableDrawer()

    }


}