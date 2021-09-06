package uz.quvonchbek.telegramdemo1.ui.register

import android.os.Bundle
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
import uz.quvonchbek.telegramdemo1.databinding.FragmentPhoneNumberBinding
import uz.quvonchbek.telegramdemo1.utilits.AUTH
import uz.quvonchbek.telegramdemo1.utilits.replaceFragment
import uz.quvonchbek.telegramdemo1.utilits.showToast
import java.util.concurrent.TimeUnit

class PhoneNumberFragment : Fragment() {
    lateinit var binding: FragmentPhoneNumberBinding
    lateinit var phoneNumber:String

    val TAG="Quvonchbek1"
    lateinit var storedVerificationId:String
    lateinit var resendToken:PhoneAuthProvider.ForceResendingToken

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentPhoneNumberBinding.inflate(layoutInflater, container, false)
       // binding.ccp.registerCarrierNumberEditText(binding.editTextCarrierNumber)
//        mCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        showToast("Добро пожаловать")
//                        (activity as RegisterActivity).replaceActivity(MainActivity())
//                    } else showToast(task.exception?.message.toString())
//                }
//            }
//
//            override fun onVerificationFailed(p0: FirebaseException) {
//                showToast(p0.message.toString())
//            }
//
//            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
////                replaceFragment(R.id.container2,CodeFragment.newInstance(phoneNumber,id))
//                parentFragmentManager.beginTransaction().replace(R.id.container2,CodeFragment.newInstance(phoneNumber,id))
//            }

//        }
        showToast("phone number")
        binding.registerBtnNext.setOnClickListener{
            if(binding.editTextCarrierNumber.text.isNotEmpty()){
                phoneNumber="+${binding.ccp.selectedCountryCode} ${binding.editTextCarrierNumber.text}"
                sendCode(phoneNumber)
            }else{
                showToast("Enter phone number")
            }

        }
        return binding.root
    }

    private fun sendCode(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(AUTH)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(requireActivity())                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
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
                   // reStartActivity()
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
            replaceFragment(
                CodeFragment(phoneNumber,storedVerificationId,resendToken)
            )
        }
    }


}