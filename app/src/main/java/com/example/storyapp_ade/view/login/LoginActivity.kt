package com.example.storyapp_ade.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import com.example.storyapp_ade.advanced_ui.MyButton
import com.example.storyapp_ade.advanced_ui.MyEditText
import com.example.storyapp_ade.data.repository.ResultState
import com.example.storyapp_ade.data.pref.UserModel
import com.example.storyapp_ade.databinding.ActivityLoginBinding
import com.example.storyapp_ade.view.ViewModelFactory
import com.example.storyapp_ade.view.main.MainActivity
import com.example.storyapp_ade.view.welcome.WelcomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var myButton: MyButton
    private lateinit var emailEditText: MyEditText
    private lateinit var passEditText: MyEditText

    private lateinit var email: String
    private lateinit var pass: String

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myButton = binding.loginButton

        emailEditText = binding.emailEditText
        passEditText = binding.passwordEditText

        setMyButtonEnable(false)
        setupAction()

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(true)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        passEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable(true)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@LoginActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        })
    }

    private fun setMyButtonEnable(isEnable: Boolean) {
        val result = passEditText.text
        if (isEnable) {
            if (result != null) {
                myButton.isEnabled = result.length >= 8 && MyEditText.isValid
            }
        } else myButton.isEnabled = false

    }


    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            uploadSignUp()
        }
    }

    private fun uploadSignUp() {
        binding.apply {
            email = emailEditText.text.toString()
            pass = passEditText.text.toString()
        }

        viewModel.login(email, pass).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                        setMyButtonEnable(false)
                    }

                    is ResultState.Success -> {
                        viewModel.saveSession(
                            UserModel(
                                result.data.loginResult.name, result.data.loginResult.token
                            )
                        )
                        showToast(result.data.message)

                        showLoading(false)
                        setMyButtonEnable(true)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    is ResultState.Error -> {
                        showToast(result.error)

                        showLoading(false)
                        setMyButtonEnable(true)
                    }
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}