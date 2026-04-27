package com.example.painlessprep

/**
 * An object class meant for helping with csv formatting and whatnot.
 */
object CsvUtils {

    /**
     * Cleanly creates a csv string using a WindowData object
     *
     *@param[measurement] a WindowData object that holds the measurement name, width, and height
     */
    fun formatCsvString(measurement: WindowData) : String{
        val formattedMeasurement : String = "${measurement.name},${measurement.width},${measurement.height},${measurement.amount}\n"
        return formattedMeasurement
    }
}