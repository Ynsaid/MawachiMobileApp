package com.example.mawachiapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mawachiapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setContent {
            MaterialTheme {
                SignupScreen(
                    isLoading = isLoading,
                    onSignup = { name, email, number, password, repeatedPassword ->

                        val cleanEmail = email.trim()

                        when {
                            cleanEmail.isEmpty() ||
                                    password.isEmpty() ||
                                    repeatedPassword.isEmpty() -> {
                                showToast("All fields are required")
                            }

                            password != repeatedPassword -> {
                                showToast("Passwords do not match")
                            }

                            else -> {
                                registerUser(
                                    name = name.trim(),
                                    email = cleanEmail,
                                    number = number.trim(),
                                    password = password
                                )
                            }
                        }
                    },
                    onLogin = {
                        openLogin()
                    }
                )
            }
        }
    }

    private fun registerUser(
        name: String,
        email: String,
        number: String,
        password: String
    ) {
        if (isLoading) return

        isLoading = true

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    isLoading = false

                    showToast(
                        "Registration Failed: " +
                                (task.exception?.localizedMessage ?: "Unknown error")
                    )

                    return@addOnCompleteListener
                }

                val uid = task.result?.user?.uid

                if (uid.isNullOrBlank()) {
                    isLoading = false
                    showToast("Unable to retrieve user ID")
                    return@addOnCompleteListener
                }

                val userMap = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "number" to number
                )

                db.collection("users")
                    .document(uid)
                    .set(userMap)
                    .addOnSuccessListener {
                        isLoading = false

                        showToast("Registration Successful")
                        openLogin()
                    }
                    .addOnFailureListener { exception ->
                        isLoading = false

                        showToast(
                            "Account created, but failed to save user info: " +
                                    exception.localizedMessage
                        )
                    }
            }
    }

    private fun openLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java)
        )
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
private fun SignupScreen(
    isLoading: Boolean,
    onSignup: (
        name: String,
        email: String,
        number: String,
        password: String,
        repeatedPassword: String
    ) -> Unit,
    onLogin: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var number by rememberSaveable { mutableStateOf("") }

    var password by remember { mutableStateOf("") }
    var repeatedPassword by remember { mutableStateOf("") }

    val changaBold = FontFamily(Font(R.font.changa_bold))
    val changaMedium = FontFamily(Font(R.font.changa_medium))

    val oliveGreen = colorResource(R.color.olive_green)
    val softWhite = colorResource(R.color.soft_white)
    val white = colorResource(R.color.white)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(softWhite)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(62.dp))

        Text(
            text = stringResource(R.string.create_acc),

            fontFamily = changaBold,
            fontSize = 34.sp,
            color = oliveGreen,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        SignupInput(
            value = name,
            onValueChange = { name = it },
            hint = stringResource(R.string.complete_name),
            textAlign = TextAlign.End,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        SignupInput(
            value = email,
            onValueChange = { email = it },
            hint = stringResource(R.string.email),
            textAlign = TextAlign.End,
            keyboardType = KeyboardType.Email,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))


        SignupInput(
            value = password,
            onValueChange = { password = it },
            hint = stringResource(R.string.password),
            isPassword = true,
            textAlign = TextAlign.End,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        SignupInput(
            value = repeatedPassword,
            onValueChange = { repeatedPassword = it },
            hint = stringResource(R.string.password),
            isPassword = true,
            textAlign = TextAlign.End,
            enabled = !isLoading
        )

        Spacer(modifier = Modifier.height(62.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isLoading) {
                        oliveGreen.copy(alpha = 0.65f)
                    } else {
                        oliveGreen
                    }
                )
                .clickable(
                    enabled = !isLoading,
                    onClick = {
                        onSignup(
                            name,
                            email,
                            number,
                            password,
                            repeatedPassword
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = white,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.create_acc),
                    fontFamily = changaBold,
                    fontSize = 24.sp,
                    color = white
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.have_account),
            fontFamily = changaMedium,
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    enabled = !isLoading,
                    onClick = onLogin
                )
        )
    }
}

@Composable
private fun SignupInput(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    textAlign: TextAlign = TextAlign.Start
) {
    val changaMedium = FontFamily(Font(R.font.changa_medium))
    val oliveGreen = colorResource(R.color.olive_green)

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(10.dp)

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        singleLine = true,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(oliveGreen),
        textStyle = TextStyle(
            fontFamily = changaMedium,
            fontSize = 16.sp,
            color = Color.Black,
            textAlign = textAlign
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) {
                KeyboardType.Password
            } else {
                keyboardType
            }
        ),
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(shape)
            .background(Color.White)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) {
                    oliveGreen
                } else {
                    oliveGreen.copy(alpha = 0.35f)
                },
                shape = shape
            )
            .padding(horizontal = 8.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = hint,
                        fontFamily = changaMedium,
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = textAlign,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    propagateMinConstraints = true
                ) {
                    innerTextField()
                }
            }
        }
    )
}