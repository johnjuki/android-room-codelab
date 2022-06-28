package com.example.roomwordsample.repo

import com.example.roomwordsample.database.Word
import com.example.roomwordsample.database.WordDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WordRepository(private val wordDao: WordDao) {
    val allWords: Flow<List<Word>> = wordDao.getAlphabetizedWords()

    fun insert(word: Word) {
        CoroutineScope(Dispatchers.IO).launch {
            wordDao.insert(word)
        }
    }
}
