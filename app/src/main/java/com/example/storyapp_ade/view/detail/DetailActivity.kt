package com.example.storyapp_ade.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.storyapp_ade.api.response.Story
import com.example.storyapp_ade.data.repository.ResultState
import com.example.storyapp_ade.databinding.ActivityDetailBinding
import com.example.storyapp_ade.view.ViewModelFactory


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this, true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        detailUser()

    }

    private fun detailUser() {
        intent.getStringExtra(SEND_ID)?.let {
            viewModel.detail(it).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is ResultState.Loading -> {
                            showLoading(true)
                        }

                        is ResultState.Success -> {
                            showLoading(false)
                            setDetailData(result.data.story)
                        }

                        is ResultState.Error -> {
                            showToast(result.error)
                            showLoading(false)

                        }
                    }
                }

            }
        }
    }

    private fun setDetailData(theResponse: Story) {
        Glide.with(binding.root.context)
            .load(theResponse.photoUrl)
            .into(binding.hasilgambardetail)
        binding.nama.text = theResponse.name
        binding.deskripsi.text = theResponse.description
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            tvMohon.visibility = if (isLoading) View.VISIBLE else View.GONE
            pbHero.visibility = if (isLoading) View.VISIBLE else View.GONE
            Judul.visibility = if (isLoading) View.GONE else View.VISIBLE
            rlJudul.visibility = if (isLoading) View.GONE else View.VISIBLE
            llJudul.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val SEND_ID = "id"
    }
}