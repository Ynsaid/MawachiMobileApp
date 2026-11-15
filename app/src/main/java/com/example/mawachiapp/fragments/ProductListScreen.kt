package com.example.mawachiapp.fragments

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.mawachiapp.classes.Product
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.Locale

private fun formatProductPrice(price: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        maximumFractionDigits = 2
    }

    return "${formatter.format(price)} دج"
}

@Composable
fun ProductListScreen(
    category: String,
    onBack: () -> Unit,
    onOpenProduct: (Product) -> Unit
) {
    var products by remember {
        mutableStateOf<List<Product>>(emptyList())
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

    var sortOption by rememberSaveable {
        mutableStateOf("الأحدث")
    }

    var showSortMenu by remember {
        mutableStateOf(false)
    }

    DisposableEffect(category) {
        var active = true

        loading = true
        error = null
        products = emptyList()

        val collection = FirebaseFirestore.getInstance()
            .collection("products")

        val firestoreQuery = if (category.isBlank()) {
            collection
        } else {
            collection.whereEqualTo(
                "category",
                category
            )
        }

        firestoreQuery.get()
            .addOnSuccessListener { result ->
                if (active) {
                    products = result.documents.mapNotNull { document ->
                        runCatching {
                            document.toObject(Product::class.java)
                        }.getOrNull()
                    }

                    loading = false
                }
            }
            .addOnFailureListener {
                if (active) {
                    error =
                        "تعذر تحميل منتجات هذه الفئة. تحقق من اتصال الإنترنت."
                    loading = false
                }
            }

        onDispose {
            active = false
        }
    }

    val filteredProducts = remember(
        products,
        query,
        sortOption
    ) {
        val searched = products.filter { product ->
            val text = query.trim()

            text.isBlank() ||
                    product.name.contains(text, ignoreCase = true) ||
                    product.location.contains(text, ignoreCase = true) ||
                    product.age.contains(text, ignoreCase = true)
        }

        when (sortOption) {
            "الأقل سعرًا" -> searched.sortedBy { it.price }

            "الأعلى سعرًا" -> searched.sortedByDescending { it.price }

            "الأثقل وزنًا" -> searched.sortedByDescending { it.weight }

            else -> searched
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MarketBackground)
    ) {
        Surface(
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 12.dp,
                        bottom = 14.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "رجوع"
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (category.isBlank()) {
                                "جميع المنتجات"
                            } else {
                                category
                            },
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "${filteredProducts.size} منتج",
                            color = MarketMuted,
                            fontSize = 11.sp
                        )
                    }

                    Box {
                        IconButton(
                            onClick = {
                                showSortMenu = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FilterList,
                                contentDescription = "ترتيب النتائج"
                            )
                        }

                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = {
                                showSortMenu = false
                            }
                        ) {
                            listOf(
                                "الأحدث",
                                "الأقل سعرًا",
                                "الأعلى سعرًا",
                                "الأثقل وزنًا"
                            ).forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(text = option)
                                    },
                                    onClick = {
                                        sortOption = option
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(
                            text = "ابحث بالاسم أو الولاية أو العمر..."
                        )
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
                        focusedContainerColor = MarketBackground,
                        unfocusedContainerColor = MarketBackground,
                        unfocusedBorderColor = Color(0xFFE0E5DC)
                    )
                )
            }
        }

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = error.orEmpty(),
                        color = MarketRed,
                        textAlign = TextAlign.Center
                    )

                    TextButton(
                        onClick = onBack
                    ) {
                        Text(text = "رجوع")
                    }
                }
            }

            filteredProducts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    MarketEmptyState(
                        icon = Icons.Outlined.Search,
                        title = "لا توجد منتجات",
                        message = if (query.isBlank()) {
                            "لم تتم إضافة منتجات إلى هذه الفئة بعد."
                        } else {
                            "جرّب تغيير البحث أو الترتيب."
                        }
                    )
                }
            }

            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 24.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = filteredProducts
                    ) { product ->
                        ProductCard(
                            product = product,
                            onClick = {
                                onOpenProduct(product)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.1f)
                .background(MarketLightGreen)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                modifier = Modifier.matchParentSize()
            )

            Surface(
                color = Color.White.copy(alpha = 0.94f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp)
            ) {
                Text(
                    text = "${product.amount} رأس",
                    color = MarketText,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(
                        horizontal = 9.dp,
                        vertical = 4.dp
                    )
                )
            }
        }

        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MarketMuted,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(3.dp))

                Text(
                    text = product.location,
                    color = MarketMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "${product.weight} كغ • ${product.age}",
                color = MarketMuted,
                fontSize = 11.sp
            )

            Text(
                text = formatProductPrice(product.price),
                color = MarketGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }
    }
}