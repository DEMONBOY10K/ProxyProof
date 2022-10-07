package com.mact.proxyproof


import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mact.proxyproof.dataclass.Dates
import com.mact.proxyproof.dataclass.Stats
import com.mact.proxyproof.dataclass.UserData
import com.mact.proxyproof.dataclass.Users
import com.mact.proxyproof.fragments.SignUpFragment1
import kotlinx.android.synthetic.main.activity_signup2.*
import kotlinx.android.synthetic.main.fragment_signup1.*
import kotlinx.android.synthetic.main.fragment_signup2.*
import kotlinx.android.synthetic.main.fragment_signup3.*
import java.util.*


class SignUpActivity : AppCompatActivity() {
    private lateinit var database : DatabaseReference
    private lateinit var userDataDatabase : DatabaseReference
    private lateinit var statsDatabase : DatabaseReference
    private lateinit var datesDatabase : DatabaseReference
    private lateinit var user : FirebaseAuth
    private var backPressedTime:Long = 0
    lateinit var backToast:Toast
    private val radioGroup: RadioGroup? = null
    private var radioButton: RadioButton? = null
    override fun onBackPressed() {
        backToast = Toast.makeText(this, "Press back again to Exit.", Toast.LENGTH_LONG)
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel()
            super.onBackPressed()
            return
        } else {
            backToast.show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup2)

        val fragment1 = SignUpFragment1()
        replaceFragment(fragment1)
        val slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_to_center_animation)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.alphafadein700_animation)
        lavSignUp.startAnimation(slideRightAnimation)
        constraintLayoutSignup.startAnimation(fadeInAnimation)
        user = FirebaseAuth.getInstance()
//        btnToDashboard.setOnClickListener {
//
//            if (validateFName() && validateLName() && validateEmail() && validateAge() && validateWeight() && validateHeight() && validatePassword()) {
//                registerUser()
//
//            }
//        }
        tvbtnToLogin.setOnClickListener {
            val slideRightAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_center_to_right_animation)
            val fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.alphafadeout700_animation)
            lavSignUp.startAnimation(slideRightAnimation)
            constraintLayoutSignup.startAnimation(fadeOutAnimation)
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }

        }


