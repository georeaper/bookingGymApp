package com.gkprojects.bookimggym.logic

import android.content.Context
import android.util.Log
import com.gkprojects.bookimggym.data.entities.GymServices
import com.gkprojects.bookimggym.data.entities.Subscriptions
import com.gkprojects.bookimggym.data.entities.UserSubscriptions
import com.gkprojects.bookimggym.data.entities.Users
import com.gkprojects.bookimggym.data.repo.ServiceRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File

class SubscriptionLogic(private val context: Context)
{

    fun getAvailableSubs():List<Subscriptions>{
        var subscription= ArrayList<Subscriptions>()
        val serviceRepo =ServiceRepository()
        subscription=serviceRepo.getSubscriptionData() as ArrayList<Subscriptions>

        return  subscription as List<Subscriptions>

    }


    fun getServices():List<GymServices>{
        var listServices =ArrayList<GymServices>()
        val serviceRepo =ServiceRepository()
        listServices=serviceRepo.getServicesData() as ArrayList<GymServices>

        return listServices as List<GymServices>
    }
    fun addServiceToUser(userSub: UserSubscriptions, filename: String) :Boolean {
        // Read existing user subscriptions
        val existingUsers: MutableList<UserSubscriptions> = readUserSubsFromFile(filename)

        val filteredUsers = existingUsers.filter { userSubscription ->
            userSubscription.userName == userSub.userName && userSubscription.packageName == userSub.packageName && userSubscription.remainingSubs>0
        }

        if (filteredUsers.isNotEmpty()) {
            return false


        }else{
            // Add the new user subscription to the list
            existingUsers.add(userSub)

            // Write the updated list back to the file
            writeJsonToFile(filename, serializeUsersToJson(existingUsers))
            return true
        }


    }

    private fun serializeUsersToJson(userSubs: List<UserSubscriptions>): String {
        val gson = Gson()
        return gson.toJson(userSubs)  // Serialize
    }

    private fun writeJsonToFile(filename: String, jsonString: String) {
        val file = File(context.filesDir, filename)
        file.writeText(jsonString)
    }

    private fun readUserSubsFromFile(filename: String): MutableList<UserSubscriptions> {
        val file = File(context.filesDir, filename)

        if (!file.exists()) {
            return mutableListOf()  // Return an empty list if file does not exist
        }

        val jsonString = file.readText()
        val gson = Gson()

        return try {
            // Deserialize the JSON
            val userSub = object : TypeToken<MutableList<UserSubscriptions>>() {}.type
            gson.fromJson(jsonString, userSub) ?: mutableListOf()  // Return deserialized list or empty list
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            mutableListOf()  // Return an empty list if there's an error
        }
    }

}