package com.gkprojects.bookimggym


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gkprojects.bookimggym.data.entities.Bookings

class ListAdapter (private var bookings: List<Bookings>) :
    RecyclerView.Adapter<ListAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val packageTextView: TextView = view.findViewById(R.id.bookingPackage)
        val timestampTextView: TextView = view.findViewById(R.id.bookingTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.booking_item, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]
        holder.packageTextView.text = booking.gymService
        holder.timestampTextView.text = booking.timeStamp
    }

    override fun getItemCount(): Int = bookings.size
    fun setData(newBookings: List<Bookings>) {
        bookings = newBookings
        notifyDataSetChanged() // Notify the adapter that data has changed
    }
}