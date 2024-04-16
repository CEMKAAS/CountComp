package com.zaroslikov.count2.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.zaroslikov.count2.InventoryApplication
import com.zaroslikov.count2.data.Item
import com.zaroslikov.count2.data.ItemDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

class CountViewModel(private val itemDao: ItemDao) : ViewModel() {

    fun getFullSchedule(): Flow<List<Item>> {
        return itemDao.getItem()
    }

    var itemUiState by mutableStateOf(ItemDetails())
        private set

    init {
        viewModelScope.launch {
            itemUiState = itemDao.getItemS(5)
                .filterNotNull()
                .first()
                .toItemDetal()

        }
    }

    suspend fun insertTable() {
        itemDao.insert(itemUiState.toItem())
    }

    fun plus() {
        itemUiState = ItemDetails(
            id = 5,
            count = itemUiState.count + itemUiState.step,
            step = itemUiState.step
        )
    }
    fun minus() {
        itemUiState = ItemDetails(
            id = 5,
            count = itemUiState.count - itemUiState.step,
            step = itemUiState.step
        )
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as InventoryApplication)
                CountViewModel(application.database.itemDao())
            }
        }
    }

}

data class ItemDetails(
    val id: Int = 1,
    val title:String = "",
    val count: Int = 0,
    val step: Int = 1,
    val lastCount:String = "",
    val time:String = ""
)

fun ItemDetails.toItem(): Item = Item(
    id = id,
    title = title,
    count = count,
    step = step,
    lastCount = lastCount,
    time = time
)

fun Item.toItemDetal(): ItemDetails = ItemDetails(
    id = id,
    title = title,
    count = count,
    step = step,
    lastCount = lastCount,
    time = time
)



