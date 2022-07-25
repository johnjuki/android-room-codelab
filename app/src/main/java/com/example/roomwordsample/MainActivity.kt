package com.example.roomwordsample

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomwordsample.NewWordNotification.createNotificationChannel
import com.example.roomwordsample.NewWordNotification.notify
import com.example.roomwordsample.database.Word
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val wordViewModel: WordViewModel by viewModels {
        WordViewModelFactory((application as WordsApplication).repository)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val getWord =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let {
                    val word = Word(it)
                    wordViewModel.insert(word)

                    notify(this, "New Word", it, 0)
                }
            } else {
                Toast.makeText(applicationContext, R.string.empty_not_saved, Toast.LENGTH_LONG)
                    .show()
            }
        }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel(this)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = WordListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        wordViewModel.allWords.observe(this) { words ->
            // Update the cached copy of the words in the adapter
            words?.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
            getWord.launch(intent)
        }
    }

}
