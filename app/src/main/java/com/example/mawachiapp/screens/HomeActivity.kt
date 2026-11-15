package com.example.mawachiapp.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.mawachiapp.classes.Product
import com.example.mawachiapp.fragments.MarketBackground
import com.example.mawachiapp.fragments.MarketFont
import com.example.mawachiapp.fragments.MarketGreen
import com.example.mawachiapp.fragments.MarketLightGreen
import com.example.mawachiapp.fragments.MarketMuted
import com.example.mawachiapp.fragments.MarketText
import com.example.mawachiapp.fragments.ProductListScreen
import com.example.mawachiapp.fragments.RealCartScreen
import com.example.mawachiapp.fragments.RealHomeScreen
import com.example.mawachiapp.fragments.RealSettingsScreen
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val baseTypography = Typography()

            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = MarketGreen,
                    onPrimary = Color.White,
                    background = MarketBackground,
                    surface = Color.White,
                    onSurface = MarketText,
                    surfaceVariant = MarketLightGreen,
                    onSurfaceVariant = MarketMuted,
                    error = Color(0xFFB54E46)
                ),
                typography = Typography(
                    titleLarge = baseTypography.titleLarge.copy(
                        fontFamily = MarketFont,
                        fontWeight = FontWeight.Bold
                    ),
                    titleMedium = baseTypography.titleMedium.copy(
                        fontFamily = MarketFont,
                        fontWeight = FontWeight.Bold
                    ),
                    bodyLarge = baseTypography.bodyLarge.copy(
                        fontFamily = MarketFont
                    ),
                    bodyMedium = baseTypography.bodyMedium.copy(
                        fontFamily = MarketFont
                    ),
                    bodySmall = baseTypography.bodySmall.copy(
                        fontFamily = MarketFont
                    ),
                    labelLarge = baseTypography.labelLarge.copy(
                        fontFamily = MarketFont
                    ),
                    labelMedium = baseTypography.labelMedium.copy(
                        fontFamily = MarketFont
                    ),
                    labelSmall = baseTypography.labelSmall.copy(
                        fontFamily = MarketFont
                    )
                )
            ) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Rtl
                ) {
                    MarketApp(
                        onOpenCategory = { category ->
                            // سيتم تمرير الفئة إلى ProductListScreen.
                        },
                        onSessionEnded = {
                            FirebaseAuth.getInstance().signOut()

                            startActivity(
                                Intent(
                                    this,
                                    LoginActivity::class.java
                                ).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )

                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketApp(
    onOpenCategory: (String) -> Unit,
    onSessionEnded: () -> Unit
) {
    val context = LocalContext.current

    var selectedTab by rememberSaveable {
        mutableStateOf(0)
    }

    var selectedCategory by rememberSaveable {
        mutableStateOf("")
    }

    var showProducts by rememberSaveable {
        mutableStateOf(false)
    }

    val tabs = listOf(
        "الرئيسية" to Icons.Outlined.Home,
        "السلة" to Icons.Outlined.ShoppingCart,
        "الإعدادات" to Icons.Outlined.Settings
    )

    Scaffold(
        containerColor = MarketBackground,
        contentWindowInsets = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            showProducts = false
                        },
                        icon = {
                            Icon(
                                imageVector = tab.second,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(text = tab.first)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MarketGreen,
                            selectedTextColor = MarketGreen,
                            indicatorColor = MarketLightGreen,
                            unselectedIconColor = MarketMuted,
                            unselectedTextColor = MarketMuted
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                selectedTab == 0 && showProducts -> {
                    ProductListScreen(
                        category = selectedCategory,
                        onBack = {
                            showProducts = false
                        },
                        onOpenProduct = { product ->
                            val intent = Intent(
                                context,
                                ProductDetailsActivity::class.java
                            )

                            intent.putExtra(
                                "product",
                                product
                            )

                            context.startActivity(intent)
                        }
                    )
                }

                selectedTab == 0 -> {
                    RealHomeScreen(
                        onOpenCategory = { category ->
                            selectedCategory = category
                            showProducts = true
                        }
                    )
                }

                selectedTab == 1 -> {
                    RealCartScreen()
                }

                else -> {
                    RealSettingsScreen(
                        onSessionEnded = onSessionEnded
                    )
                }
            }
        }
    }
}