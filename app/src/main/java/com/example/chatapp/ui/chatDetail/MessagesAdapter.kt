package com.example.chatapp.ui.chatDetail

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.datas.models.Message
import org.w3c.dom.Text

class MessagesAdapter(
    private val dataset: List<Message>
)  : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.item_chat_name)
        val avatar = view.findViewById<ImageView>(R.id.item_chat_avatar)
        val data = view.findViewById<TextView>(R.id.item_chat_data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message_left, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        holder.name.text = item.sender
        holder.data.text = item.data
    }
}