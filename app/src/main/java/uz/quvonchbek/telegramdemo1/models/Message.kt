package uz.quvonchbek.telegramdemo1.models

class Message {
    var id:String=""
    var text:String=""
    var message_type:String=""
    var timeStamp:Any=""
    var from:String=""
    var isRead:Boolean=false
    var file_url:String="empty"

    constructor()


    override fun equals(other: Any?): Boolean {
        val m=other as Message
        return  m.message_type==this.message_type
                && m.from==this.from && m.id==this.id

    }
}