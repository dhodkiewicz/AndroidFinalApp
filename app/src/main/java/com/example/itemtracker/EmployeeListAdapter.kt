package com.example.itemtracker


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*

class ItemListAdapter(
	private val context: Context
) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

	lateinit var itemList: ArrayList<Item>


	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): ItemViewHolder {

		val itemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
		return ItemViewHolder(itemView)
	}

	override fun getItemCount(): Int = itemList.size

	override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
		val item = itemList[position]
		holder.setData(item.name, item.department, item.price, item.quantity, position)
		holder.setListener()
	}

	fun setItems(items: ArrayList<Item>) {
		this.itemList = items
		notifyDataSetChanged()
	}

	inner class ItemViewHolder(itemView: View)  : RecyclerView.ViewHolder(itemView) {

		var pos = 0

		fun setData(name: String, department: String, price: Double, quantity: Int, pos: Int){
			itemView.tvItemName.text = name
			itemView.tvItemDepartment.text = department
			itemView.tvItemPrice.text = "$" + price
			itemView.tvItemQuantity.text = quantity.toString()

			val num = price * quantity
			val formatted = "$" + String.format("%.2f", num)
			itemView.tvTotalCost.text = formatted
			this.pos = pos
		}

		fun setListener(){
			itemView.setOnClickListener{
				val intent = Intent(context, UpdateItemActivity::class.java)
				intent.putExtra(ItemTrackerDBContract.ItemEntry.COLUMN_ID, itemList[pos].id)
				(context as Activity).startActivityForResult(intent, 2) //add was req code of 1
			}
		}
	}
}
