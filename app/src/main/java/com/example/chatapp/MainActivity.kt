package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import com.example.chatapp.ui.home.HomeFragment
import com.example.chatapp.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val loginSharedPreference: LoginSharedPreference = LoginSharedPreferenceImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
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

    private fun isLogin(): Boolean {
        val currentUser = loginSharedPreference.getCurrentUserId(this)
        return !(currentUser == null || currentUser == "")
    }
}