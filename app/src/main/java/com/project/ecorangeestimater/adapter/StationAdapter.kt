package com.project.ecorangeestimater.adapter

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.project.ecorangeestimater.databinding.ActivityViewListBinding
import com.project.ecorangeestimater.databinding.ViewstationBinding
import com.project.ecorangeestimater.model.Entries
import com.project.ecorangeestimater.model.EvStation
import com.project.ecorangeestimater.utils.LocationUtils
import com.project.ecorangeestimater.utils.spanned
import java.util.Locale

class StationAdapter(var list: List<EvStation>) :
    RecyclerView.Adapter<StationAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ListViewHolder(
        ViewstationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount() = list.size
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) =
        holder.bind(list[position])

    class ListViewHolder(val binding: ViewstationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: EvStation) {

            val latLng = item.location.split(",").map { it.trim().toDouble() }

            val address =
                LocationUtils.getAddressFromLatLng(binding.root.context, latLng[0], latLng[1])


            binding.stationName.text =
                spanned("<b><big>Station Name</big></b>: ${item.stationName}")
            binding.stationMobile.text =
                spanned("<b><big>Station Mobile</big></b>: ${item.stationMobile}")
            binding.stationEmail.text =
                spanned("<b><big>Charging Points</big></b>: ${item.chargingpoints}")
            binding.stationLocation.text = address
        }

    }

    fun newList(list: List<EvStation>) {
        this.list = list
        notifyDataSetChanged()

    }


}