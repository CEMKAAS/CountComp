package com.zaroslikov.count2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.count2.R
import com.zaroslikov.count2.data.Item

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCountApp(
    viewModel: CountViewModel = viewModel(factory = CountViewModel.factory)
) {
    //для БД
    val countAD by viewModel.getFullSchedule().collectAsState(emptyList())
    val scope = rememberCoroutineScope()

    //запоминает состояние для BottomSheet
    val sheetState = rememberModalBottomSheetState()
    val showBottomSheet = remember { mutableStateOf(false) }

    //bottomSheet для найстроки
    val sheetStateSetting = rememberModalBottomSheetState()
    val showBottomSheetSetting = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        viewModel.itemUiState.title, maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                },
                navigationIcon = {
                    IconButton(onClick = { showBottomSheet.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showBottomSheetSetting.value = true }) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Localized description"
                        )
                    }
                },
            )
        }
    ) { contentPadding ->

        ImageBody(
            itemUiState = viewModel.itemUiState,
            plus = {
                scope.launch {
                    viewModel.updateColum()
                }
                viewModel.plus()
                scope.launch {
                    viewModel.updateTable()
                }
            },
            minus = {
                scope.launch {
                    viewModel.updateColum()
                }
                viewModel.minus()
                scope.launch {
                    viewModel.updateTable()
                }
            },
            showBottomSheet = showBottomSheet,
            buttonSheet = {
                scope.launch {
                    sheetState.hide()
                }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet.value = false
                    }
                }
            },
            sheetState = sheetState,
            showBottomSheetSetting = showBottomSheetSetting,
            buttonSheetSetting = {
                scope.launch {
                    sheetStateSetting.hide()
                }.invokeOnCompletion {
                    if (!sheetStateSetting.isVisible) {
                        showBottomSheetSetting.value = false
                    }
                }
            },
            sheetStateSetting = sheetStateSetting,
            countList = countAD,
            scope = scope,
            viewModel = viewModel,
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageBody(
    itemUiState: ItemDetails,
    plus: () -> Unit,
    minus: () -> Unit,
    showBottomSheet: MutableState<Boolean>,
    buttonSheet: () -> Unit,
    sheetState: SheetState,
    showBottomSheetSetting: MutableState<Boolean>,
    buttonSheetSetting: () -> Unit,
    sheetStateSetting: SheetState,
    countList: List<Item>,
    scope: CoroutineScope,
    viewModel: CountViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = itemUiState.count, fontSize = 50.sp
        )
        Row() {
            Image(
                painter = painterResource(id = R.drawable.minuse),
                contentDescription = null,
                modifier = Modifier
                    .height(177.dp)
                    .width(170.dp)
                    .clickable(onClick = minus),
                contentScale = ContentScale.Fit
            )
            Image(
                painter = painterResource(id = R.drawable.plus), contentDescription = null,
                modifier = Modifier
                    .height(177.dp)
                    .width(170.dp)
                    .clickable(onClick = plus),
                contentScale = ContentScale.Fit
            )
        }
    }

    if (showBottomSheet.value) {
        BottomSheet(showBottomSheet, buttonSheet, sheetState, countList, scope, viewModel)
    }

    if (showBottomSheetSetting.value) {
        BottomSheetSetting(
            itemUiState = viewModel.itemUiState,
            onItemValueChange = viewModel::updateItemUiStateSett,
            showBottomSheetSetting = showBottomSheetSetting,
            sheetState = sheetStateSetting,
            updateSett = {
                scope.launch {
                    viewModel.updateTable()
                }
                showBottomSheetSetting.value = false
            }
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSetting(
    itemUiState: ItemDetails,
    onItemValueChange: (ItemDetails) -> Unit,
    showBottomSheetSetting: MutableState<Boolean>,
    sheetState: SheetState,
    updateSett: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = { showBottomSheetSetting.value = false },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(15.dp)) {

            OutlinedTextField(
                value = itemUiState.title,
                onValueChange = {onItemValueChange(itemUiState.copy(title = it))},
                label = { Text("Мой счет") }

            )

            OutlinedTextField(
                value = itemUiState.step,
                onValueChange = {onItemValueChange(itemUiState.copy(step = it))},
                label = { Text("Шаг") }

            )

            Text(text = "Мой счет v1.2", fontSize = 25.sp)
            Text(text = "Дорогой друг\nНезабудь вступить в нашу группу ВК!", fontSize = 25.sp)
            Text(text = "https://vk.com/bagesmakestudios", fontSize = 25.sp)
            Button(onClick = updateSett){}
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    showBottomSheet: MutableState<Boolean>,
    scopeFun: () -> Unit,
    sheetState: SheetState,
    countList: List<Item>,
    scope: CoroutineScope,
    viewModel: CountViewModel
) {
    val openAlertDialog = remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = { showBottomSheet.value = false },
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.padding(15.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                IconButton(onClick = scopeFun) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = {openAlertDialog.value = true}) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Localized description"
                    )
                }
            }
            Text(text = "Значение", fontSize = 25.sp)
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(5.dp))
            CountDetails(
                countList = countList,
                viewModel = viewModel,
                showBottomSheet = showBottomSheet
            )
        }
    }
    if (openAlertDialog.value) {
        AlterDialog(
            openAlertDialog = openAlertDialog,
            scope = scope,
            viewModel = viewModel,
            showBottomSheet = showBottomSheet
        )
    }
}

@Composable
fun AlterDialog(
    openAlertDialog: MutableState<Boolean>,
    scope: CoroutineScope,
    viewModel: CountViewModel,
    showBottomSheet: MutableState<Boolean>
) {
    var text by rememberSaveable { mutableStateOf("") }
    Dialog(onDismissRequest = { openAlertDialog.value = false }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                Text(text = "Добавить новый счетчик")
                Text(text = "Укажите название")
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Label") }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { openAlertDialog.value = false },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Отмена")
                    }
                    TextButton(
                        onClick = {
                            scope.launch {
                                viewModel.updateColum()
                                viewModel.insertTable(
                                    Item(
                                        id = 0,
                                        title = text,
                                        count = 0,
                                        step = 1,
                                        lastCount = 1,
                                        time = Calendar.getInstance().time.toString()
                                    )
                                )
                                viewModel.last()
                            }
                            openAlertDialog.value = false
                            showBottomSheet.value = false
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Создать")
                    }
                }
            }
        }
    }
}

@Composable
fun CountDetails(
    countList: List<Item>, viewModel: CountViewModel, showBottomSheet: MutableState<Boolean>
) {
    LazyColumn(
        modifier = Modifier
    ) {
        items(countList) { item ->
            CountCard(viewModel, item, showBottomSheet)
        }
    }
}

@Composable
fun CountCard(
    viewModel: CountViewModel,
    item: Item,
    showBottomSheet: MutableState<Boolean>
) {
    Card(modifier = Modifier.clickable {
        viewModel.updateItemUiState(item)
        showBottomSheet.value = false;

    }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp, start = 5.dp)
        ) {
            Column {
                Text(text = item.title)
                Text(text = "Последние изм.: ${item.time}")
            }
            Text(text = "${item.count}")
        }
        Divider()
    }
}


//@Preview(showBackground = true)
//@Composable
//fun CountCardPrewie() {
//    CountCard(1, 65, "Мой счет", "16.04.2024")
//}




