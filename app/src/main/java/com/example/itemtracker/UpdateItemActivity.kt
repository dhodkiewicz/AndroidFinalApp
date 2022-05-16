package com.example.itemtracker

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add.*
import java.text.SimpleDateFormat
import java.util.*


class UpdateItemActivity : AppCompatActivity(){

    lateinit var databaseHelper : DBHelper
    private val myCalendar = Calendar.getInstance()

    var entryId: String? = null

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        databaseHelper = DBHelper(this)

         val bundle = intent.extras

         bundle?.let{
             entryId = bundle.getString(EntryDBContract.iEntry.ID)

             val entry = DataManager.fetchEntry(databaseHelper, entryId!!)

             entry?.let{

                 etEntry.setText(entry.entry)
                 ratingBar.rating = entry.moodRating.toFloat()
                 etDate.setText(entry.entryDate)
             }
         }

        // on clicking ok on the calender dialog
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etDate.setText(getFormattedDate(myCalendar.timeInMillis))
        }

        etDate.setOnClickListener {
            setUpCalender(date)
        }

        bSave.setOnClickListener {
            saveEmployee()
        }

        bCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveEmployee() {

        var isValid = true

        etEntry.error = if (etEntry?.text.toString().isEmpty()) {
            isValid = false
            "Required Field"
        } else null


        if (isValid) {

//            val uName = etEntry.text.toString()
//            val uDept = etDepartment.text.toString()
//            val uPrice = etPrice.text.toString().toDouble()
//            val uQuan = etQuantity.text.toString().toInt()
//            val uSku = etSku.text.toString().toInt()
//            val uDate = myCalendar.timeInMillis
//
//            val uItem = Item(itemId!!, uName, uDate, uPrice, uQuan, uSku, uDept)
//
//            DataManager.updateItem(databaseHelper, uItem)
//
//            setResult(Activity.RESULT_OK, Intent())
//
//            Toast.makeText(applicationContext, "Item Updated", Toast.LENGTH_SHORT).show()

            finish()
        }
    }

    private fun setUpCalender(date: DatePickerDialog.OnDateSetListener) {
        DatePickerDialog(
            this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun getFormattedDate(dateInMilis: Long?): String {

        return dateInMilis?.let {
            val sdf = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
            sdf.format(dateInMilis)
        } ?: "Not Found"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){

            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.confirm_sure)
                    .setPositiveButton(R.string.yes){ dialog, itmId ->
                        val result = DataManager.deleteEntry(databaseHelper, item.itemId.toString())

                        Toast.makeText(
                            applicationContext, "$result record(s) deleted",
                            Toast.LENGTH_SHORT
                        ).show()

                        setResult(Activity.RESULT_OK, Intent())
                        finish()
                    }
                    .setNegativeButton(R.string.no){ dialog, id ->
                        dialog.dismiss()
                    }

                val dialog = builder.create()
                dialog.setTitle("Are you sure?")
                dialog.show()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}