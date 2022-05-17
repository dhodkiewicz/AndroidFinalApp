package com.example.itemtracker

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add.*
import java.time.LocalDateTime
import java.util.*

/**
 * Activity that dictates what happens when items get added,
 * also utilizes Text to speech and AppCompatActivity - for purposes of being able to utilize
 * registerForActivityResult which under basic Activity() does not work
 */
class AddEntryActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var databaseHelper: DBHelper // late initialized field for our DBHelper
    lateinit var textToSpeech: TextToSpeech // field that will allow TextToSpeech processor
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent> // field for executing the activity result contract
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient // field for allowing communication with location provider

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //instantiate the DBHelper here with this context
        databaseHelper = DBHelper(this)

        // the same with TextToSpeech processor
        textToSpeech = TextToSpeech(this, this)

        // set the fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // sets the current date
        val date = LocalDateTime.now().toLocalDate().toString()
        etDate.setText(date)

        // set the text to speech button's onClickListener for text to speech to our speakOut method
        btnTxtToSpeech.setOnClickListener{
            speakOut()
        }

        // set the location button's onClickListener for text to speech to our speakOut method
        btnLocation.setOnClickListener{
            fetchLocation()
        }

        // initially not enabled here
        btnTxtToSpeech.isEnabled = false

        // set the save button's  onclick listener
        bSave.setOnClickListener {
            saveItem()
        }

        // set the cancel button's onclick listener
        bCancel.setOnClickListener {
            finish()
        }

        //sets the speech to text buttons onclick listener
        btnSpeechToTxt.setOnClickListener(View.OnClickListener {
            //bundle a bunch of different extras into an intent to launch
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Recording... :)")
            try{
                // try to launch the intent here
                activityResultLauncher.launch(intent)
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
            }catch(exp: ActivityNotFoundException){
                // if something went wrong or activity wasn't launched then something broke here
                Toast.makeText(this, "Device Does not Support", Toast.LENGTH_SHORT).show()
            }
        })

        // goes through and make sure that the activity results are good and data isn't null
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult? ->
            if(result!!.resultCode == RESULT_OK && result!!.data !=null){
                val spkText = result!!.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<Editable>
                etEntry.text = spkText[0] // set etEntry's text to the spoken text
            }
        }

    }

    // void method that reads the journal entry outloud
    private fun speakOut() {
        val textForSpeech = etEntry.text.toString()
        textToSpeech.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    //void method that saves a journal entry
    private fun saveItem() {
        var isValid = true // set that it's valid to true initially

        // error if journal entry is empty
       etEntry.error = if(etEntry?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        var invalidRating = ratingBar.numStars == 0 // can't have zero stars, we can't be that sad


        // if entry isn't empty, star rating isn't zero and permissions are granted for location
        if(isValid && !invalidRating && isPermissionGranted()){
            val entry = etEntry?.text.toString() // get etExtries text
            val date = etDate.text // get the text from the date
            val user = "12345" // setting a default user to simulate a uid
            val mood = ratingBar.rating // set the mood to the ratingBar's entered user rating

            var lat = tvLat.text.toString().toDouble() // get the latitude from the tvLat's text
            var long = tvLong.text.toString().toDouble() // get the longitude from tvLong's text

            var gson = Gson() // utilizing Google's Gson object for serialization
            val str= gson.toJson(Data(lat,long)) // take the lat and the long utilizing the data class Data and convert to JSON

            val db = databaseHelper.writableDatabase // open or create a database for writing

            // take all of the values that were entered in
            val values = ContentValues()
            values.put(EntryDBContract.iEntry.ENTRY,entry)
            values.put(EntryDBContract.iEntry.DATE,date.toString())
            values.put(EntryDBContract.iEntry.USERID, user)
            values.put(EntryDBContract.iEntry.MOOD, mood)
            values.put(EntryDBContract.iEntry.LOC, str)

            // now insert all of those values
           val result = db.insert(EntryDBContract.iEntry.TABLE_NAME, null, values)

            // so activity can return back to it's caller
            setResult(RESULT_OK, Intent())

            // toast message that the Journal entry was Added
            Toast.makeText(applicationContext, "Journal Entry Added", Toast.LENGTH_SHORT).show()
        }
        else{
            // Toast message for an invalid entry
            Toast.makeText(applicationContext, "Invalid Entry", Toast.LENGTH_SHORT).show()
        }
        // activity is finished okay to close
        finish()
    }

    // upon completion of speech synth initialization
    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val res= textToSpeech.setLanguage(Locale.getDefault()) // set default language here - could possibly make an  interpreter with this if different
            if(res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED){
                // if language data is missing or the language isn't supported - make a message
                Toast.makeText(this, "Language not supported", Toast.LENGTH_SHORT).show()
            }
            else{ // otherwise our button for speech to text is enabled
                btnTxtToSpeech.isEnabled = true
            }
        }else{ // if TextToSpeech = failed
            Toast.makeText(this, "Failed to initialize", Toast.LENGTH_SHORT).show()
        }
    }

    //Fetch the user's current location as long as permission settings are valid
    private fun fetchLocation() {

        val task = fusedLocationProviderClient.lastLocation // task for retrieving most recent location of user

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it!= null){
                // if the task returned isn't null (ie) last location
                this.tvLong.text = it.longitude.toString()
                this.tvLat.text = it.latitude.toString()
                task.isComplete
            }
        }
    }

    // checks to see if user's location permissions are granted - same method as above almost except that one's void
    private fun isPermissionGranted(): Boolean {

        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return false
        }
        task.addOnSuccessListener {
            if(it!= null){
                true
            }
        }
        return true
        task.isComplete
    }

}

