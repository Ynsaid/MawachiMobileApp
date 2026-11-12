package com.example.mawachiapp.screens

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.mawachiapp.R
import com.example.mawachiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.sheep_sound)
        mediaPlayer.start()
        Handler(mainLooper).postDelayed({

            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
                mediaPlayer.release()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        },5000)


    }

    override fun onDestroy() {
        super.onDestroy()
        if(::mediaPlayer.isInitialized && mediaPlayer.isPlaying){
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}