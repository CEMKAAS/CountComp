package com.zaroslikov.count2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zaroslikov.count2.R
import com.zaroslikov.count2.data.Item

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "sds", maxLines = 1,
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
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
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
                viewModel.plus()
                scope.launch {
                    viewModel.insertTable()
                }
            },
            minus = {
                viewModel.minus()
                scope.launch {
                    viewModel.insertTable()
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
            countList = countAD,
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
    countList: List<Item>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = itemUiState.count.toString(), fontSize = 50.sp
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
        BottomSheet(showBottomSheet, buttonSheet, sheetState, countList)
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    showBottomSheet: MutableState<Boolean>,
    scope: () -> Unit,
    sheetState: SheetState,
    countList: List<Item>
) {
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
                IconButton(onClick = scope) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Localized description"
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Localized description"
                    )
                }
            }
            Text(text = "Значение", fontSize = 25.sp)
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(5.dp))
            CountDetails(countList = countList)
        }
    }
}

@Composable
fun CountDetails(
    countList: List<Item>
) {
    LazyColumn(
        modifier = Modifier
    ) {
        items(countList) { item ->
            CountCard(item.id, item.count)
        }
    }

}

@Composable
fun CountCard(
    id: Int,
    count: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 10.dp, start = 5.dp)
    ) {
        Column {
            Text(text = "Мой счет $id")
            Text(text = "Последние изм.: 14.10.2000")
        }
        Text(text = "$count")

    }
    Divider()
}


@Preview(showBackground = true)
@Composable
fun CountCardPrewie() {
    CountCard(1, 65)
}




