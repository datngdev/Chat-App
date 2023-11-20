package com.example.chatapp.ui.editBox

import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.Resource
import com.example.chatapp.R
import com.example.chatapp.datas.models.User

class UserListAdapter(
    private val currentUserId: String,
    private val userList: List<User>,
    private val adminIdList: List<String>,
    private val callBack: UserListAdapterCallBack
): RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val avatar = view.findViewById<ImageView>(R.id.item_user_image)
        val name = view.findViewById<TextView>(R.id.item_user_name)
        val btnRemove = view.findViewById<Button>(R.id.item_user_btn_remove)
        val showBtnLayout = view.findViewById<LinearLayout>(R.id.item_user_show_btn_layout)
        val btnSetAdmin = view.findViewById<Button>(R.id.item_user_btn_set_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = userList[position]

        callBack.loadUserAvatar(holder.avatar, item.avatar)
        holder.name.text = item.name

        if (adminIdList.contains(item.id)) {
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red_admin_box))
        } else {
            holder.name.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.item_text_color))
        }

        if (adminIdList.contains(currentUserId)) {
            holder.itemView.setOnClickListener {
                holder.showBtnLayout.visibility = if (holder.showBtnLayout.visibility == View.GONE) {
                    View.VISIBLE
                } else
                    View.GONE
            }
            holder.btnRemove.setOnClickListener {
                callBack.onClickBtnRemove(item.id)
                holder.showBtnLayout.visibility = View.GONE
            }
            holder.btnSetAdmin.setOnClickListener {
                callBack.onclickBtnSetAdmin(item.id)
                holder.showBtnLayout.visibility = View.GONE
            }
        }

    }

    interface UserListAdapterCallBack {
        fun loadUserAvatar(avatarImv: ImageView, avatarUrl: String)
        fun onClickBtnRemove(userId: String)
        fun onclickBtnSetAdmin(userId: String)
    }
}