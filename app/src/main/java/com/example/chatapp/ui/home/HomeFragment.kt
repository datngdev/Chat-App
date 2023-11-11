package com.example.chatapp.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.databinding.FragmentHomeBinding
import com.example.chatapp.datas.models.BoxChat
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
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

        var boxList = emptyList<BoxChat>().toMutableList()
        val layoutManager = LinearLayoutManager(context)

        val adapterCallBack = object: BoxChatAdapter.Callback {
            override fun onBoxClick(boxId: String) {
                callBack?.navigateToChatDetail(boxId)
            }

        }
        val boxChatAdapter = BoxChatAdapter(boxList, adapterCallBack)

        binding.recyclerviewBoxList.layoutManager = layoutManager
        binding.recyclerviewBoxList.adapter = boxChatAdapter

        binding.txtHomeMenu.setOnClickListener {
            homeViewModel.logout(currentUser)
            callBack?.navigateToLogin()
        }

        binding.txtBoxAdd.setOnClickListener {
            val boxId = "Math"

            lifecycleScope.launch {
                homeViewModel.processCreateBoxChat(currentUser, boxId).collect {
                    if (it == true) {
                        Toast.makeText(context, "Create Box Chat $boxId successfully", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            homeViewModel.processGetBoxChat(currentUser).collect {
                if (!it.isNullOrEmpty()) {
                    boxList.clear()
                    boxList.addAll(it)
                    Log.d("CA", boxList.toString())
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