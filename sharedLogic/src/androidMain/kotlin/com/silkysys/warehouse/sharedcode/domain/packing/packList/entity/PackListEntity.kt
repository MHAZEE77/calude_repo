package com.silkysys.warehouse.sharedcode.domain.packing.packList.entity


/**
 *
 * Created by: Mahmoud Ibrahim on 5/1/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
data class PackListEntity(
    val fulfillments: ArrayList<Fulfillments>
){
    data class Fulfillments(
        val id: Int,
        val name: String,
        val status: String,
        val created_at: String,
        val document: Document,
        val warehouse: Warehouse
    ){
        data class Warehouse(
            val name: String
        )
        data class Document(
            val document_name: String
        )
    }
}
