package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import com.example.chatapp.ui.chatDetail.ChatDetailFragment
import com.example.chatapp.ui.home.HomeFragment
import com.example.chatapp.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    lateinit var loginSharedPreference: LoginSharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        loginSharedPreference = LoginSharedPreferenceImpl(this)
        setContentView(binding.root)

        if(isLogin()) {
            goToHomeScreen()
        } else {
            goToLoginScreen()
        }
    }

    private fun goToHomeScreen() {
        val homeFragment = HomeFragment.newInstance(object: HomeFragment.HomeFragmentCallBack {
            override fun navigateToLogin() {
                goToLoginScreen()
            }

            override fun navigateToChatDetail(boxId: String) {
                goToChatDetail(boxId)
            }
        })

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_frame_layout, homeFragment).commit()
    }
    private fun goToLoginScreen() {
        val loginFragment = LoginFragment.newInstance(object: LoginFragment.LoginFragmentCallBack {
            override fun navigateToHome() {
                goToHomeScreen()
            }
        })

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_frame_layout, loginFragment).commit()
    }
    private fun goToChatDetail(boxId: String) {
        val chatDetailFragment = ChatDetailFragment.newInstance(object: ChatDetailFragment.ChatDetailCallBack {
            override fun navigateToHome() {
                goToHomeScreen()
            }
        }, boxId)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_frame_layout, chatDetailFragment).commit()
    }

    private fun isLogin(): Boolean {
        val currentUser = loginSharedPreference.getCurrentUserId()
        return !(currentUser == null || currentUser == "")
    }
}