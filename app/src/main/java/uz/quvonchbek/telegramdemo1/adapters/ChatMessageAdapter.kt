package uz.quvonchbek.telegramdemo1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.adapters.holder.VhVoiceMessage
import uz.quvonchbek.telegramdemo1.databinding.FromMeMessageBinding
import uz.quvonchbek.telegramdemo1.databinding.ItemPhotoMessageBinding
import uz.quvonchbek.telegramdemo1.databinding.ItemVoiceMessageBinding
import uz.quvonchbek.telegramdemo1.databinding.ToMeMessageBinding
import uz.quvonchbek.telegramdemo1.models.Message
import uz.quvonchbek.telegramdemo1.utilits.*
import javax.inject.Singleton

@Singleton
class ChatMessageAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var list = mutableListOf<Message>()

    fun addItemToBottom(
        item: Message,
        onSuccess: () -> Unit
    ) {
        if (!list.contains(item)) {
            list.add(item)
            notifyItemInserted(list.size)
            onSuccess()
        }
    }

    fun addItemToTop(
        item: Message,
        onSuccess: () -> Unit
    ) {
        if (!list.contains(item)) {
            list.add(item)
            list.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
            onSuccess()
        }
    }


    inner class VhMessageFromMe(var fromMeMessageBinding: FromMeMessageBinding) :
        RecyclerView.ViewHolder(fromMeMessageBinding.root) {

        fun onBind(message: Message) {
            if (message.isRead) {
                fromMeMessageBinding.isRead.setImageResource(R.drawable.ic_check_2)
            } else {
                fromMeMessageBinding.isRead.setImageResource(R.drawable.ic_check_1)
            }
            fromMeMessageBinding.messageText.text = message.text
            fromMeMessageBinding.messageTime.text = message.timeStamp.toString().asTime()

        }
    }

    inner class VhMessageToMe(var toMeMessageBinding: ToMeMessageBinding) :
        RecyclerView.ViewHolder(toMeMessageBinding.root) {

        fun onBind(message: Message) {
            if (!message.isRead) {
                readMessage(message)
            }
            toMeMessageBinding.messageText.text = message.text
            toMeMessageBinding.messageTime.text = message.timeStamp.toString().asTime()
        }
    }

    inner class VhPhotoMessage(var itemPhotoMessageBinding: ItemPhotoMessageBinding) :
        RecyclerView.ViewHolder(itemPhotoMessageBinding.root) {
        fun onBindPhoto1(message: Message) {
            itemPhotoMessageBinding.blockReceivedMessage.visibility = View.GONE
            itemPhotoMessageBinding.blockUserMessage.visibility = View.VISIBLE
            itemPhotoMessageBinding.messagePhoto1.setUriImage(message.file_url)
            itemPhotoMessageBinding.messageTime1.text = message.timeStamp.toString().asTime()
            if (message.isRead) {
                itemPhotoMessageBinding.isRead1.setImageResource(R.drawable.ic_check_white2)
            } else {
                itemPhotoMessageBinding.isRead1.setImageResource(R.drawable.ic_check_white1)
            }
        }

        fun onBindPhoto2(message: Message) {
            if (!message.isRead) {
                readMessage(message)
            }
            itemPhotoMessageBinding.blockReceivedMessage.visibility = View.VISIBLE
            itemPhotoMessageBinding.blockUserMessage.visibility = View.GONE
            itemPhotoMessageBinding.messagePhoto2.setUriImage(message.file_url)
            itemPhotoMessageBinding.messageTime2.text = message.timeStamp.toString().asTime()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            1 -> {
                VhMessageFromMe(
                    FromMeMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            2 -> {
                VhPhotoMessage(
                    ItemPhotoMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            3->{
                VhVoiceMessage(
                    itemVoiceMessageBinding = ItemVoiceMessageBinding
                        .inflate(LayoutInflater.from(parent.context,),parent,false)
                    )
            }
            5 -> {
                VhMessageToMe(
                    ToMeMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            6 -> {
                VhPhotoMessage(
                    ItemPhotoMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            7->{
                VhVoiceMessage(
                    ItemVoiceMessageBinding
                        .inflate(LayoutInflater.from(parent.context),parent,false),
                )
            }
            else -> {
                VhMessageToMe(
                    ToMeMessageBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val m = list[position]
        return if (m.from == CURRENT_UID) {
            when (m.message_type) {
                MESSAGE_TYPE_TEXT -> 1
                MESSAGE_TYPE_IMAGE -> 2
                MESSAGE_TYPE_VOICE -> 3
                else -> 4
            }
        } else {
            when (m.message_type) {
                MESSAGE_TYPE_TEXT -> 5
                MESSAGE_TYPE_IMAGE -> 6
                MESSAGE_TYPE_VOICE -> 7
                else -> 8
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            1 -> {
                val from = holder as VhMessageFromMe
                from.onBind(list[position])
            }
            2 -> {
                val fromImage = holder as VhPhotoMessage
                fromImage.onBindPhoto1(list[position])
            }
            3->{
                val fromVoice = holder as VhVoiceMessage
                fromVoice.onBindUser(list[position])
            }
            5 -> {
                val tome = holder as VhMessageToMe
                tome.onBind(list[position])
            }
            6 -> {
                val tomeImage = holder as VhPhotoMessage
                tomeImage.onBindPhoto2(list[position])
            }
            7->{
                val tomeVoice=holder as VhVoiceMessage
                tomeVoice.onBindReceived(list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if(holder.itemViewType == 3 || holder.itemViewType==7){
            (holder as VhVoiceMessage).onAttach(list[holder.adapterPosition])
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if(holder.itemViewType == 3 || holder.itemViewType==7){
            (holder as VhVoiceMessage).onDeAttach()
        }
        super.onViewDetachedFromWindow(holder)
    }

}