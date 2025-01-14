package com.project.ecorangeestimater.model

enum class AdapterType(val displayName: String) {
    TYPE1("Type 1 (SAE J1772)"),
    TYPE2("Type 2 (Mennekes)"),
    CCS("CCS (Combined Charging System)"),
    CHADEMO("CHAdeMO"),
    TESLA("Tesla Connector"),
    GB_T("GB/T")
}