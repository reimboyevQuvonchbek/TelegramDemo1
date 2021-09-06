package uz.quvonchbek.telegramdemo1.adapters.holder

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import uz.quvonchbek.telegramdemo1.R
import uz.quvonchbek.telegramdemo1.databinding.ItemVoiceMessageBinding
import uz.quvonchbek.telegramdemo1.models.Message
import uz.quvonchbek.telegramdemo1.utilits.*
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VhVoiceMessage @Inject constructor(var mediaplayerServise: MediaplayerServise,var itemVoiceMessageBinding: ItemVoiceMessageBinding) :
    RecyclerView.ViewHolder(itemVoiceMessageBinding.root){
    private var handler = Handler(Looper.getMainLooper())
    var mMediaPlayer=mediaplayerServise.getMediaPlayer1()
    private lateinit var mFile: File
    private lateinit var fromId:String


    fun onBindUser(message: Message) {
        if (message.isRead) {
           itemVoiceMessageBinding.voiceIsRead.setImageResource(R.drawable.ic_check_2)
        }else{
            itemVoiceMessageBinding.voiceIsRead.setImageResource(R.drawable.ic_check_1)
        }
        itemVoiceMessageBinding.blocReceivedVoiceMessage.visibility = View.INVISIBLE
        itemVoiceMessageBinding.blocUserVoiceMessage.visibility = View.VISIBLE
        itemVoiceMessageBinding.chatUserVoiceMessageSize.visibility=View.INVISIBLE
        itemVoiceMessageBinding.chatUserVoiceMessageTime.text =
            message.timeStamp.toString().asTime()

        fromId=message.from

    }

    fun onBindReceived(message: Message) {
        if (!message.isRead) {
            readMessage(message)
        }
        itemVoiceMessageBinding.blocUserVoiceMessage.visibility = View.INVISIBLE
        itemVoiceMessageBinding.blocReceivedVoiceMessage.visibility = View.VISIBLE
        itemVoiceMessageBinding.chatReceivedVoiceMessageSize.visibility=View.INVISIBLE

        itemVoiceMessageBinding.chatReceivedVoiceMessageTime.text =
            message.timeStamp.toString().asTime()
        fromId=message.from

    }

    fun onAttach(message: Message) {
        if (message.from == CURRENT_UID) {

            itemVoiceMessageBinding.chatUserBtnPlay.setOnClickListener {

                itemVoiceMessageBinding.chatUserBtnPause.visibility = View.VISIBLE
                itemVoiceMessageBinding.chatUserBtnPlay.visibility = View.INVISIBLE
                itemVoiceMessageBinding.chatUserVoiceMessageSize.visibility=View.VISIBLE

                play(message.id, message.file_url) {
                    itemVoiceMessageBinding.chatUserBtnPause.visibility = View.INVISIBLE
                    itemVoiceMessageBinding.chatUserBtnPlay.visibility = View.VISIBLE
                    itemVoiceMessageBinding.chatUserVoiceMessageSize.visibility=View.INVISIBLE
                    itemVoiceMessageBinding.userSeekbar.setProgress(0)
                    handler.removeCallbacks(runnable)
                }



                itemVoiceMessageBinding.chatUserVoiceMessageSize.text =
                    timerConversion(mMediaPlayer.duration.toLong())
                handler.postDelayed(runnable, 100)
                itemVoiceMessageBinding.userSeekbar.max = mMediaPlayer.duration

                itemVoiceMessageBinding.chatUserBtnPause.setOnClickListener {
                    if (mMediaPlayer.isPlaying) {
                        mMediaPlayer.pause()
                        itemVoiceMessageBinding.chatUserBtnPause.setImageResource(R.drawable.ic_play_green)
                    } else {
                        itemVoiceMessageBinding.chatUserBtnPause.setImageResource(R.drawable.ic_pause_green)
                        mMediaPlayer.start()
//                        mMediaPlayer.setOnCompletionListener {
//                            stop {
//                                itemVoiceMessageBinding.chatUserBtnPause.visibility = View.INVISIBLE
//                                itemVoiceMessageBinding.chatUserBtnPlay.visibility = View.VISIBLE
//                                itemVoiceMessageBinding.chatUserVoiceMessageSize.visibility = View.INVISIBLE
//                                itemVoiceMessageBinding.userSeekbar.setProgress(0)
//                                handler.removeCallbacks(runnable)
//                            }
//                        }
                    }
                }
                itemVoiceMessageBinding.userSeekbar.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            if (fromUser) {
                                mMediaPlayer.seekTo(progress)
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })
            }
        } else {
            itemVoiceMessageBinding.chatReceivedBtnPlay.setOnClickListener {
                itemVoiceMessageBinding.chatReceivedBtnPause.visibility = View.VISIBLE
                itemVoiceMessageBinding.chatReceivedBtnPlay.visibility = View.INVISIBLE
                itemVoiceMessageBinding.chatReceivedVoiceMessageSize.visibility=View.VISIBLE
                play(message.id, message.file_url) {
                    itemVoiceMessageBinding.chatReceivedBtnPause.visibility = View.INVISIBLE
                    itemVoiceMessageBinding.chatReceivedBtnPlay.visibility = View.VISIBLE
                    itemVoiceMessageBinding.chatReceivedVoiceMessageSize.visibility=View.INVISIBLE
                    itemVoiceMessageBinding.receivedSeekbar.setProgress(0)
                    handler.removeCallbacks(runnable)
                }
                itemVoiceMessageBinding.chatReceivedVoiceMessageTime.text =
                    timerConversion(mMediaPlayer.duration.toLong())
                handler.postDelayed(runnable, 100)
                itemVoiceMessageBinding.receivedSeekbar.max = mMediaPlayer.duration

                itemVoiceMessageBinding.chatReceivedBtnPause.setOnClickListener {
                    if (mMediaPlayer.isPlaying) {
                        mMediaPlayer.pause()
                        itemVoiceMessageBinding.chatReceivedBtnPause.setImageResource(R.drawable.ic_play_blue)
                    } else {
                        itemVoiceMessageBinding.chatReceivedBtnPause.setImageResource(R.drawable.ic_pause_blue)
                        mMediaPlayer.start()
                    }


                }
                itemVoiceMessageBinding.receivedSeekbar.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            if (fromUser) {
                                mMediaPlayer.seekTo(progress)
                            }
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    })
            }
        }
    }

    fun onDeAttach() {
        itemVoiceMessageBinding.chatReceivedBtnPlay.setOnClickListener(null)
        itemVoiceMessageBinding.chatUserBtnPlay.setOnClickListener(null)
        release()
    }

    fun play(messageKey: String, fileUrl: String, function: () -> Unit) {
        mFile = File(APP_ACTIVITY.filesDir, messageKey)
        if (mFile.exists() && mFile.length() > 0 && mFile.isFile) {
            startPlay {
                function()
            }
        } else {
            mFile.createNewFile()
            getFileFromStorage(mFile, fileUrl) {
                startPlay {
                    function()
                }

            }
        }
    }

    private fun startPlay(function: () -> Unit) {
        try {
            mMediaPlayer.setDataSource(mFile.absolutePath)
            mMediaPlayer.prepare()
            mMediaPlayer.start()
            mMediaPlayer.setOnCompletionListener {
                stop {
                    function()
                }
            }
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun stop(function: () -> Unit) {
        try {
            mMediaPlayer.stop()
            mMediaPlayer.reset()
            function()
        } catch (e: Exception) {
            showToast(e.message.toString())
            function()
        }
    }

    fun pause(funtion: () -> Unit) {
        try {
            mMediaPlayer.pause()
            funtion()
        } catch (e: Exception) {
            showToast(e.message.toString())
        }
    }

    fun release() {
        mMediaPlayer.release()
    }

    fun timerConversion(value: Long): String {
        val audioTime: String
        val dur = value.toInt()
        val hrs = dur / 3600000
        val mns = dur / 60000 % 60000
        val scs = dur % 60000 / 1000
        audioTime = if (hrs > 0) {
            String.format("%02d:%02d:%02d", hrs, mns, scs)
        } else {
            String.format("%02d:%02d", mns, scs)
        }
        return audioTime
    }

    private var runnable = object : Runnable {
        override fun run() {
            if(fromId== CURRENT_UID){
                itemVoiceMessageBinding.userSeekbar.progress = mMediaPlayer.currentPosition
                itemVoiceMessageBinding.chatUserVoiceMessageSize.text =
                    timerConversion(mMediaPlayer.currentPosition.toLong())
            }else{
                itemVoiceMessageBinding.receivedSeekbar.progress = mMediaPlayer.currentPosition
                itemVoiceMessageBinding.chatReceivedVoiceMessageSize.text =
                    timerConversion(mMediaPlayer.currentPosition.toLong())
            }

            handler.postDelayed(this, 100)
        }
    }

//
//    override fun onDetach() {
//        chatReceivedBtnPlay.setOnClickListener(null)
//        chatUserBtnPlay.setOnClickListener(null)
//        mAppVoicePlayer.release()
//    }

}