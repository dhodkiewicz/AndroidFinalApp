package com.example.itemtracker


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.list_item.view.*

class JournalListAdapter(
	private val context: Context
) : RecyclerView.Adapter<JournalListAdapter.ItemViewHolder>() {

	lateinit var entryList: ArrayList<Entry>


	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ItemViewHolder {

		val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
		return ItemViewHolder(itemView)
	}

	override fun getItemCount(): Int = entryList.size

	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val entry = entryList[position]
		holder.setData(entry.entry, entry.entryDate, entry.moodRating, entry.location,  position)
		holder.setListener()
	}

	fun setItems(entries: ArrayList<Entry>) {
		this.entryList = entries
		notifyDataSetChanged()
	}

	inner class ItemViewHolder(entryView: View)  : RecyclerView.ViewHolder(entryView) {

		var pos = 0

		fun setData(entry: String, date: String, moodRating: Double, loc: Data, pos: Int){
			itemView.tvEntry.text = entry
			itemView.tvDate.text = date
			itemView.tvRating.text = moodRating.toString()
			itemView.tvGeo.text = "Latitude: ${loc.lat} Longitude ${loc.long}"

			this.pos = pos
		}

		fun setListener(){
			itemView.setOnClickListener{
				val intent = Intent(context, UpdateItemActivity::class.java)
				intent.putExtra(EntryDBContract.iEntry.ID, entryList[pos].id)
				(context as Activity).startActivityForResult(intent, 2) //add was req code of 1
			}
		}
	}
}
