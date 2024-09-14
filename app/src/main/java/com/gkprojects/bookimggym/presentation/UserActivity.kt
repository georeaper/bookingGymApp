package com.gkprojects.bookimggym.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gkprojects.bookimggym.data.entities.Users
import com.gkprojects.bookimggym.databinding.ActivityUserInputBinding
import com.gkprojects.bookimggym.logic.UserLogic

class UserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUserInputBinding
    private var filenameUser= "user.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInputBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val userData =UserLogic(this)
        binding.btnSubmit.setOnClickListener {
            val user = Users(binding.etName.text.toString(),
                binding.etAge.text.toString().toIntOrNull(),
                binding.etEmail.text.toString(),
                binding.etPhone.text.toString()
            )
        userData.addUser(user,filenameUser)
        }

    }
}