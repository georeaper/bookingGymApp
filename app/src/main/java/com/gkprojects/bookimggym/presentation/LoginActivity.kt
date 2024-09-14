package com.gkprojects.bookimggym.presentation

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gkprojects.bookimggym.databinding.ActivityLoginBinding
import com.gkprojects.bookimggym.logic.UserLogic


class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private var fileUsers ="users.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val userData = UserLogic(this)
        binding.loginButton.setOnClickListener {

           val user= userData.findUserByName(fileUsers,binding.etLoginName.text.toString())
            if(user==null ){
                Toast.makeText(this,"$user",Toast.LENGTH_SHORT).show()
            }else
            {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_NAME", binding.etLoginName.text.toString())
                startActivity(intent)
            }


        }

        binding.signUpButton.setOnClickListener {

            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }
    }

}