package com.vkaja.dagger.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vkaja.dagger.application.databinding.ActivityMainBinding
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        DaggerMainViewModelFactory.create().inject(this)
        binding.abcDef.text = viewModel.data()
        setContentView(binding.root)
    }
}