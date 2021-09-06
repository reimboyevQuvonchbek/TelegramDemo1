package uz.quvonchbek.telegramdemo1.ui.register

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import uz.quvonchbek.telegramdemo1.databinding.FragmentCodeBinding
import uz.quvonchbek.telegramdemo1.utilits.*
import java.util.concurrent.TimeUnit

class CodeFragment(
    val number: String,
    var storedVerificationId: String,
    var resendToken: PhoneAuthProvider.ForceResendingToken
) : Fragment() {
    val TAG = "Quvonchbek1"

    lateinit var binding: FragmentCodeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCodeBinding.inflate(layoutInflater, container, false)

        binding.btnResend.setOnClickListener {
            resendCode(number)
        }
        binding.t2.text = "Bir martalik kod $number raqamiga yuborildi"
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.t4.text = "00:" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                binding.btnResend.isShown

            }
        }.start()


        binding.code.addTextChangedListener(AppTextWatcher {
            val codetext = binding.code.text.toString()
            if (codetext.length == 6) {
                verifyCode(codetext)
            }
        })
        return binding.root
    }

    private fun verifyCode(codetext: String) {
        val c = PhoneAuthProvider.getCredential(storedVerificationId, codetext)
        signInWithPhoneAuthCredential(c)
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        AUTH.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val id=task.result?.user?.uid.toString()
                    val dateMap = mutableMapOf<String, Any>()
                    dateMap[CHILD_ID] = id
                    dateMap[CHILD_PHONE] = number

                    REF_DATABASE_ROOT.child(NODE_USERS).child(id)
                        .addListenerForSingleValueEvent(AppValueEventListener{
                            if(!it.hasChild(CHILD_USERNAME)){
                                dateMap[CHILD_USERNAME] = id
                            }

                            REF_DATABASE_ROOT.child(NODE_PHONES).child(number).setValue(id).addOnCompleteListener {it1->
                                if(it1.isSuccessful){
                                    REF_DATABASE_ROOT.child(NODE_USERS).child(id).updateChildren(dateMap)
                                        .addOnCompleteListener { task2 ->
                                            if (task2.isSuccessful) {
                                                showToast("Добро пожаловать")
                                                reStartActivity()
                                            } else showToast(task2.exception?.message.toString())
                                        }
                                }
                            }

                        })

                } else showToast(task.exception?.message.toString())

            }
    }

    fun resendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .setForceResendingToken(resendToken)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    private var callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
//            AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    showToast("Добро пожаловать")
//                    (activity as RegisterActivity).replaceActivity(MainActivity())
//                } else showToast(task.exception?.message.toString())
//            }
            Log.d(TAG, "Phonefragment1 onVerificationCompleted:$credential")
            AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    showToast("Добро пожаловать")
                    reStartActivity()
                } else showToast(task.exception?.message.toString())
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            Log.w(TAG, "onVerificationFailed", e)

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
            }

            // Show a message and update the UI
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.
            Log.d(TAG, "onCodeSent:$verificationId")

            // Save verification ID and resending token so we can use them later
            storedVerificationId = verificationId
            resendToken = token
        }
    }
}