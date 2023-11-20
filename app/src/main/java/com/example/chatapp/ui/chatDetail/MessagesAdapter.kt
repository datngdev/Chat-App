package com.example.chatapp.ui.chatDetail

import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.datas.models.Message

class MessagesAdapter(
    private val currentUserId: String,
    private val dataset: List<Message>,
    private val callBack: MessagesAdapterCallBack
)  : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.item_message_senderName_txt)
        val avatar = view.findViewById<ImageView>(R.id.item_message_avatar_imv)
        val dataMess = view.findViewById<TextView>(R.id.item_chat_data_mess)
        val dataImage = view.findViewById<ImageView>(R.id.item_chat_data_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == 1) {
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_right, parent, false)
        } else {
            LayoutInflater.from(parent.context).inflate(R.layout.item_message_left, parent, false)
        }
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentUserId == dataset[position].sender) {
            1
        } else {
            0
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        holder.name.text = item.sender
        callBack.loadUserImage(item.sender, holder.avatar)

        if (item.type == 1) {
            holder.dataMess.visibility = View.VISIBLE
            holder.dataImage.visibility = View.GONE
            holder.dataMess.text = item.data
        } else if (item.type == 2) {
            holder.dataImage.visibility = View.VISIBLE
            holder.dataMess.visibility = View.GONE
            callBack.loadDataImage(item.data, holder.dataImage)
        }
    }

    interface MessagesAdapterCallBack {
        fun loadUserImage(userId: String, img: ImageView)
        fun loadDataImage(imageUrl: String, imageView: ImageView)
    }
}