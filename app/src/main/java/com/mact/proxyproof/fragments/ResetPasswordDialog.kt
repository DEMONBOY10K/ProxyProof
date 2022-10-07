package com.mact.GetStepGo.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.FirebaseAuth
import com.mact.proxyproof.LoginActivity
import com.mact.proxyproof.R
import com.mact.proxyproof.SignUpActivity
import com.mact.proxyproof.fragments.EmailSentDialog
import kotlinx.android.synthetic.main.fragment_resetpassword.*



class ResetPasswordDialog : DialogFragment(R.layout.fragment_resetpassword) {
    private lateinit var userReset : FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        userReset = FirebaseAuth.getInstance()
        val email = etResetEmail.toString().trim()
        val mainActivityView = (activity as LoginActivity)
        btnResetPass.setOnClickListener{
            val dialog = EmailSentDialog()
            dialog.show(mainActivityView.supportFragmentManager,"customDialog")
            dialog.isCancelable = false
            dismiss()
            if(validateEmail()){

                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(mainActivityView,"Email Sent Successfully",Toast.LENGTH_LONG).show()
                        val alert = "Password reset Mail has been sent to your \\nEmail Address!!"
                        tvResetAlert.text = alert

                    }
                }.addOnFailureListener {
                    Log.d("ResetPassword","Failed to Send reset password email $email")

                }
//            btnBackToLogin.visibility = View.VISIBLE
//            btnResetPass.visibility = View.INVISIBLE
//            mainActivityView.replaceFragment(fragment)
            }
        }
        btnBackToLogin.setOnClickListener{
            dismiss()
        }
    }
    private fun validateEmail() : Boolean{
        val email  = etResetEmail.text.toString().trim()
        val emailRegex : Regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$".toRegex()
        if (email.isEmpty()){
            tiResetEmail.error = "Enter Your Email"
            return false
        }
        else if(!email.matches(emailRegex)){
            tiResetEmail.error = "Invalid Email Address"
            return false
        }
        else
        {
            tiResetEmail.isErrorEnabled = false
            tiResetEmail.error = null
            return true
        }
    }
}
//class ResetPasswordFragment:DialogFragment(){
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView : View = inflater.inflate(R.layout.fragment_resetpassword,container,false)
//        return rootView
//    }
//}