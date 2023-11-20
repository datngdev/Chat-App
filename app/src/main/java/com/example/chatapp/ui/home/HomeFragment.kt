package com.example.chatapp.ui.home

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    companion object {
        fun newInstance(callBack: HomeFragmentCallBack): HomeFragment {
            return HomeFragment().apply {
                this.callBack = callBack
            }
        }
    }

    interface HomeFragmentCallBack {
        fun navigateToLogin()
        fun navigateToChatDetail(boxId: String)
        fun navigateToEditProfile()
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var callBack: HomeFragmentCallBack? = null
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var loginSharedPreference: LoginSharedPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loginSharedPreference = LoginSharedPreferenceImpl(requireContext())
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentUser = loginSharedPreference.getCurrentUserId()!!

        var boxList = mutableListOf<BoxChat>()
        var unseenList = mutableListOf<Map<String, String>>()
        val layoutManager = LinearLayoutManager(context)

        val adapterCallBack = object: BoxChatAdapter.Callback {
            override fun onBoxClick(boxId: String) {
                callBack?.navigateToChatDetail(boxId)
            }

            override fun loadBoxAvatar(img: ImageView, url: String) {
                Glide.with(requireContext())
                    .load(url)
                    .centerCrop()
                    .into(img)
            }
        }
        val boxChatAdapter = BoxChatAdapter(boxList, unseenList, adapterCallBack)

        binding.recyclerviewBoxList.layoutManager = layoutManager
        binding.recyclerviewBoxList.adapter = boxChatAdapter

        binding.txtHomeMenu.setOnClickListener {
            callBack!!.navigateToEditProfile()
        }

        binding.btnSearch.setOnClickListener {

        }

        binding.txtBoxAdd.setOnClickListener {
            //create dialog
            val builder = AlertDialog.Builder(requireContext())
            val createBoxView = layoutInflater.inflate(R.layout.dialog_create_box_chat, null)

            builder.setTitle("Input Box Chat name")
            builder.setView(createBoxView)
            builder.setPositiveButton("Create") { _: DialogInterface?, _: Int ->
                val boxName = createBoxView.findViewById<EditText>(R.id.alert_dialog_data).text.toString()
                if (!boxName.isNullOrEmpty()) {
                    lifecycleScope.launch {
                        homeViewModel.processCreateBoxChat(currentUser, boxName).collect {
                            if (it == true) {
                                Toast.makeText(context, "Create Box Chat $boxName successfully", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }

            val dialog = builder.create()
            dialog.show()
        }

        lifecycleScope.launch {
            homeViewModel.getUnseenCountList(currentUser).collect {newUnseenList ->
                if (!newUnseenList.isNullOrEmpty()) {
                    unseenList.clear()
                    unseenList.addAll(newUnseenList)

                    boxChatAdapter.notifyDataSetChanged()
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.processGetBoxChat(currentUser).collect {
                if (!it.isNullOrEmpty()) {
                    boxList.clear()
                    boxList.addAll(it)

                    boxChatAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}