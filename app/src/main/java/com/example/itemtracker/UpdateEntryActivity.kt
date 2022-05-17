package com.example.itemtracker

import android.app.Activity
import android.app.AlertDialog
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
import java.util.*


class UpdateEntryActivity : AppCompatActivity(), TextToSpeech.OnInitListener{

    lateinit var databaseHelper : DBHelper // field for holding our DBHelper to allow us to work with the SQLite db
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent> // field for getting the results of a launched activity
    lateinit var textToSpeech: TextToSpeech // field for the speech to text synth
    var entryId: String? = null // field for an entries id
    var entry: Entry? = null // field for an entry object

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

         // instantiate both the DBHelper and TextToSpeech fields
        databaseHelper = DBHelper(this)
         textToSpeech = TextToSpeech(this, this)

         // get the intent extra's that started this activity
         val bundle = intent.extras

         // get's the values from the bundle if it's not null
         bundle?.let{
             entryId = bundle.getString(EntryDBContract.iEntry.ID) // get's the entries id from the bundle

             entry = DataManager.fetchEntry(databaseHelper, entryId!!) // gets the entry from the SQLite db

             // if the entry isn't null, update the activity_add page which also doubles as the modify entry xml view
             entry?.let{
                 etEntry.setText(entry!!.entry) // set the etEntry's text to the passed entries text
                 ratingBar.rating = entry!!.moodRating.toFloat() // set the ratingbar's rating from the passed/retrieved entry
                 etDate.setText(entry!!.entryDate) // set the etDate's text to the passed date
             }
         }

         //sets the text to speech buttons onclick listener
         btnTxtToSpeech.setOnClickListener(View.OnClickListener{
             speakOut() // execute the speakOut void method
         })

         // set the speech to text's onClick Listener
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

         // save the entry
        bSave.setOnClickListener {
            saveEntry()
        }

         // cancel modifying an entry
        bCancel.setOnClickListener {
            finish()
        }
    }


    // void method for saving an entry
    private fun saveEntry() {
        var isValid = true
        // if entry text isn't empty
        etEntry.error = if (etEntry?.text.toString().isEmpty()) {
            isValid = false
            "Required Field"
        } else null

        if (isValid) {
            // if entry is populated then we can accept the changes
            val newEntry = this.entry
            newEntry?.entry = etEntry.text.toString()
            newEntry?.moodRating = ratingBar.rating.toDouble()
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean{
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    //on Delete
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            // verify that the user wants to delete an entry
            R.id.action_delete -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.confirm_sure)
                    .setPositiveButton(R.string.yes){ dialog, itmId ->
                        val result = DataManager.deleteEntry(databaseHelper, this.entry!!)
                        // delete the entry
                        Toast.makeText(
                            applicationContext, "$result record(s) deleted",
                            Toast.LENGTH_SHORT
                        ).show()

                        setResult(Activity.RESULT_OK, Intent())
                        finish()
                    }
                        // if user doesn't want to delete the entry then dismiss the dialog message
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
    // void method that reads the journal entry outloud
    private fun speakOut() {
        val textForSpeech = etEntry.text.toString()
        textToSpeech.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    // upon completion of speech synth initialization
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