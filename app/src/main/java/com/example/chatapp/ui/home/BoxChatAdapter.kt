package com.example.chatapp.ui.home

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.datas.models.BoxChat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


class BoxChatAdapter(
    private val dataset: List<BoxChat>,
    private val unseenList: MutableList<Map<String, String>>,
    private val callBack: Callback
) : RecyclerView.Adapter<BoxChatAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.item_user_image)
        val name = view.findViewById<TextView>(R.id.item_user_name)
        val mess = view.findViewById<TextView>(R.id.item_mess)
        val time = view.findViewById<TextView>(R.id.item_time_last_mess)
        val unseenCount = view.findViewById<TextView>(R.id.item_mess_unseen_count)
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

        val timeSend = processMessTime(item.lastSendTime)
        holder.time.text = timeSend

        val unSeenCount = isUnseen(item.id)
        if (unSeenCount != 0) {
            holder.unseenCount.visibility = View.VISIBLE
            holder.unseenCount.text = unSeenCount.toString()
            val unseenColor = ContextCompat.getColor(holder.itemView.context, R.color.white)
            holder.name.setTextColor(unseenColor)
            holder.mess.setTextColor(unseenColor)
        } else {
            holder.unseenCount.visibility = View.INVISIBLE
            holder.unseenCount.text = ""
            val stockColor = ContextCompat.getColor(holder.itemView.context, R.color.item_text_color)
            holder.name.setTextColor(stockColor)
            holder.mess.setTextColor(stockColor)
        }
    }

    private fun isUnseen(boxId: String): Int {
        unseenList.forEach { unseenMap ->
            if (unseenMap.containsKey(boxId)) {
                return unseenMap[boxId]!!.toInt()
            }
        }
        return 0
    }

    private fun processMessTime(timeStamp: String): String {
        var resultTime = ""

        if (!timeStamp.isNullOrEmpty()) {
            val timeSend = timeStamp.toLong()

            val yearFormat = SimpleDateFormat("yyyy", Locale.ENGLISH)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
            val dayAndMonForMat = SimpleDateFormat("dd-MM", Locale.ENGLISH)
            val timeFormat = SimpleDateFormat("HH MM", Locale.ENGLISH)

            if (yearFormat.format(timeSend) != LocalDate.now().year.toString()) {
                resultTime = yearFormat.format(timeSend)
                return resultTime
            }

            if (dateFormat.format(timeSend) != LocalDate.now().toString()) {
                resultTime = dayAndMonForMat.format(timeSend)
                return resultTime
            }

            resultTime = timeFormat.format(timeSend)
        }

        return resultTime
    }

    interface Callback{
        fun onBoxClick(boxId: String)
        fun loadBoxAvatar(img: ImageView, url: String)
    }
}