package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreference
import com.example.chatapp.datas.sharedpreferences.LoginSharedPreferenceImpl
import com.example.chatapp.ui.chatDetail.ChatDetailFragment
import com.example.chatapp.ui.editBox.EditBoxChatFragment
import com.example.chatapp.ui.editProfile.EditProfileFragment
import com.example.chatapp.ui.home.HomeFragment
import com.example.chatapp.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var loginSharedPreference: LoginSharedPreference

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

            override fun navigateToEditProfile() {
                goToEditProfile()
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

            override fun navigateToEditBoxChat(boxId: String) {
                goToEditBoxChat(boxId)
            }
        }, boxId)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_frame_layout, chatDetailFragment).commit()
    }

    private fun goToEditProfile() {
        val editProfileFragment = EditProfileFragment.newInstance(object : EditProfileFragment.EditProfileCallBack {
            override fun navigateToHome() {
                goToHomeScreen()
            }

            override fun navigateToLogin() {
                goToLoginScreen()
            }
        })

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_frame_layout, editProfileFragment).commit()
    }

    private fun goToEditBoxChat(boxId: String) {
        val editBoxChatFragment = EditBoxChatFragment.newInstance(boxId, object: EditBoxChatFragment.EditBoxChatCallBack {
            override fun navigateToBoxDetail() {
                goToChatDetail(boxId)
            }

            override fun navigateToHome() {
                goToHomeScreen()
            }
        })

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.main_frame_layout, editBoxChatFragment).commit()
    }

    private fun isLogin(): Boolean {
        val currentUser = loginSharedPreference.getCurrentUserId()
        return !(currentUser == null || currentUser == "")
    }
}