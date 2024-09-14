package com.gkprojects.bookimggym.logic

import android.content.Context
import android.util.Log
import com.gkprojects.bookimggym.data.entities.Bookings
import com.gkprojects.bookimggym.data.entities.GymServices
import com.gkprojects.bookimggym.data.entities.UserSubscriptions
import com.gkprojects.bookimggym.data.repo.ServiceRepository
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.File
import java.sql.Time

class BookingLogic(private val context: Context) {

    fun getBookings(filename: String, userName: String): List<Bookings> {
//       var  listBooking =ArrayList<Bookings>()
       val listBooking= readBookingsFromFile(filename) as ArrayList<Bookings>
        listBooking.filter { it.userName==userName }
        Log.d("BookingLogic", listBooking.toString())

        return listBooking
    }


    fun book(booking: Bookings, filename: String, filenameSubs:String): Boolean {
        Log.d("BookingLogicAdd", booking.toString())
        val existingBookings: MutableList<Bookings> = readBookingsFromFile(filename)

        val existingSubs : MutableList<UserSubscriptions> = readUserSubsFromFile(filenameSubs,booking.userName)

        val updatedSubs = existingSubs.map { userSub ->
            if (userSub.userName == booking.userName && userSub.packageName == booking.gymService) {
                userSub.copy(remainingSubs = userSub.remainingSubs - 1)  // Update the value you want, e.g., decrementing remainingSubs
            } else {
                userSub  // Keep other items unchanged
            }
        }.toMutableList()
        Log.d("BookingLogicAdd", updatedSubs.toString())
        existingBookings.add(booking)

        Log.d("BookingLogicAdd", existingBookings.toString())
        writeJsonToFile(filename, serializeBookingToJson(existingBookings))
        writeJsonToFile(filenameSubs, serializeSubsToJson(updatedSubs))

        return true
    }

    fun getUserSubscriptions(filename: String,userName: String): List<UserSubscriptions> {
//        val existingUsers: List<UserSubscriptions> =
        val listUserSubs = readUserSubsFromFile(filename,userName)

        return listUserSubs

    }

    private fun serializeBookingToJson(booking: List<Bookings>): String {
        val gson = Gson()
        return gson.toJson(booking)  // Serialize
    }

    private fun writeJsonToFile(filename: String, jsonString: String) {
        val file = File(context.filesDir, filename)
        Log.d("BookingLogic", jsonString)
        file.writeText(jsonString)
    }

    private fun readBookingsFromFile(filename: String): MutableList<Bookings> {
        val file = File(context.filesDir, filename)

        if (!file.exists()) {
            return mutableListOf()  // Return an empty list if file does not exist
        }

        val jsonString = file.readText()
        val gson = Gson()

        return try {
            // Deserialize the JSON
            val booking = object : TypeToken<MutableList<Bookings>>() {}.type
            gson.fromJson(jsonString, booking) ?: mutableListOf()  // Return deserialized list or empty list
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            mutableListOf()  // Return an empty list if there's an error
        }
    }

    private fun readUserSubsFromFile(filename: String, username: String): MutableList<UserSubscriptions> {
        val file = File(context.filesDir, filename)

        if (!file.exists()) {
            return mutableListOf()  // Return an empty list if the file does not exist
        }

        val jsonString = file.readText()
        val gson = Gson()

        return try {
            // Deserialize the JSON
            val userSubType = object : TypeToken<MutableList<UserSubscriptions>>() {}.type
            val allUserSubs: MutableList<UserSubscriptions> = gson.fromJson(jsonString, userSubType) ?: mutableListOf()

            // Filter the list based on the username and remaining subscriptions
            allUserSubs.filter { it.userName == username && it.remainingSubs > 0 }.toMutableList()
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            mutableListOf()  // Return an empty list if there's an error
        }
    }

    private fun serializeSubsToJson(subs: List<UserSubscriptions>): String {
        val gson = Gson()
        return gson.toJson(subs)  // Serialize
    }


}