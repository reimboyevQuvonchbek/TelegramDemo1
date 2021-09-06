package uz.quvonchbek.telegramdemo1.utilits

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import uz.quvonchbek.telegramdemo1.models.User

class AppValueEventListener(val onSuccess:(DataSnapshot)->Unit):ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        onSuccess(snapshot)

    }
    override fun onCancelled(error: DatabaseError) {
    }
}