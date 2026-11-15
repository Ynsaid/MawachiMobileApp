package com.example.mawachiapp.fragments

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mawachiapp.classes.Item
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal

private fun itemSubtotal(item: Item): BigDecimal? {
    val priceText = item.price?.trim()
        ?: return null

    val quantityText = item.quantity?.trim()
        ?: return null

    val price = priceText.toBigDecimalOrNull()
        ?: return null

    val quantity = quantityText.toIntOrNull()
        ?: return null

    if (price < BigDecimal.ZERO || quantity <= 0) {
        return null
    }

    return price.multiply(
        BigDecimal.valueOf(quantity.toLong())
    )
}

@Composable
fun RealCartScreen() {
    val context = LocalContext.current

    val preferences = remember(context) {
        context.getSharedPreferences(
            "cart",
            Context.MODE_PRIVATE
        )
    }

    val gson = remember {
        Gson()
    }

    var cartItems by remember {
        mutableStateOf<List<Item>>(emptyList())
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(preferences) {
        fun readCart() {
            try {
                val json = preferences.getString(
                    "cart_items",
                    null
                )

                val type = object : TypeToken<List<Item>>() {}.type

                val loaded: List<Item> =
                    gson.fromJson<List<Item>>(json, type)
                        ?: emptyList()

                require(
                    loaded.all { item ->
                        !item.name.isNullOrBlank() &&
                                item.price != null &&
                                item.quantity != null &&
                                item.image != null
                    }
                )

                cartItems = loaded
                error = null
            } catch (_: Exception) {
                error = "تعذر قراءة السلة المحفوظة. لم نحذف بياناتها."
            }
        }

        readCart()

        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == "cart_items" || key == null) {
                    readCart()
                }
            }

        preferences.registerOnSharedPreferenceChangeListener(listener)

        onDispose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    fun removeItem(index: Int) {
        if (error != null || index !in cartItems.indices) {
            return
        }

        val updated = cartItems.toMutableList().apply {
            removeAt(index)
        }

        preferences.edit()
            .putString(
                "cart_items",
                gson.toJson(updated)
            )
            .apply()

        cartItems = updated
    }

    if (error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MarketEmptyState(
                icon = Icons.Outlined.Info,
                title = "تعذر تحميل السلة",
                message = error.orEmpty()
            )
        }

        return
    }

    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            MarketEmptyState(
                icon = Icons.Outlined.ShoppingCart,
                title = "سلتك فارغة",
                message = "المنتجات التي تضيفها من صفحة التفاصيل ستظهر هنا."
            )
        }

        return
    }

    val subtotals = cartItems.map(::itemSubtotal)

    val validTotal = subtotals.all { it != null }

    val total = subtotals
        .filterNotNull()
        .fold(BigDecimal.ZERO) { sum, value ->
            sum.add(value)
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "سلتي",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "${cartItems.size} عناصر في السلة",
            color = MarketMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(
                items = cartItems
            ) { index, item ->
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            MarketImage(
                                url = item.image,
                                description = item.name,
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                item.name?.let {
                                    Text(
                                        text = it,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                item.location?.let {
                                    Text(
                                        text = it,
                                        color = MarketMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = "الكمية: ${item.quantity}",
                                    color = MarketMuted,
                                    fontSize = 12.sp
                                )
                            }

                            IconButton(
                                onClick = { removeItem(index) }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DeleteOutline,
                                    contentDescription = "حذف من السلة",
                                    tint = MarketRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        HorizontalDivider(
                            color = MarketBackground
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الإجمالي الفرعي",
                                color = MarketMuted,
                                fontSize = 12.sp
                            )

                            val subtotal = itemSubtotal(item)

                            Text(
                                text = subtotal?.let(::formatMoney)
                                    ?: "تحقق من السعر والكمية",
                                color = if (subtotal != null) {
                                    MarketGreen
                                } else {
                                    MarketRed
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Surface(
            color = MarketGreen,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "المبلغ الإجمالي",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )

                Text(
                    text = if (validTotal) {
                        formatMoney(total)
                    } else {
                        "بعض الأسعار أو الكميات غير صالحة"
                    },
                    color = MarketGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (validTotal) 24.sp else 14.sp
                )
            }
        }
    }
}