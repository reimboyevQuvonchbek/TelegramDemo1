package uz.quvonchbek.telegramdemo1.utilits

import android.net.Uri
import android.provider.ContactsContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import uz.quvonchbek.telegramdemo1.models.ContactModel
import uz.quvonchbek.telegramdemo1.models.Message
import uz.quvonchbek.telegramdemo1.models.User
import uz.quvonchbek.telegramdemo1.ui.register.PhoneNumberFragment
import java.io.File


lateinit var AUTH: FirebaseAuth
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT:StorageReference
lateinit var USER:User
lateinit var CURRENT_UID:String

const val FOLDER_PROFIL_IMAGE="profil_image"
const val FOLDER_MESSAGE_IMAGE="message_image"

const val NODE_USERS = "users"
const val NODE_USERNAMES="usernames"
const val NODE_PHONES="phones"
const val NODE_PHONES_CONTACTS="phone_contacts"
const val NODE_MESSAGES="messages"
const val NODE_MAIN_LIST="main_list"

const val CHILD_NAME="name"
const val CHILD_ID = "id"
const val CHILD_PHONE = "phoneNumber"
const val CHILD_USERNAME = "username"
const val CHILD_FULNAME="fullName"
const val CHILD_SURNAME="surName"
const val CHILD_BIO="bio"
const val CHILD_FILE_URL="file_url"
const val CHILD_PHOTO_URL="photo_url"
const val CHILD_STATE="state"

const val CHILD_TEXT="text"
const val CHILD_TYPE="message_type"
const val CHILD_FROM="from"
const val CHILD_TIMESTAMP="timeStamp"
const val CHILD_READ="isRead"
const val CHILD_LASTMESSAGE="lastMessage"

const val MESSAGE_TYPE_IMAGE ="image"
const val MESSAGE_TYPE_TEXT ="text"
const val MESSAGE_TYPE_VOICE="voice"
const val MESSAGE_TYPE_FILE="file"

fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER= User()
    CURRENT_UID= AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT=FirebaseStorage.getInstance().reference
}

inline fun initUser(crossinline function: ()->Unit) {
    if(AUTH.currentUser!=null){
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
            .addListenerForSingleValueEvent(AppValueEventListener{
                USER =it.getValue(User::class.java) ?: User()
                if(USER.username.isEmpty()){
                    USER.username= CURRENT_UID
                }
                function()
            })
    }else{
        replaceFragment(PhoneNumberFragment())
    }
}

//lambda storagega rasm saqlash
inline fun putFileStorage(uri: Uri, path:StorageReference, crossinline function: ()->Unit){
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener{ showToast("error1:${it.message.toString()}")}
}

inline fun getUrlFromStorage(path: StorageReference,crossinline function:(P_url:String)->Unit){
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener{ showToast("error2:${it.message.toString()}")}
}

inline fun putUrlDatabase(url:String,crossinline function:()-> Unit){
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_FILE_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener{showToast("error3:${it.message.toString()}")}
}


fun initContacts() {
    if(checkPermission(READ_CONTACTS)){
        val contactList=ArrayList<ContactModel>()
        val cursor= APP_ACTIVITY.contentResolver
            .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
        if(cursor!=null){
            while (cursor.moveToNext()){
                val name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val newContact=ContactModel()
                newContact.fullName=name
                newContact.phoneNumber=number.replace(Regex("[\\s,-]"),"")
                contactList.add(newContact)
            }
        }
        cursor?.close()
        updateContactDatabase(contactList)

    }
}

fun DataSnapshot.getUser():User{
    return this.getValue(User::class.java) ?: User()
}

fun updateContactDatabase(contactList: ArrayList<ContactModel>) {
    if(AUTH.currentUser!=null){
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(
            AppValueEventListener(){
                it.children.forEach{snapshot->
                    contactList.forEach { contact->
                        val num=snapshot.key.toString().replace(Regex("[\\s,-]"),"")
                        if(num==contact.phoneNumber){
                            contact.id=snapshot.value.toString()
                            REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                                .child(snapshot.value.toString()).setValue(contact)
                        }
                    }
                }
            }
        )
    }
}
fun readMessage(message: Message){
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/${message.from}"
    val refDialogReceivingUser = "$NODE_MESSAGES/${message.from}/$CURRENT_UID"
    val mapDialog = hashMapOf<String,Any>()
    mapDialog["$refDialogUser/${message.id}/isRead"] = true
    mapDialog["$refDialogReceivingUser/${message.id}/isRead"] = true
    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { }
        .addOnFailureListener { showToast(it.message.toString()) }
}
fun sendMessage(message: String, receivingUserID: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    val refUser= "$NODE_MAIN_LIST/$CURRENT_UID/$receivingUserID"
    val refReceived= "$NODE_MAIN_LIST/$receivingUserID/$CURRENT_UID"
    val messageKey= getMessageKey(receivingUserID)
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = MESSAGE_TYPE_TEXT
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_READ]=false

    val mapDialog = hashMapOf<String,Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function()
            REF_DATABASE_ROOT.child(refUser).child(CHILD_LASTMESSAGE).updateChildren(mapMessage)
            REF_DATABASE_ROOT.child(refReceived).child(CHILD_LASTMESSAGE).updateChildren(mapMessage)
        }
        .addOnFailureListener { showToast(it.message.toString()) }


}

