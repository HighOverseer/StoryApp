package com.lajar.mystoryapp

import android.content.Context
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.lajar.mystoryapp.Helper.Helper
import com.lajar.mystoryapp.data.local.entity.Story
import com.lajar.mystoryapp.ViewModel.DetailViewModel
import com.lajar.mystoryapp.ViewModel.ViewModelFactory
import com.lajar.mystoryapp.databinding.ActivityDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DetailActivity : AppCompatActivity() {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_info")
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var binding: ActivityDetailBinding
    private lateinit var geocoder: Geocoder

    companion object {
        const val EXTRA_STORY = "story"
        const val SHARED_ELEMENT_1 = "scroll"
        const val SHARED_ELEMENT_2 = "image"
        const val SHARED_ELEMENT_3 = "name"
        const val SHARED_ELEMENT_4 = "description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

        getStoryDetail()

        detailViewModel.storyDetail.observe(this) { story ->
            setLayout(story)
        }
    }

    private fun init() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        detailViewModel = obtainViewModel(this, dataStore)
        geocoder = Geocoder(this, Locale.getDefault())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getStoryDetail() {
        val story = intent.getParcelableExtra<Story>(EXTRA_STORY)
        if (story is Story) {
            detailViewModel.setStoryDetail(story)
        }
    }

    private fun setLayout(story: Story) = lifecycleScope.launch(Dispatchers.Main) {
        binding.apply {
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
            tvDetailName.text = story.name
            tvDetailLocation.text = Helper.convertToAddressLine(
                story.lat,
                story.lon,
                geocoder,
                getString(R.string.no_location_found)
            )
            tvDetailDescription.text = story.description
        }

    }

    private fun obtainViewModel(
        activity: AppCompatActivity,
        dataStore: DataStore<Preferences>
    ): DetailViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, dataStore)
        return ViewModelProvider(activity, factory)[DetailViewModel::class.java]

    }


}