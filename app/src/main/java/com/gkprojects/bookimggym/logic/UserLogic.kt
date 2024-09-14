package com.gkprojects.bookimggym.logic

import android.content.Context
import android.util.Log
import com.gkprojects.bookimggym.data.entities.Users
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File

class UserLogic(private val context: Context) {


    fun addUser(user: Users, filename: String) {
        //Read
        var existingUsers :MutableList<Users> = readUsersFromFile(filename)


        if (existingUsers.isEmpty()){
            existingUsers= emptyList<Users>().toMutableList()
            Log.d("usersLogIf","$existingUsers")
            existingUsers.add(user)

        }else{
            Log.d("usersLogElse","$existingUsers")
            existingUsers.add(user)
        }


        // Serialize
        writeJsonToFile(filename, serializeUsersToJson(existingUsers))
    }

    private fun serializeUsersToJson(users: List<Users>): String {
        val gson = Gson()
        return gson.toJson(users)  // Serialize
    }

    private fun writeJsonToFile(filename: String, jsonString: String) {
        val file = File(context.filesDir, filename)
        file.writeText(jsonString)
    }
    fun findUserByName(filename: String, searchName: String): Users? {
        // Read the list
        val users = readUsersFromFile(filename)
        Log.d("logJson","$users")

       //Search
        return users.find { it.name == searchName }
    }

    private fun readUsersFromFile(filename: String): MutableList<Users> {
        val file = File(context.filesDir, filename)

        // Check if the file exists
        if (!file.exists()) {
            return mutableListOf()
        }

        val jsonString = file.readText()
        val gson = Gson()

        return try {
            //
            val userType = object : TypeToken<MutableList<Users>>() {}.type
            gson.fromJson(jsonString, userType) ?: mutableListOf()
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            mutableListOf()
        }
    }

}