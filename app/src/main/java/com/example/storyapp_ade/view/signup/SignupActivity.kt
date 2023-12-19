package com.example.storyapp_ade.view.signup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.storyapp_ade.R
import com.example.storyapp_ade.advanced_ui.MyButton
import com.example.storyapp_ade.advanced_ui.MyEditText
import com.example.storyapp_ade.data.repository.ResultState
import com.example.storyapp_ade.databinding.ActivitySignupBinding
import com.example.storyapp_ade.view.ViewModelFactory
import com.example.storyapp_ade.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var myButton: MyButton
    private lateinit var emailEditText: MyEditText
    private lateinit var passEditText: MyEditText

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String

    private val viewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this, true)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        myButton = findViewById(R.id.signupButton)
        emailEditText = findViewById(R.id.emailEditText)
        passEditText = findViewById(R.id.passwordEditText)

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
        binding.signupButton.setOnClickListener {
            uploadSignUp()
        }
    }

    private fun uploadSignUp() {
        binding.apply {
            name = namaEditText.text.toString()
            email = emailEditText.text.toString()
            password = passEditText.text.toString()
        }

        viewModel.uploadData(name, email, password).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                        setMyButtonEnable(false)
                    }

                    is ResultState.Success -> {
                        showToast(result.data.message)

                        showLoading(false)
                        setMyButtonEnable(true)
                        val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                        startActivity(intent)
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