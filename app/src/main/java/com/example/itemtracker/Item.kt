package com.example.itemtracker

class Item (
    val id: String,
    val name: String,
    val usedByDate: Long,
    val price: Double,
    val quantity: Int,
    val sku: Int,
    val department: String
)

{

    override fun toString(): String{
        return "id: $id, name: $name, Eat By: $usedByDate, price: $price," +
                " quantity: $quantity SKU: $sku dep $department"
    }
}