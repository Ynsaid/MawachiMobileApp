package com.example.mawachiapp.fragments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RealSettingsScreen(
    onSessionEnded: () -> Unit
) {
    val auth = remember {
        FirebaseAuth.getInstance()
    }

    val user = auth.currentUser

    var name by remember {
        mutableStateOf("مستخدم مواشي")
    }

    var loadingName by remember {
        mutableStateOf(user != null)
    }

    var profileError by remember {
        mutableStateOf<String?>(null)
    }

    var showLogout by remember {
        mutableStateOf(false)
    }

    var showDelete by remember {
        mutableStateOf(false)
    }

    var password by remember {
        mutableStateOf("")
    }

    var busy by remember {
        mutableStateOf(false)
    }

    var deleteError by remember {
        mutableStateOf<String?>(null)
    }

    DisposableEffect(user?.uid) {
        var active = true

        val uid = user?.uid

        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (active) {
                        name = document.getString("name")
                            ?.takeIf { it.isNotBlank() }
                            ?: "مستخدم مواشي"

                        loadingName = false
                    }
                }
                .addOnFailureListener {
                    if (active) {
                        loadingName = false
                        profileError = "تعذر تحميل الاسم."
                    }
                }
        }

        onDispose {
            active = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = "الإعدادات",
            style = MaterialTheme.typography.titleLarge
        )

        Surface(
            color = Color.White,
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = MarketLightGreen,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MarketGreen,
                        modifier = Modifier
                            .padding(20.dp)
                            .size(38.dp)
                    )
                }

                if (loadingName) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = user?.email ?: "غير مسجل الدخول",
                    color = MarketMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                profileError?.let { message ->
                    Text(
                        text = message,
                        color = MarketRed,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Text(
            text = "إدارة الحساب",
            style = MaterialTheme.typography.titleMedium
        )

        SettingsAction(
            title = "تسجيل الخروج",
            subtitle = "الخروج من الحساب على هذا الجهاز",
            icon = Icons.AutoMirrored.Outlined.ExitToApp,
            color = MarketGreen,
            onClick = { showLogout = true }
        )

        SettingsAction(
            title = "حذف حساب الدخول",
            subtitle = "حذف الحساب من Firebase Authentication",
            icon = Icons.Outlined.DeleteOutline,
            color = MarketRed,
            onClick = {
                password = ""
                deleteError = null
                showDelete = true
            }
        )
    }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = {
                Text(text = "تسجيل الخروج")
            },
            text = {
                Text(
                    text = "هل تريد تسجيل الخروج من حسابك؟"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogout = false
                        auth.signOut()
                        onSessionEnded()
                    }
                ) {
                    Text(text = "تسجيل الخروج")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogout = false }
                ) {
                    Text(text = "إلغاء")
                }
            }
        )
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = {
                if (!busy) {
                    password = ""
                    showDelete = false
                }
            },
            title = {
                Text(text = "حذف حساب الدخول")
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(
                        rememberScrollState()
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "أدخل كلمة مرورك لتأكيد حذف حساب الدخول نهائيًا."
                    )

                    Text(
                        text = "هذا الإجراء لا يحذف تلقائيًا بيانات Firestore " +
                                "أو الصور أو السلة المحلية.",
                        color = MarketRed,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(text = "كلمة المرور")
                        },
                        singleLine = true,
                        enabled = !busy,
                        visualTransformation =
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    deleteError?.let { message ->
                        Text(
                            text = message,
                            color = MarketRed,
                            fontSize = 12.sp
                        )
                    }

                    if (busy) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !busy && password.isNotEmpty(),
                    onClick = {
                        val currentUser = auth.currentUser
                        val email = currentUser?.email

                        if (currentUser == null || email.isNullOrBlank()) {
                            deleteError =
                                "لا توجد جلسة بريد إلكتروني صالحة."
                        } else {
                            busy = true
                            deleteError = null

                            val credential =
                                EmailAuthProvider.getCredential(
                                    email,
                                    password
                                )

                            currentUser.reauthenticate(credential)
                                .addOnCompleteListener { reauthTask ->
                                    if (!reauthTask.isSuccessful) {
                                        busy = false
                                        deleteError =
                                            "تعذر التحقق. تأكد من كلمة المرور والاتصال."
                                    } else {
                                        currentUser.delete()
                                            .addOnCompleteListener { deleteTask ->
                                                busy = false

                                                if (deleteTask.isSuccessful) {
                                                    password = ""
                                                    showDelete = false
                                                    auth.signOut()
                                                    onSessionEnded()
                                                } else {
                                                    deleteError =
                                                        "تعذر حذف الحساب. حاول مجددًا."
                                                }
                                            }
                                    }
                                }
                        }
                    }
                ) {
                    Text(
                        text = "حذف نهائي",
                        color = MarketRed
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !busy,
                    onClick = {
                        password = ""
                        showDelete = false
                    }
                ) {
                    Text(text = "إلغاء")
                }
            }
        )
    }
}

@Composable
private fun SettingsAction(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = color.copy(alpha = 0.08f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.padding(11.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    color = color,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = MarketMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}