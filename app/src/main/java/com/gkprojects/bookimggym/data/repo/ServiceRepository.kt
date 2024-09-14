package com.gkprojects.bookimggym.data.repo

import com.gkprojects.bookimggym.data.entities.GymServices
import com.gkprojects.bookimggym.data.entities.Subscriptions
import java.util.concurrent.Flow

class ServiceRepository {

    fun getServicesData(): List<GymServices>{
        val list = ArrayList<GymServices>()
        list.add(
            GymServices("Swimming",6,
                listOf("Monday 10-12",
                    "Monday 15-17",
                    "Tuesday 18-20")))

        list.add(GymServices("Folklore Dances",6,
            listOf("Monday 12-14",
                "Monday 11-15",
                "Thursday 18-20")))

        list.add(GymServices("Cross Fit",6,
            listOf("Wednesday 15-18",
                "Friday 11-15",
                "Friday 18-20")))

        return list

    }

    fun getSubscriptionData():List<Subscriptions>{
        var list = ArrayList<Subscriptions>()
        list.add(Subscriptions("Package of 6", 6))
        list.add(Subscriptions("Package of 15",15))

        return list
    }
}