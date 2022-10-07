package com.mact.proxyproof

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*


class Home : AppCompatActivity() {
    var htu: Button? = null
    var contact: Button? = null
    var back: Button? = null
    private lateinit var user : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        htu = findViewById<View>(R.id.btnHowToUse) as Button
        back = findViewById<View>(R.id.btnBack) as Button
        contact = findViewById<View>(R.id.btnContact) as Button
        val url = getString(R.string.firebase_db_location)
        user = FirebaseAuth.getInstance()
        htu!!.setOnClickListener { view: View? ->
            val i = Intent(this@Home, SliderActivity::class.java)
            startActivity(i)
            finish()
        }
        back!!.setOnClickListener { view: View? ->
            val i = Intent(this@Home, CameraActivity::class.java)
            startActivity(i)
            finish()
        }
        contact!!.setOnClickListener { view: View? ->
            val uri =
                Uri.parse("http://demonboyiscurrentlylive.on.drv.tw/www.B2SCam.com/") // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        btnLogout.setOnClickListener{
            Log.d("userCurrentAtProfile", user.currentUser?.email.toString()+" has Logged Out")
            user.signOut()
            Intent(this,LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                overridePendingTransition(R.anim.fadein_animation, R.anim.fadeout_animation)
                finish()
            }
        }
    }
}