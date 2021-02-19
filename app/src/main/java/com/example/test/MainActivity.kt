    package com.example.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.test.data.pref.SharedPref
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi


    @ExperimentalSerializationApi
    class MainActivity : AppCompatActivity() {

        private val sharedPreferences by lazy {
            SharedPref(this)
        }

        private val githubUtils: GitHubUtils by lazy {
            GitHubUtils()
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            findViewById<Button>(R.id.Login).setOnClickListener {
                startGitHubLogin()
            }
        }

        private fun startGitHubLogin() {
            val authIntent = Intent(Intent.ACTION_VIEW, githubUtils.buildAuthGitHubUrl())
            startActivity(authIntent)
        }

        override fun onResume() {
            super.onResume()

            val code = githubUtils.getCodeFromUri(uri = intent.data)
            code ?: return

            GlobalScope.launch {
                val response = githubUtils.getAccesToken(code)
                val token = "${response.tokenType} ${response.accessToken}"
                sharedPreferences.token = token
                val user = githubUtils.getUser(token)

                Log.d("TAG_11", "user $user")
            }
        }
    }


