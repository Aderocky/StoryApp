package com.example.storyapp_ade.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp_ade.R
import com.example.storyapp_ade.adapter.LoadingStateAdapter
import com.example.storyapp_ade.adapter.StoryAdapter
import com.example.storyapp_ade.databinding.ActivityMainBinding
import com.example.storyapp_ade.view.maps.MapsActivity
import com.example.storyapp_ade.view.ViewModelFactory
import com.example.storyapp_ade.view.upload.UploadActivity
import com.example.storyapp_ade.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvHero.layoutManager = LinearLayoutManager(this)

        setReviewData()
        setupAction()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.story()
    }


    private fun setReviewData() {
        val adapter = StoryAdapter()
        viewModel.story().observe(this) {
            adapter.submitData(lifecycle, it)
        }
        binding.rvHero.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
    }

    private fun setupAction() {
        binding.fabTambah.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.btn_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }

            R.id.btn_logout -> {
                viewModel.logout()
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
                true
            }

            R.id.btn_maps -> {
                val intent = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}