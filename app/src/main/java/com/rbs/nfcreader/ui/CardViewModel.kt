package com.rbs.nfcreader.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rbs.nfcreader.data.CardRepository
import com.rbs.nfcreader.data.model.Card
import kotlinx.coroutines.launch

class CardViewModel(private val repository: CardRepository) : ViewModel() {

    fun getAllData(): LiveData<List<Card>> = repository.getAllData()

    fun insert(card: Card) {
        repository.insert(card)
    }

    fun delete(id: Int) {
        repository.delete(id)
    }

    fun send(serialNumber: String, message: String) {
        viewModelScope.launch {
            repository.send(serialNumber, message)
        }
    }
}