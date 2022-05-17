package com.example.itemtracker

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
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

class AddItemActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var databaseHelper: DBHelper
    lateinit var textToSpeech: TextToSpeech
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        databaseHelper = DBHelper(this)
        textToSpeech = TextToSpeech(this, this)
        // on clicking ok on the calender dialog

        // set the fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        val date = LocalDateTime.now().toLocalDate().toString()
        etDate.setText(date)


        btnTxtToSpeech.setOnClickListener{
            speakOut()
        }

        btnLocation.setOnClickListener{
            fetchLocation()
        }

        btnTxtToSpeech.isEnabled = false

        bSave.setOnClickListener {
            saveItem()
        }

        bCancel.setOnClickListener {
            finish()
        }

        btnSpeechToTxt.setOnClickListener(View.OnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "SAY SOMETHING!!! GRAHHH!!")
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

    }


    private fun speakOut() {
        val textForSpeech = etEntry.text.toString()
        textToSpeech.speak(textForSpeech, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    private fun saveItem() {
        var isValid = true

       etEntry.error = if(etEntry?.text.toString().isEmpty()){
            isValid = false
            "Required Field"
        } else null

        var invalidRating = ratingBar.numStars == 0



        if(isValid && !invalidRating && isPermissionGranted()){
            val entry = etEntry?.text.toString()
            val date = etDate.text
            val user = "12345"
            val mood = ratingBar.rating
            val loc = Companion.userLocation

            val jsonString = returnJSON()

            val db = databaseHelper.writableDatabase

            val values = ContentValues()
            values.put(EntryDBContract.iEntry.ENTRY,entry)
            values.put(EntryDBContract.iEntry.DATE,date.toString())
            values.put(EntryDBContract.iEntry.USERID, user)
            values.put(EntryDBContract.iEntry.MOOD, mood)
            values.put(EntryDBContract.iEntry.LOC, jsonString)

           val result = db.insert(EntryDBContract.iEntry.TABLE_NAME, null, values)

            setResult(RESULT_OK, Intent())

            Toast.makeText(applicationContext, "Journal Entry Added", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(applicationContext, "Invalid Entry", Toast.LENGTH_SHORT).show()
        }

        finish()
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

    //Fetch the user's current location as long as permission settings are valid
    private fun fetchLocation() {

        val task = fusedLocationProviderClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 101)
            return
        }
        task.addOnSuccessListener {
            if(it!= null){
                Companion.userLocation = Location(it)
            }
        }
    }

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
    }

    companion object {
        lateinit var userLocation: Location
    }

    private fun returnJSON(): String {
        var lat = userLocation.latitude
        var long = userLocation.longitude
        var gson = Gson()
        return gson.toJson(Data(lat,long))
    }



}
