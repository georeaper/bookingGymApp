package com.gkprojects.bookimggym.presentation

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gkprojects.bookimggym.ListAdapter
import com.gkprojects.bookimggym.data.entities.Bookings

import com.gkprojects.bookimggym.data.entities.UserSubscriptions
import com.gkprojects.bookimggym.data.repo.ServiceRepository
import com.gkprojects.bookimggym.databinding.ActivityMainBinding
import com.gkprojects.bookimggym.databinding.DialogBookingBinding
import com.gkprojects.bookimggym.databinding.DialogSubscriptionPackageBinding
import com.gkprojects.bookimggym.logic.BookingLogic
import com.gkprojects.bookimggym.logic.SubscriptionLogic

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var dialogView : DialogSubscriptionPackageBinding
    private lateinit var dialogBooking : DialogBookingBinding
    private lateinit var userNameGlobal :String
    private lateinit var adapterBooking :ListAdapter
    private var filenameBooking="BookingData.json"
    private var filenameSubscription="SubscriptionData.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        dialogView = DialogSubscriptionPackageBinding.inflate(layoutInflater)
        dialogBooking = DialogBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapterBooking=ListAdapter(emptyList())
        userNameGlobal = intent.getStringExtra("USER_NAME").toString()


        binding.subscriptionButton.setOnClickListener {

            openSubscriptionPackage()

        }

        binding.floatingBtnMain.setOnClickListener {
            showSubscriptionDialog()

        }
        populateBookingsListView(userNameGlobal)


    }
    private fun populateBookingsListView(username :String) {
        val filename = filenameBooking
        val userName = username // Use the actual userName

        val bookingLogic = BookingLogic(this)
        val bookings = bookingLogic.getBookings(filename, userName)

        val recyclerView = binding.listViewBookings // Ensure this ID matches your RecyclerView ID

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapterBooking
        adapterBooking.setData(bookings)
    }

    private fun showSubscriptionDialog() {
        // Inflate the dialog layout using ViewBinding or LayoutInflater
        dialogBooking= DialogBookingBinding.inflate(layoutInflater)
        val spinnerPackage = dialogBooking.spinnerPackage
        val spinnerTimestamp = dialogBooking.spinnerTimestamp

        // Define the available data
        val bookingLogic = BookingLogic(this)
        val userSubscriptions = bookingLogic.getUserSubscriptions(filenameSubscription,
            userNameGlobal
        )
        val packages = userSubscriptions.map{it.packageName}
        val timestamps = mutableListOf<String>()

        // Set the adapter for the package spinner
        spinnerPackage.adapter = ArrayAdapter(this, R.layout.simple_spinner_item, packages)
        spinnerPackage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPackage = packages[position]
                updateTimestamps(selectedPackage) { updatedtimestamps ->
                    timestamps.clear()
                    timestamps.addAll(updatedtimestamps)
                    spinnerTimestamp.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, timestamps)
                    spinnerTimestamp.setSelection(0)

                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }


        AlertDialog.Builder(this)
            .setTitle("Select Options")
            .setView(dialogBooking.root)
            .setPositiveButton("OK") { dialog, _ ->
                val selectedPackage = spinnerPackage.selectedItem.toString()
                val selectedService = spinnerTimestamp.selectedItem.toString()
                // Handle the selections
                handleSelections(selectedPackage, selectedService)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }


    private fun updateTimestamps(serviceName: String, callback: (List<String>) -> Unit) {
        val repo = ServiceRepository()
        val allServices = repo.getServicesData() // Retrieve all services
        val selectedService = allServices.find { it.name == serviceName }  // Find the selected service

        if (selectedService != null) {
            // Return the timestamps for the selected service
            callback(selectedService.timeStamps)
        } else {
            callback(emptyList())  // Return an empty list if no service is found
        }
    }

    private fun handleSelections(selectedPackage: String, selectedTimestamp: String) {
        val booking=Bookings(userNameGlobal,selectedPackage,selectedTimestamp)
        Log.d("MainActivity2", "Package: $selectedPackage, Timestamp: $selectedTimestamp")
        val subLogic = BookingLogic(this)
        subLogic.book(booking,filenameBooking,filenameSubscription)
        populateBookingsListView(userNameGlobal)

        }

    private fun openSubscriptionPackage() {
        //using viewbinding
        dialogView = DialogSubscriptionPackageBinding.inflate(layoutInflater)
        // Find the spinners from the inflated layout
        val gymServicesSpinner = dialogView.gymServicesSpinner
        val subscriptionSpinner = dialogView.subscriptionSpinner
        val subs =SubscriptionLogic(this)
        // Available gym services and subscription packages
        val allServices =subs.getServices()
        val gymServices =allServices.map { it.name }
        val allSubscription =subs.getAvailableSubs()
        val subscriptions = allSubscription.map { it.subscriptionName }

        // Set the spinners' adapters
        gymServicesSpinner.adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, gymServices)
        subscriptionSpinner.adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, subscriptions)

        // Build and show the AlertDialog
        AlertDialog.Builder(this)
            .setTitle("Choose Subscription Package")
            .setView(dialogView.root)  // Set the custom layout as the dialog's view
            .setPositiveButton("OK") { dialog, _ ->
                // Get the selected gym service and subscription
                val selectedGymService = gymServicesSpinner.selectedItem.toString()
                val selectedSubscription = subscriptionSpinner.selectedItem.toString()
                val selectedPackage = allSubscription.find { it.subscriptionName == selectedSubscription }


                val userSub= UserSubscriptions(userNameGlobal,gymServicesSpinner.selectedItem.toString(),selectedPackage!!.visits)
                Log.d("SubscriptionAdded", " $userSub")
                val logic = SubscriptionLogic(this)
                if(logic.addServiceToUser(userSub = userSub,filenameSubscription)){
                    Toast.makeText(this, "Subscription Added", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Subscription Not Added", Toast.LENGTH_SHORT).show()
                }



                // Combine the selections into a result and show it
                val result = "$selectedGymService $selectedSubscription"
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()  // Close
            }
            .show()
    }



}