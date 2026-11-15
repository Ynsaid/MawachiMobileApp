package com.example.mawachiapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mawachiapp.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    private var isLoading by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            openHome()
            return
        }

        setContent {
            MaterialTheme {
                LoginScreen(
                    isLoading = isLoading,
                    onLogin = { email, password ->
                        loginUser(
                            email = email.trim(),
                            password = password
                        )
                    },
                    onCreateAccount = {
                        startActivity(
                            Intent(
                                this,
                                SignupActivity::class.java
                            )
                        )

                        finish()
                    }
                )
            }
        }
    }

    private fun loginUser(
        email: String,
        password: String
    ) {
        if (isLoading) return

        if (email.isBlank() || password.isEmpty()) {
            showToast("يرجى إدخال البريد الإلكتروني وكلمة المرور")
            return
        }

        isLoading = true

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                isLoading = false


                if (isFinishing || isDestroyed) {
                    return@addOnCompleteListener
                }

                if (task.isSuccessful) {
                    showToast("تم تسجيل الدخول بنجاح")
                    openHome()
                } else {
                    showToast(
                        "تعذر تسجيل الدخول. تحقق من بياناتك واتصال الإنترنت."
                    )
                }
            }
    }

    private fun openHome() {
        startActivity(
            Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
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
private fun LoginScreen(
    isLoading: Boolean,
    onLogin: (email: String, password: String) -> Unit,
    onCreateAccount: () -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val changaBold = remember {
        FontFamily(Font(R.font.changa_bold))
    }

    val changaMedium = remember {
        FontFamily(Font(R.font.changa_medium))
    }

    val oliveGreen = colorResource(R.color.olive_green)
    val softWhite = colorResource(R.color.soft_white)
    val softRed = colorResource(R.color.soft_red)
    val white = colorResource(R.color.white)

    val focusManager = LocalFocusManager.current

    val submitLogin: () -> Unit = {
        if (!isLoading) {
            focusManager.clearFocus()
            onLogin(email, password)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(softWhite)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // بديل المستطيل العلوي في XML.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 32.dp,
                        bottomEnd = 32.dp
                    )
                )
                .background(oliveGreen),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(R.drawable.small_logo),
                contentDescription = null,
                modifier = Modifier.padding(top = 48.dp)
            )
        }

        Spacer(Modifier.height(43.dp))

        Text(
            text = stringResource(R.string.app_name_ar),
            fontFamily = changaBold,
            fontSize = 48.sp,
            color = oliveGreen,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .width(88.dp)
                .height(3.dp)
                .background(softRed)
        )

        Spacer(Modifier.height(43.dp))

        LoginInput(
            value = email,
            onValueChange = { email = it },
            hint = stringResource(R.string.email),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            enabled = !isLoading,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(16.dp))

        LoginInput(
            value = password,
            onValueChange = { password = it },
            hint = stringResource(R.string.password),
            isPassword = true,
            imeAction = ImeAction.Done,
            onDone = submitLogin,
            enabled = !isLoading,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(62.dp))

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
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
                    onClick = submitLogin
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
                    text = stringResource(R.string.login),
                    fontFamily = changaBold,
                    fontSize = 24.sp,
                    color = white
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.no_account),
            fontFamily = changaMedium,
            fontSize = 24.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clickable(
                    enabled = !isLoading,
                    onClick = onCreateAccount
                )
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun LoginInput(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onDone: () -> Unit = {}
) {
    val changaMedium = remember {
        FontFamily(Font(R.font.changa_medium))
    }

    val oliveGreen = colorResource(R.color.olive_green)
    val focusManager = LocalFocusManager.current

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
            textAlign = TextAlign.Right,
            textDirection = TextDirection.Ltr
        ),

        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) {
                KeyboardType.Password
            } else {
                keyboardType
            },
            imeAction = imeAction
        ),

        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(
                    androidx.compose.ui.focus.FocusDirection.Down
                )
            },
            onDone = {
                onDone()
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
                        textAlign = TextAlign.Right,
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