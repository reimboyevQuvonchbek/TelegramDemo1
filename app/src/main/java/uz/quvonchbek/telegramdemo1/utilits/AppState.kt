package uz.quvonchbek.telegramdemo1.utilits

enum class AppState(val state:String) {
    ONLINE("online"),
    OFFLINE("offline"),
    TYPING("typing..");
    companion object{
        fun uptadeState(appState: AppState){
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
                .child(CHILD_STATE).setValue(appState.state)
                .addOnSuccessListener { USER.state=appState.state }
                .addOnFailureListener{showToast(it.message.toString())}
        }
    }
}