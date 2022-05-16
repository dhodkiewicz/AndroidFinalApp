package com.example.itemtracker

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add.*
import java.text.SimpleDateFormat
import java.util.*

class AddItemActivity : Activity() {

    private val myCalendar = Calendar.getInstance()
    private lateinit var databaseHelper: DatabaseHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        databaseHelper = DatabaseHelper(this)

        // on clicking ok on the calender dialog
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etExp.setText(getFormattedDate(myCalendar.timeInMillis))
        }

        etExp.setOnClickListener {
            setUpCalender(date)
        }

        bSave.setOnClickListener {
            saveItem()
        }

        bCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveItem() {
        var isValid = true

       etItemName.error = if(etItemName?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        etDepartment.error = if(etDepartment?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        etPrice.error = if(etPrice?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        etQuantity.error = if(etQuantity?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        etSku.error = if(etSku?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null


        if(isValid){
            val name = etItemName?.text.toString()
            val department = etDepartment?.text.toString()
            val expiry = myCalendar.timeInMillis.toString()
            val price = etPrice?.text.toString()
            val quan = etQuantity?.text.toString()
            val sku = etSku?.text.toString()

            val db = databaseHelper.writableDatabase

            val values = ContentValues()
            values.put(ItemTrackerDBContract.ItemEntry.COLUMN_NAME, name)
            values.put(ItemTrackerDBContract.ItemEntry.COLUMN_DEPARTMENT, department)
            values.put(ItemTrackerDBContract.ItemEntry.COLUMN_DATE, expiry)
            values.put(ItemTrackerDBContract.ItemEntry.COLUMN_PRICE, price)
            values.put(ItemTrackerDBContract.ItemEntry.COLUMN_QUANTITY, quan)
            values.put(ItemTrackerDBContract.ItemEntry.COLUMN_SKU, sku)


           val result = db.insert(ItemTrackerDBContract.ItemEntry.TABLE_NAME, null, values)

            setResult(RESULT_OK, Intent())

            Toast.makeText(applicationContext, "Item Added", Toast.LENGTH_SHORT).show()
        }

        finish()
    }

    private fun setUpCalender(date: DatePickerDialog.OnDateSetListener) {

        DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getFormattedDate(dobInMilis: Long?): String {

        return dobInMilis?.let {
            val sdf = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
            sdf.format(dobInMilis)
        } ?: "Not Found"
    }
}
