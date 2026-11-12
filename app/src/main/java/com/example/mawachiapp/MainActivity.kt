package com.example.mawachiapp

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mawachiapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, R.raw.sheep_sound)
        mediaPlayer.start()
        Handler(getMainLooper()).postDelayed({

            if(mediaPlayer.isPlaying){
                mediaPlayer.stop()
                mediaPlayer.release()
            }
            startActivity(Intent(this,LoginActivity::class.java))
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