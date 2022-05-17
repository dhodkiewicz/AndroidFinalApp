package com.example.itemtracker

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add.*
import java.text.SimpleDateFormat
import java.util.*


class UpdateItemActivity : AppCompatActivity(), TextToSpeech.OnInitListener{

    lateinit var databaseHelper : DBHelper
    private val myCalendar = Calendar.getInstance()
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var textToSpeech: TextToSpeech



    var entryId: String? = null
    var entry: Entry? = null

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        databaseHelper = DBHelper(this)
         textToSpeech = TextToSpeech(this, this)



         val bundle = intent.extras

         bundle?.let{
             entryId = bundle.getString(EntryDBContract.iEntry.ID)

             entry = DataManager.fetchEntry(databaseHelper, entryId!!)

             entry?.let{
                 etEntry.setText(entry!!.entry)
                 ratingBar.rating = entry!!.moodRating.toFloat()
                 etDate.setText(entry!!.entryDate)
             }
         }

        // on clicking ok on the calender dialog
        val date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            etDate.setText(getFormattedDate(myCalendar.timeInMillis))
        }

         btnTxtToSpeech.setOnClickListener(View.OnClickListener{
             speakOut()
         })

         btnSpeechToTxt.setOnClickListener(View.OnClickListener {
             val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
             intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
             intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
             intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Recording Now...")
             try{
                 activityResultLauncher.launch(intent)
                 Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
             }catch(exp: ActivityNotFoundException){
                 Toast.makeText(this, "Device Does not Support", Toast.LENGTH_SHORT).show()
             }
         })

         activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                 result: ActivityResult? ->
             if(result!!.resultCode == RESULT_OK && result!!.data !=null){
                 val spkText = result!!.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<Editable>
                 etEntry.text = spkText[0]
             }
         }

        etDate.setOnClickListener {
            setUpCalender(date)
        }

        bSave.setOnClickListener {
            saveEntry()
        }

        bCancel.setOnClickListener {
            finish()
        }
    }



    private fun saveEntry() {

        var isValid = true

        etEntry.error = if (etEntry?.text.toString().isEmpty()) {
            isValid = false
            "Required Field"
        } else null


        if (isValid) {

            val newEntry = this.entry
            newEntry?.entry = etEntry.text.toString()
            if (newEntry != null) {
                DataManager.updateEntry(databaseHelper, newEntry)
            }
            else{
                Toast.makeText(applicationContext, "Entry is null here", Toast.LENGTH_SHORT).show()
            }

            setResult(Activity.RESULT_OK, Intent())

            Toast.makeText(applicationContext, "Item Updated", Toast.LENGTH_SHORT).show()

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

    //on Delete
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){

            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.confirm_sure)
                    .setPositiveButton(R.string.yes){ dialog, itmId ->
                        val result = DataManager.deleteEntry(databaseHelper, this.entry!!)

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

    private fun speakOut() {
        val textForSpeech = etEntry.text.toString()
        textToSpeech.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val res= textToSpeech.setLanguage(Locale.getDefault())
            if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED){
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
            }
            else{
                btnTxtToSpeech.isEnabled = true
            }
        }else{
            Toast.makeText(this, "Failed to initialize", Toast.LENGTH_SHORT).show()
        }
    }
}