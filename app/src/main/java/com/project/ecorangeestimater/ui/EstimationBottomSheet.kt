package com.project.ecorangeestimater.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.project.ecorangeestimater.R
import com.project.ecorangeestimater.utils.spanned

class EstimationBottomSheet(
    private val stationName: String,
    private val stationMobile: String,
    private val location: String,
    private val chargingPoints: String,
    private val adapterTypes: List<String>,
    private val distance: String,
    private val estimation: String,
    private val suggestion: String,
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_estimation_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvStationName = view.findViewById<TextView>(R.id.tvStationName)
        val tvStationMobile = view.findViewById<TextView>(R.id.tvStationMobile)
        val tvStationLocation = view.findViewById<TextView>(R.id.tvStationLocation)
        val tvChargingPoints = view.findViewById<TextView>(R.id.tvChargingPoints)
        val tvAdapterTypes = view.findViewById<TextView>(R.id.tvAdapterTypes)
        val tvDistance = view.findViewById<TextView>(R.id.tvDistance)
        val tvEstimationMessage = view.findViewById<TextView>(R.id.tvEstimationMessage)
        val tvSuggestion = view.findViewById<TextView>(R.id.tvSuggestion)
        val btnCallStation = view.findViewById<Button>(R.id.btnCallStation)
        val btnClose = view.findViewById<Button>(R.id.btnClose)

        val numberedAdapterTypes = adapterTypes.mapIndexed { index, type ->
            "${index + 1}. $type"
        }.joinToString("<br>"                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      )


        tvStationName.text = spanned("<b><big>Station Name</big></b>: $stationName")
        tvStationMobile.text = spanned("<b><big>Mobile</big></b>: $stationMobile")
        tvStationLocation.text = spanned("<b><big>Location</big></b>: $location")
        tvChargingPoints.text = spanned("<b><big>Charging Points</big></b>: $chargingPoints")
        tvDistance.text = spanned("<b><big>Distance</big></b>: $distance")
        tvAdapterTypes.text = spanned("<b><big>Adapter Types</big></b>:<br>$numberedAdapterTypes")
        tvEstimationMessage.text = spanned("<b><big>Estimation</big></b>: $estimation")
        tvSuggestion.text = spanned("<b><big>Suggestion</big></b>: $suggestion")


        btnCallStation.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$stationMobile")
            }
            startActivity(intent)
        }


        btnClose.setOnClickListener {
            dismiss()
        }
    }
}