//        rbtnMale.setOnClickListener {
//            ivGender.setImageResource(R.drawable.male)
//        }
//        rbtnFemale.setOnClickListener {
//            ivGender.setImageResource(R.drawable.female)
//        }

    }
     fun replaceFragment(fragment : Fragment){
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fcvSignUp,fragment)
//                addToBackStack(null)
                commit()
            }
    }
    private fun showDialog(activity: Activity?, msg: String?) {
        val dialog = Dialog(activity!!, R.style.AppTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.loadingscreen)
        dialog.show()
//        val timer = Timer()
//        timer.schedule(object : TimerTask() {
//            override fun run() {
//                dialog.dismiss()
//                timer.cancel()
//            }
//        }, 4000)
    }
    fun registerUser (){
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()

        user.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(SignUpActivity()){ task->
                if (task.isSuccessful){
                    showDialog(this,"SignUp Successful")
                    beginRegistration()
                }
                else{
                    Toast.makeText(
                        this,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }.addOnFailureListener (SignUpActivity() ){
                Toast.makeText(
                    this,
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun beginRegistration(){
        val url = getString(R.string.firebase_db_location)
        val firstName = etFName.text.toString().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
        val lastName =etLName.text.toString().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }
        val name = "$firstName $lastName"
        val email = etEmail.text.toString()
        val age = etAge.text.toString()
        val scholar = etScholar.text.toString().toInt()
        val semester = etSemester.text.toString().toInt()
        val userName = emailToUserName(email)
        val password = etPassword.text.toString()

//        val checkedGenderRadioButtonId = rgGender.checkedRadioButtonId
//        val gender = findViewById<RadioButton>(checkedGenderRadioButtonId).text.toString()

//        val selectedId = radioGroup!!.checkedRadioButtonId
//        radioButton = (radioButton)?.findViewById((selectedId));
//        val gender = radioButton?.text.toString();
        val currentDate = getDate()
        user.currentUser?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent Successfully to "+ user.currentUser?.email.toString())
                    database = FirebaseDatabase.getInstance(url).getReference("users")
                    val users = Users(firstName, lastName, email, scholar,age,semester, password)
                    userDataDatabase = FirebaseDatabase.getInstance(url).getReference("userData")
                    val userData = UserData(email,0,0F,0F,0,0,0F,0F,0,currentDate)
                    userDataDatabase.child(userName).setValue(userData).addOnSuccessListener {
                        Log.d("UserData","Successfully Initialized Userdata")
                    }.addOnFailureListener {
                        Log.d("UserData","Failed to Initialize Userdata")
                    }

                    datesDatabase = FirebaseDatabase.getInstance(url).getReference("stats")
                    val stats = Stats(email,0,0F,0F,0,currentDate)
                    val dates = Dates(stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats, stats,stats,
                        stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats,stats)
//                    statsDatabase = FirebaseDatabase.getInstance(url).getReference("stats")
                    datesDatabase.child(userName).setValue(dates).addOnSuccessListener {
                        Log.d("stats","Successfully Initialized stats")
                    }.addOnFailureListener {
                        Log.d("stats","Failed to Initialize stats")
                    }
                    database.child(userName).setValue(users).addOnSuccessListener {
                        etFName.text?.clear()
                        etLName.text?.clear()
                        etEmail.text?.clear()
                        etAge.text?.clear()
                        etSemester.text?.clear()
                        etScholar.text?.clear()
                        etPassword.text?.clear()
//                        Log.d(
//                            "MyActivity",
//                            "$firstName $lastName @($email) , $gender" + " born on $dob has height ${height}cm & Weight ${weight}kg , Registered as an User"
//                        )
                        Log.d("userCurrent", "${user.currentUser}")
                        Toast.makeText(
                            this,
                            "$firstName, Verify your Email to Continue",
                            Toast.LENGTH_LONG
                        ).show()
                        Handler().postDelayed({
                            user.signOut()
                            Intent(this, LoginActivity::class.java).also {
                                startActivity(it)
                                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                                finish()
                            }
                        }, 1500) // 1500 is the delayed time in milliseconds.
                    }.addOnFailureListener {
                        Log.d("SignUp", "Failed TO Signup")
                        Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                    }

                }
            }

    }
    private fun getDate() : String{
        val c =Calendar.getInstance()
        var day = c.get(Calendar.DAY_OF_MONTH).toString()
        var month =  (c.get(Calendar.MONTH) + 1).toString()
        if(day.length<2){
            day = "0$day"
        }
        if(month.length<2){
            month = "0$month"
        }
        val year = c.get(Calendar.YEAR).toString()
        val date = "$day$month$year"
        return date
    }
    fun validateFName() : Boolean{
        Log.d(TAG, "validateFname")
        val first = etFName.text.toString().trim()
        if (first.isEmpty()){
            tiFName.error = "Enter Your First Name"
           return false
        }
        else{
            tiFName.isErrorEnabled = false
            tiFName.error=null
            return true
        }

    }
    fun validateLName() : Boolean{
        val last = etLName.text.toString().trim()
        if (last.isEmpty()){
            tiLName.error = "Enter Your Last Name"
                return false
        }
        else{
            tiLName.isErrorEnabled = false
            tiLName.error = null
            return true
        }
    }
    fun validateAge() : Boolean{
        val age = etAge.text.toString().trim()
//        val dobRegex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}\$".toRegex()

        if (age.isEmpty()){
            tiAge.error = "Enter Your Age"
                return false
        }else if(age.toInt()>0){
            if(age.toInt()>100 || age.toInt()<5){
                tiAge.error = "You aren't eligible to use this app"
                return false
            }else{
                tiAge.isErrorEnabled = false
                tiAge.error=null
                return true
            }
        }
        tiAge.error = "Enter your Age correctly"
       return false
    }
    fun validateEmail() : Boolean{
        val email = etEmail.text.toString().trim()
        val emailRegex : Regex = "^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$".toRegex()
        if (email.isEmpty()){
            tiEmail.error = "Enter Your Email"
            return false
        }
        else if(!email.matches(emailRegex)){
            tiEmail.error = "Invalid Email Address"
            return false
        }
        else
        {
            tiEmail.isErrorEnabled = false
            tiEmail.error=null
            return true
        }
    }
    fun validatePassword() : Boolean{
        val password = etPassword.text.toString().trim()
        val passwordRegex : Regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,16}\$".toRegex()
//        Regex Conditions:
//        Min 1 uppercase letter.
//        Min 1 lowercase letter.
//        Min 1 special character.
//        Min 1 number.
//        Min 8 characters.
//        Max 30 characters.

        if (password.isEmpty()){
            tiPassword.error = "Enter Your Password"
            return false
        }
        else if(!password.matches(passwordRegex)) {
            tiPassword.error = "Must be 8-16 characters Long, Containing an UpperCase, Lowercase and Number"
            return false
        }
        else{
            tiPassword.isErrorEnabled = false
            tiPassword.error = null
            return true
        }
    }
    fun validateScholar() : Boolean{
        val emojiCode = "1F605" //Enter Code without u prefix
        val scholar = etScholar.text.toString().trim()
        if (scholar.isEmpty()){
            tiScholar.error = "Enter Your Scholar Number"
            return false
        } else if(scholar.toInt()>1000000000)
        {
            tiScholar.error = "Pls Enter Your Scholar Number Correctly "
            return false
        }
        else{
            tiScholar.isErrorEnabled = false
            tiScholar.error = null
            return true
        }


    }
    fun validateSemester() : Boolean{
        val emojiCode = "1F64B" //Enter Code without u prefix
        val semester = etSemester.text.toString().trim()
        if (semester.isEmpty()){
            tiSemester.error = "Enter your Semester Number"
                return false
        }else if(semester.toInt()>10)
        {
            tiSemester.error = "Pls Enter Your Semester Number Correctly "
            return false
        }
        else{
            tiSemester.isErrorEnabled = false
            tiSemester.error=null
            return true
        }
    }
    private fun getEmojiByUnicode(reactionCode: String): String {
        val code = reactionCode.toInt(16)
        return String(Character.toChars(code))
    }
    private fun emailToUserName(email : String ): String{
//        var count = 0
//        for (i in email){
//            if(i=='@'){
//                break
//            }
//            count++
//        }
//        var userName= email.slice(0 until count)
        var userName = email
        val regex = Regex("[^A-Za-z0-9]")
        userName = regex.replace(userName, "")
        return userName
    }
}