fun getMessageKey(id:String):String {
   return REF_DATABASE_ROOT.child("$NODE_MESSAGES/$CURRENT_UID/$id").push().key.toString()
}

fun sendFileMessage(file_url:String, key:String, receivingUserID: String, type:String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    val refUser= "$NODE_MAIN_LIST/$CURRENT_UID/$receivingUserID"
    val refReceived= "$NODE_MAIN_LIST/$receivingUserID/$CURRENT_UID"
    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FILE_URL]= file_url
    mapMessage[CHILD_ID] = key
    mapMessage[CHILD_TEXT]=""
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = type
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_READ]=false
    val mapDialog = hashMapOf<String,Any>()
    mapDialog["$refDialogUser/$key"] = mapMessage
    mapDialog["$refDialogReceivingUser/$key"] = mapMessage
    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function()
            REF_DATABASE_ROOT.child(refUser).child(CHILD_LASTMESSAGE).updateChildren(mapMessage)
            REF_DATABASE_ROOT.child(refReceived).child(CHILD_LASTMESSAGE).updateChildren(mapMessage)
        }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setFullNameFirebase(name: String, surname: String) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_SURNAME)
        .setValue(surname)
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULNAME).setValue(name)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                USER.fullName = name
                USER.surName = surname
                APP_ACTIVITY.supportFragmentManager.popBackStack()
                APP_ACTIVITY.mAppDrawer.updateHeader()
            }
        }
}

fun currentUsernameChange(name:String){
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_USERNAME).setValue(name).addOnCompleteListener {
        if(it.isSuccessful){
            deleteOldUsername(name)
        }
    }
}
fun changeUsername(name: String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(name).setValue(CURRENT_UID).addOnCompleteListener {
        if(it.isSuccessful){
            currentUsernameChange(name)
        }else{
            showToast("xatolik1")
        }
    }
}
fun deleteOldUsername(name:String) {
    REF_DATABASE_ROOT.child(NODE_USERNAMES).child(USER.username).removeValue()
        .addOnCompleteListener {
            if(it.isSuccessful){
                USER.username=name
                APP_ACTIVITY.supportFragmentManager.popBackStack()
            }else{
                showToast("xatolik3")
            }
        }
}

fun setMainList(id: String, type: String) {
    val refUser= "$NODE_MAIN_LIST/$CURRENT_UID/$id"
    val refReceived= "$NODE_MAIN_LIST/$id/$CURRENT_UID"
    val map1= hashMapOf<String,String>()
    map1[CHILD_ID]=id
    map1[CHILD_TYPE]=type

    val map2= hashMapOf<String,String>()
    map2[CHILD_ID]= CURRENT_UID
    map2[CHILD_TYPE]=type

    val map= hashMapOf<String,Any>()
    map[refUser]=map1
    map[refReceived]=map2
    REF_DATABASE_ROOT.updateChildren(map).addOnFailureListener { showToast(it.message.toString()) }
}

fun uploadFileStorage(uri: Uri, messageKey: String,receivingUserID: String,type:String) {
    val path= REF_STORAGE_ROOT
        .child(CURRENT_UID)
        .child(type)
        .child(messageKey)
    putFileStorage(uri,path) {
        getUrlFromStorage(path) {
            putUrlDatabase(it) {
                sendFileMessage(uri.toString(), messageKey, receivingUserID, type) {

                }
                showToast("ok")
            }
        }
    }
}
fun getFileFromStorage(mFile: File, fileUrl: String, function: () -> Unit) {
    val path= REF_STORAGE_ROOT.storage.getReferenceFromUrl(fileUrl)
    path.getFile(mFile)
        .addOnSuccessListener { function() }
        .addOnFailureListener{ showToast(it.message.toString())}
}