package com.example.chatapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.datas.models.BoxChat


class BoxChatAdapter(
    private val dataset: List<BoxChat>,
    private val callBack: Callback
) : RecyclerView.Adapter<BoxChatAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.item_user_image)
        val name = view.findViewById<TextView>(R.id.item_user_name)
        val mess = view.findViewById<TextView>(R.id.item_mess)
        //val time = view.findViewById<TextView>(R.id.item_time_last_mess)
        //val messCount = view.findViewById<TextView>(R.id.item_mess_unseen_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_box_chat, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        callBack.loadBoxAvatar(holder.image, item.avatar)
        holder.name.text = item.name
        holder.mess.text = item.lastMess

        holder.itemView.setOnClickListener {
            callBack.onBoxClick(item.id)
        }
    }

    interface Callback{
        fun onBoxClick(boxId: String)
        fun loadBoxAvatar(img: ImageView, url: String)
    }
}