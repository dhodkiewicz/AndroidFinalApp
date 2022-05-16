package com.example.itemtracker

import android.content.ContentValues
import com.example.itemtracker.ItemTrackerDBContract.*


object DataManager {

    fun fetchAllItems(databaseHelper: DatabaseHelper) : ArrayList<Item>{

        val items = ArrayList<Item>()

        val db = databaseHelper.readableDatabase

        val columns = arrayOf(
            ItemEntry.COLUMN_ID,
            ItemEntry.COLUMN_NAME,
            ItemEntry.COLUMN_PRICE,
            ItemEntry.COLUMN_QUANTITY,
            ItemEntry.COLUMN_SKU,
            ItemEntry.COLUMN_DATE,
            ItemEntry.COLUMN_DEPARTMENT
        )

        val cursor = db.query(
            ItemEntry.TABLE_NAME,
        columns,
        null,
        null,
        null,
        null,
        null)

        val idPos = cursor.getColumnIndex(ItemEntry.COLUMN_ID)
        val namePos = cursor.getColumnIndex(ItemEntry.COLUMN_NAME)
        val pricePos = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE)
        val quanPos = cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY)
        val skuPos = cursor.getColumnIndex(ItemEntry.COLUMN_SKU)
        val datePos = cursor.getColumnIndex(ItemEntry.COLUMN_DATE)
        val depPos = cursor.getColumnIndex(ItemEntry.COLUMN_DEPARTMENT)

        while(cursor.moveToNext()){

            val id = cursor.getString(idPos)
            val name = cursor.getString(namePos)
            val price = cursor.getString(pricePos).toDouble()
            val quan = cursor.getString(quanPos).toInt()
            val sku = cursor.getString(skuPos).toInt()
            val date = cursor.getLong(datePos)
            val dep = cursor.getString(depPos)

            items.add(Item(id,name,date,price,quan,sku,dep))
        }

        cursor.close()

        return items
    }

    fun fetchItem(dbHelper: DatabaseHelper, itemId: String) : Item?{

        val db = dbHelper.readableDatabase
        var item: Item? = null

        val columns = arrayOf(
            ItemEntry.COLUMN_NAME,
            ItemEntry.COLUMN_PRICE,
            ItemEntry.COLUMN_QUANTITY,
            ItemEntry.COLUMN_SKU,
            ItemEntry.COLUMN_DATE,
            ItemEntry.COLUMN_DEPARTMENT
        )

        val sel = ItemEntry.COLUMN_ID + " LIKE ? "

        val selArgs = arrayOf(itemId)

        val cursor = db.query(
            ItemEntry.TABLE_NAME,
            columns,
            sel,
            selArgs,
            null,
            null,
            null
        )

        val namePos = cursor.getColumnIndex(ItemEntry.COLUMN_NAME)
        val pricePos = cursor.getColumnIndex(ItemEntry.COLUMN_PRICE)
        val quanPos = cursor.getColumnIndex(ItemEntry.COLUMN_QUANTITY)
        val skuPos = cursor.getColumnIndex(ItemEntry.COLUMN_SKU)
        val datePos = cursor.getColumnIndex(ItemEntry.COLUMN_DATE)
        val depPos = cursor.getColumnIndex(ItemEntry.COLUMN_DEPARTMENT)

        while (cursor.moveToNext()){
            val name = cursor.getString(namePos)
            val price = cursor.getString(pricePos).toDouble()
            val quan = cursor.getString(quanPos).toInt()
            val sku = cursor.getString(skuPos).toInt()
            val date = cursor.getLong(datePos)
            val dep = cursor.getString(depPos)

            item = Item(itemId,name,date,price,quan,sku,dep)
        }

        cursor.close()
        return item

    }

    fun updateItem(dbHelper: DatabaseHelper, item: Item){

        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put(ItemEntry.COLUMN_NAME, item.name)
        values.put(ItemEntry.COLUMN_DEPARTMENT, item.department)
        values.put(ItemEntry.COLUMN_DATE, item.usedByDate)
        values.put(ItemEntry.COLUMN_PRICE, item.price)
        values.put(ItemEntry.COLUMN_QUANTITY, item.quantity)
        values.put(ItemEntry.COLUMN_SKU, item.sku)

        val sel = ItemEntry.COLUMN_ID + " LIKE ? "

        val selArgs = arrayOf(item.id)

        db.update(ItemEntry.TABLE_NAME, values, sel, selArgs)
    }

    fun deleteItem(dbHelper: DatabaseHelper, itemId: String) : Int{

        val db = dbHelper.writableDatabase

        val sel = ItemEntry.COLUMN_ID + " LIKE ? "

        val selArgs = arrayOf(itemId)

        return db.delete(ItemEntry.TABLE_NAME, sel, selArgs)
    }
}