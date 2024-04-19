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
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.util.Calendar

class CountViewModel(private val itemDao: ItemDao) : ViewModel() {

    fun getFullSchedule(): Flow<List<Item>> {
        return itemDao.getItem()
    }

    var itemUiState by mutableStateOf(ItemDetails())
        private set

    var textCount by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val timeIn =
                calendar[Calendar.DAY_OF_MONTH].toString() + "." + (calendar[Calendar.MONTH] + 1) + "." + calendar[Calendar.YEAR]

            if (itemDao.getAllItem().isEmpty()){
             itemDao.insert(Item(0,"Мой Счет", 0, 1, 1, timeIn))
            }

            itemUiState = itemDao.getlastReadProject()
                .filterNotNull()
                .first()
                .toItemDetal()
        }
    }

    suspend fun getAllItem(){
         textCount = (itemDao.getAllItem().size+1).toString()
    }

    suspend fun last() {
        itemUiState = itemDao.getlastReadProject().filterNotNull().first().toItemDetal()
    }


    suspend fun insertTable(item: Item) {
        itemDao.insert(item)
    }

    //Обновляем таблицу
    suspend fun updateTable() {
        itemDao.update(itemUiState.toItem())
    }

    suspend fun updateColum() {
        itemDao.updateToCount()
    }

    fun updateItemUiState(item: Item) {
        itemUiState = item.toItemDetal()
    }

    fun updateItemUiStateSett(itemDetails: ItemDetails) {
        itemUiState = itemDetails
    }

    fun plus() {
        val calendar = Calendar.getInstance()
        val timeIn =
            calendar[Calendar.DAY_OF_MONTH].toString() + "." + (calendar[Calendar.MONTH] + 1) + "." + calendar[Calendar.YEAR]

        itemUiState = itemUiState.copy(count = (itemUiState.count.toInt() + itemUiState.step.toInt()).toString(), lastCount = 1, time = timeIn)
    }

    fun minus() {
        val calendar = Calendar.getInstance()
        val timeIn =
            calendar[Calendar.DAY_OF_MONTH].toString() + "." + (calendar[Calendar.MONTH] + 1) + "." + calendar[Calendar.YEAR]
        itemUiState = itemUiState.copy(count = (itemUiState.count.toInt() - itemUiState.step.toInt()).toString(), lastCount = 1, time = timeIn)
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
    val title: String = "",
    val count:String = "",
    val step: String = "",
    val lastCount: Int = 1,
    val time: String = ""
)

fun ItemDetails.toItem(): Item = Item(
    id = id,
    title = title,
    count = count.toInt(),
    step = step.toInt(),
    lastCount = lastCount,
    time = time
)

fun Item.toItemDetal(): ItemDetails = ItemDetails(
    id = id,
    title = title,
    count = count.toString(),
    step = step.toString(),
    lastCount = lastCount,
    time = time
)



