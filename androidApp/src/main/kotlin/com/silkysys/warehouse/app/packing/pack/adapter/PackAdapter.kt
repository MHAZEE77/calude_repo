package com.silkysys.warehouse.app.packing.pack.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.domain.packing.packList.entity.PackListEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

/**
 *
 * Created by: Mahmoud Ibrahim on 5/2/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/

class PackAdapter(private val packList: ArrayList<PackListEntity.Fulfillments>
                  ,private val onClickListener: OnClickListener)
    : RecyclerView.Adapter<PackAdapter.PackViewHolder>() {
        private var filteredPackList: ArrayList<PackListEntity.Fulfillments> = ArrayList()
        private var tempPackList: ArrayList<PackListEntity.Fulfillments> = ArrayList()

    init {
        // save all items in temp list
        tempPackList.addAll(packList)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pack_item,parent,false)
        return PackViewHolder(view)
    }
    override fun getItemCount(): Int {
       return packList.size
    }
    interface OnClickListener {
        fun onClickPackItem(packItem: PackListEntity.Fulfillments)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: PackViewHolder, position: Int) {
        holder.packDocumentNameTxt.text= packList[position].document.document_name
        holder.packOrderIdTxt.text= packList[position].name
        val date= LocalDate.parse(packList[position].created_at,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        holder.packDateTxt.text= date.toString()
        holder.packStatusTxt.text= packList[position].status
        holder.packBranchNameTxt.text= packList[position].warehouse.name
        holder.itemView.setOnClickListener {
            onClickListener.onClickPackItem(packList[position])
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filter(query: String) {
        packList.clear()
        packList.addAll(tempPackList)
        notifyDataSetChanged()
        if (query.isNotEmpty()){
            filteredPackList.clear()
            if (packList.isEmpty()){
                packList.addAll(tempPackList)
            }
            for (item in packList){
                if (item.name.contains(query)||
                    item.name.lowercase(Locale.ROOT).contains(query)||
                    item.name.uppercase(Locale.ROOT).contains(query)
                ){
                    filteredPackList.add(item)
                }
            }
            packList.clear()
            packList.addAll(filteredPackList)
            notifyDataSetChanged()
        }else{
            packList.clear()
            packList.addAll(tempPackList)
            notifyDataSetChanged()
        }

    }

    class PackViewHolder(itemView: View): ViewHolder(itemView) {
        val packDocumentNameTxt: TextView = itemView.findViewById(R.id.tv_docName)
        val packOrderIdTxt: TextView  = itemView.findViewById(R.id.tv_orderId)
        val packBranchNameTxt: TextView = itemView.findViewById(R.id.tv_branchName)
        val packStatusTxt: TextView = itemView.findViewById(R.id.tv_packStatus)
        val packDateTxt: TextView = itemView.findViewById(R.id.tv_packDate)
    }

}
