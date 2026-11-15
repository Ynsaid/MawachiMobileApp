package com.example.mawachiapp.screens

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.mawachiapp.R
import com.example.mawachiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.sheep_sound)
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Handler(mainLooper).postDelayed({
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
            }
            mediaPlayer = null
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, 5000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                mp.stop()
            }
            mp.release()
        }
        mediaPlayer = null
    }
}