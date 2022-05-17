package com.example.itemtracker


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class JournalListAdapter(
	private val context: Context
) : RecyclerView.Adapter<JournalListAdapter.ItemViewHolder>() {

	lateinit var entryList: ArrayList<Entry> // field for holding journal entry objects

	// overriding the onCreateView holder constructor
	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ItemViewHolder {

		val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
		return ItemViewHolder(itemView)
	}

	override fun getItemCount(): Int = entryList.size // get the item count from the entry list

	// upon binding the entries to the item view holder, pass the entryList position/index and the entries properties
	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val entry = entryList[position]
		holder.setData(entry.entry, entry.entryDate, entry.moodRating, entry.location,  position)
		holder.setListener()
	}

	// sets the items, not in the SQLlite database but in the field above
	fun setItems(entries: ArrayList<Entry>) {
		this.entryList = entries
		notifyDataSetChanged()
	}

	inner class ItemViewHolder(entryView: View)  : RecyclerView.ViewHolder(entryView) {

		var pos = 0
		// set the entries to the list view
		fun setData(entry: String, date: String, moodRating: Double, loc: Data, pos: Int){
			itemView.tvEntry.text = entry
			itemView.tvDate.text = date
			itemView.tvRating.text = moodRating.toString()
			itemView.tvGeo.text = "Latitude: ${loc.lat} Longitude ${loc.long}"

			this.pos = pos
		}

		// set the onclick listener for each item in the listview so when one is clicked, we get that particular entry
		fun setListener(){
			itemView.setOnClickListener{
				val intent = Intent(context, UpdateEntryActivity::class.java)
				intent.putExtra(EntryDBContract.iEntry.ID, entryList[pos].id)
				(context as Activity).startActivityForResult(intent, 2) //add was req code of 1
			}
		}
	}
}
