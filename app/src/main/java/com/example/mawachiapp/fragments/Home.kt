package com.example.mawachiapp.fragments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mawachiapp.classes.Animal
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun RealHomeScreen(
    onOpenCategory: (String) -> Unit
) {
    var animals by remember {
        mutableStateOf<List<Animal>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var query by rememberSaveable {
        mutableStateOf("")
    }

    var reloadKey by remember {
        mutableStateOf(0)
    }

    DisposableEffect(reloadKey) {
        var active = true

        loading = true
        error = null

        FirebaseFirestore.getInstance()
            .collection("animals")
            .get()
            .addOnSuccessListener { result ->
                if (active) {
                    animals = result.documents.mapNotNull { document ->
                        runCatching {
                            document.toObject(Animal::class.java)
                        }.getOrNull()
                    }

                    loading = false
                }
            }
            .addOnFailureListener {
                if (active) {
                    error = "تعذر تحميل البيانات. تحقق من اتصال الإنترنت."
                    loading = false
                }
            }

        onDispose {
            active = false
        }
    }

    val filtered = animals.filter { animal ->
        query.isBlank() ||
                animal.name.orEmpty().contains(
                    query.trim(),
                    ignoreCase = true
                )
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 20.dp,
            bottom = 24.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "أهلًا بك في مواشي",
                        color = MarketMuted,
                        fontSize = 12.sp
                    )

                    Text(
                        text = "سوقك أقرب إليك",
                        fontFamily = MarketFont,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = MarketGreen
                    )

                    Text(
                        text = "اكتشف المواشي وتصفح العروض",
                        color = MarketMuted,
                        fontSize = 11.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MarketGreen
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Storefront,
                        contentDescription = null,
                        tint = MarketGold,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(27.dp)
                    )
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                placeholder = {
                    Text(text = "ابحث عن نوع الماشية...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "مسح البحث"
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color(0xFFDEE4D8)
                )
            )
        }

        if (!loading && error == null && animals.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AnimalCarousel(
                    animals = animals.take(5),
                    onOpenCategory = onOpenCategory
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Surface(
                color = MarketLightGreen,
                shape = RoundedCornerShape(18.dp)
            ) {
                Row(
                    modifier = Modifier.padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MarketGreen
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "تسوّق باطمئنان",
                            color = MarketGreen,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "عاين الماشية وتحقق من معلومات البائع قبل الدفع.",
                            color = MarketMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "تصفح الفئات",
                    style = MaterialTheme.typography.titleMedium
                )

                if (!loading && error == null) {
                    Text(
                        text = "${filtered.size} فئات",
                        color = MarketMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        when {
            loading -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            error != null -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = error.orEmpty(),
                            color = MarketRed,
                            textAlign = TextAlign.Center
                        )

                        TextButton(
                            onClick = { reloadKey++ }
                        ) {
                            Text(text = "إعادة المحاولة")
                        }
                    }
                }
            }

            filtered.isEmpty() -> {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    MarketEmptyState(
                        icon = Icons.Outlined.Search,
                        title = "لا توجد نتائج",
                        message = if (query.isBlank()) {
                            "لم تتم إضافة فئات إلى السوق بعد."
                        } else {
                            "جرّب كتابة اسم آخر."
                        }
                    )
                }
            }

            else -> {
                items(
                    items = filtered
                ) { animal ->
                    AnimalCategoryCard(
                        animal = animal,
                        onOpenCategory = onOpenCategory
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimalCarousel(
    animals: List<Animal>,
    onOpenCategory: (String) -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { animals.size }
    )

    val interaction = remember {
        MutableInteractionSource()
    }

    val dragged by pagerState.interactionSource
        .collectIsDraggedAsState()

    val pressed by interaction.collectIsPressedAsState()

    if (!dragged && !pressed && animals.size > 1) {
        LaunchedEffect(
            key1 = pagerState,
            key2 = animals.size
        ) {
            while (true) {
                delay(5000)

                pagerState.animateScrollToPage(
                    (pagerState.currentPage + 1) % animals.size
                )
            }
        }
    }

    Column {
        HorizontalPager(
            state = pagerState,
            pageSpacing = 12.dp
        ) { page ->
            val animal = animals[page]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(215.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .clickable(
                        interactionSource = interaction,
                        indication = null,
                        onClick = {
                            onOpenCategory(
                                animal.category
                                    ?: animal.name
                                    ?: ""
                            )
                        }
                    )
            ) {
                MarketImage(
                    url = animal.image,
                    description = animal.name,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF132719).copy(alpha = 0.93f)
                                )
                            )
                        )
                )

                Surface(
                    color = MarketGold,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "اكتشف السوق",
                        color = MarketText,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 5.dp
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    Text(
                        text = animal.name ?: "المواشي",
                        fontFamily = MarketFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 27.sp,
                        color = Color.White
                    )

                    Text(
                        text = "تصفح العروض المتاحة",
                        color = MarketGold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(animals.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(
                            if (pagerState.currentPage == index) {
                                22.dp
                            } else {
                                6.dp
                            }
                        )
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) {
                                MarketGreen
                            } else {
                                MarketGreen.copy(alpha = 0.18f)
                            }
                        )
                )
            }
        }
    }
}

@Composable
private fun AnimalCategoryCard(
    animal: Animal,
    onOpenCategory: (String) -> Unit
) {
    Card(
        onClick = {
            onOpenCategory(
                animal.category
                    ?: animal.name
                    ?: ""
            )
        },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        MarketImage(
            url = animal.image,
            description = animal.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
        )

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = animal.name ?: "بدون اسم",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "استعرض العروض",
                fontSize = 11.sp,
                color = MarketMuted
            )
        }
    }
}