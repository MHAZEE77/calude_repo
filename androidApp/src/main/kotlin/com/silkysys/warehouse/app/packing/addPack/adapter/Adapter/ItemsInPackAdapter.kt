package com.silkysys.warehouse.app.packing.addPack.adapter.Adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.silkysys.warehouse.R
import com.silkysys.warehouse.sharedcode.data.packing.addPack.dto.AddPackRequest
import com.silkysys.warehouse.sharedcode.domain.packing.packItems.entity.PackItemsEntity

/**
 *
 * Created by: Mahmoud Ibrahim on 5/10/2023
 * Email: mahmoudibrahiem57@gmail.com
 *
 **/

class ItemsInPackAdapter(private val items: ArrayList<PackItemsEntity.PackItems>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var packItems: ArrayList<AddPackRequest.Items> = ArrayList<AddPackRequest.Items>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.add_pack_header, parent, false)
                  HeaderViewHolder(parent.context, view)
            }
            else -> {
                val view= LayoutInflater.from(parent.context).inflate(R.layout.items_in_pack,parent,false)
                ItemsViewHolder(parent.context,view)
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if(position == 0) { 1 } else { 2 }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is ItemsViewHolder){
            holder.itemId.text=items[position-1].id.toString()
            holder.itemName.text=items[position-1].name
            holder.allowableQ.text=items[position-1].allowedQuantity.toString()
            if(items[position-1].allowedQuantity==0) {
                holder.packedQ.isEnabled = false
                holder.increasePackQuantity.isEnabled=false
                holder.decreasePackQuantity.isEnabled=false
            }
            holder.increasePackQuantity.setOnClickListener {
                holder.packedQ.error=null
                if (holder.packedQ.text.toString().isNotEmpty()) {
                    var packed=  holder.packedQ.text.toString().toInt()
                    if (holder.packedQ.text.toString().toInt() == holder.allowableQ.text.toString().toInt()) {
//                        holder.packedQ.error = "Invalid Quantity"
                    }else {
                        packed++
                        holder.packedQ.setText(packed.toString())
                    }
                }
            }
            holder.decreasePackQuantity.setOnClickListener {
                holder.packedQ.error=null
                if (holder.packedQ.text.toString().isNotEmpty()) {
                    var packed=  holder.packedQ.text.toString().toInt()
                    if (holder.packedQ.text.toString().toInt() <= 0) {
//                        holder.packedQ.error = "Invalid Quantity"
                    }else {
                        packed--
                        holder.packedQ.setText(packed.toString())
                    }
                }
            }
            holder.packedQ.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
                override fun afterTextChanged(p0: Editable?) {
                    if (holder.packedQ.text.isNotEmpty()&&holder.itemId.text.isNotEmpty()) {
                        // remove redundant
                        for (i in 0 until packItems.size) {
                            if (holder.itemId.text.toString().toInt() == packItems[i].id) {
                                packItems.remove(packItems[i])
                                break
                            }
                        }
                        // add not duplicated items
                        if (p0.toString().toInt()>holder.allowableQ.text.toString().toInt()){
                            holder.packedQ.error = "Invalid Quantity"
                        }else {
                            packItems.add(AddPackRequest.Items(
                                holder.itemId.text.toString().toInt(),
                                holder.packedQ.text.toString().toInt()
                            )
                            )
                        }
                    }
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return items.size+1
    }
    fun getAllPackedItems(): ArrayList<AddPackRequest.Items>{
        return packItems
    }
    inner class HeaderViewHolder(val context: Context, view:View): RecyclerView.ViewHolder(view)

    inner class ItemsViewHolder(val context: Context, view: View): RecyclerView.ViewHolder(view) {
        val itemId: TextView= itemView.findViewById(R.id.tv_id)
        val itemName: TextView= itemView.findViewById(R.id.tv_itemName)
        val allowableQ: TextView= itemView.findViewById(R.id.tv_allowable)
        val packedQ: EditText= itemView.findViewById(R.id.et_packedQ)
        val increasePackQuantity: ImageView= itemView.findViewById(R.id.iv_increasePack)
        val decreasePackQuantity: ImageView= itemView.findViewById(R.id.iv_decreasePack)
    }
}
