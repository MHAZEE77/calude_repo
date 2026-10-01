package com.silkysys.warehouse.sharedcode.data.packing.packList.dto

import kotlinx.serialization.SerialName


/**
 *
 * Created by: Mahmoud Ibrahim on 5/1/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/
data class PackResponse(
    @SerialName("fulfillments")
    val fulfillments: List<Fulfillments>
){
    data class Fulfillments(
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String,
        @SerialName("status")
        val status: String,
        @SerialName("created_at")
        val created_at: String,
        @SerialName("warehouse")
        val warehouse: Warehouse,
        @SerialName("document")
        val document: Document
    ){
        data class Document(
            @SerialName("document_number")
            val document_number: String
        )

        data class Warehouse(
            @SerialName("name")
            val name: String
        )
    }
}
