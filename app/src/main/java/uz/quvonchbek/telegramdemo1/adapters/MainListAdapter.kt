package uz.quvonchbek.telegramdemo1.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.quvonchbek.telegramdemo1.databinding.ItemMainListBinding
import uz.quvonchbek.telegramdemo1.models.ChatModel
import uz.quvonchbek.telegramdemo1.models.ContactModel
import uz.quvonchbek.telegramdemo1.ui.UserChatFragment
import uz.quvonchbek.telegramdemo1.utilits.*

class MainListAdapter():RecyclerView.Adapter<MainListAdapter.VhMainListItem>() {
    private var mainList:ArrayList<ChatModel> = ArrayList()
    fun setList(list:ArrayList<ChatModel>){
        mainList=list
        mainList.sortBy { it.lastMessage.timeStamp.toString() }

        notifyDataSetChanged()
    }

    inner class VhMainListItem(var itemMainListBinding: ItemMainListBinding)
        :RecyclerView.ViewHolder(itemMainListBinding.root){
            fun onBind(chatModel: ChatModel){
                itemMainListBinding.mainlistItemPhoto.setUriImage(chatModel.file_url)
                if(chatModel.countMessage>0){
                    itemMainListBinding.mainlistMessageCount.text=chatModel.countMessage.toString()
                    itemMainListBinding.cardCount.visibility=View.VISIBLE
                }else{
                    itemMainListBinding.cardCount.visibility=View.INVISIBLE
                }
                if(chatModel.lastMessage.message_type== MESSAGE_TYPE_TEXT){
                    itemMainListBinding.mainlistLastmessageText.text= chatModel.lastMessage.text
                }else if(chatModel.lastMessage.message_type== MESSAGE_TYPE_IMAGE){
                    itemMainListBinding.mainlistLastmessageText.text="Photo message"
                    itemMainListBinding.mainlistLastmessageText.setTextColor(Color.BLUE)
                }else{
                    itemMainListBinding.mainlistLastmessageText.text="Voice message"
                    itemMainListBinding.mainlistLastmessageText.setTextColor(Color.BLUE)
                }
                itemMainListBinding.mainlistName.text=chatModel.name
                itemMainListBinding.mainlistLastmessageTime.text=
                    chatModel.lastMessage.timeStamp.toString().asTime()
                if(chatModel.state=="online"){
                    itemMainListBinding.isOnlineIndicator.visibility= View.VISIBLE
                }else{
                    itemMainListBinding.isOnlineIndicator.visibility= View.GONE
                }
                itemMainListBinding.root.setOnClickListener{
                    var model=ContactModel()
                    model.id=chatModel.id
                    model.fullName=chatModel.name
                    replaceFragment(UserChatFragment.newInstance(model))
                }

            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VhMainListItem {
        return VhMainListItem(ItemMainListBinding
            .inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: VhMainListItem, position: Int) {
        holder.onBind(mainList[mainList.size-1-position])
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

}