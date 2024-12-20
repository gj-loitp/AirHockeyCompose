package com.dbtechprojects.airhockey

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dbtechprojects.airhockey.ui.playerVCPU.*
import com.dbtechprojects.airhockey.ui.playerVCPU.playerVsCpuState
import com.dbtechprojects.airhockey.ui.shared.GameState
import com.dbtechprojects.airhockey.ui.shared.GameTypeState
import com.dbtechprojects.airhockey.ui.theme.AirHockeyComposeTheme
import com.dbtechprojects.airhockey.ui.twoPlayerLocal.TwoPlayerGameBoard
import com.dbtechprojects.airhockey.ui.twoPlayerLocal.twoPlayerLocalState
import com.google.android.gms.security.ProviderInstaller


class MainActivity : ComponentActivity() {

    private lateinit var gameState: MutableState<GameState>
    private lateinit var gameTypeState: MutableState<GameTypeState>

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Install ssl if needed
        // https://stackoverflow.com/questions/53583016/api-call-on-android-19-emulator-isconnected-failed-ehostunreach-no-route-to
        ProviderInstaller.installIfNeededAsync(this, object :
            ProviderInstaller.ProviderInstallListener {
            override fun onProviderInstalled() {}
            override fun onProviderInstallFailed(i: Int, intent: Intent?) {
                Log.i("main", "Provider install failed ($i) : SSL Problems may occurs")
            }
        })
        setContent {
            AirHockeyComposeTheme {
                // A surface container using the 'background' color from the theme

                gameTypeState = remember {
                    mutableStateOf(GameTypeState.INITIAL)
                }

                when (gameTypeState.value) {
                    GameTypeState.INITIAL -> {
                        gameState = remember { mutableStateOf(GameState()) }
                    }

                    GameTypeState.PLAYER_VS_CPU -> {
                        gameState.value = playerVsCpuState(gameState = gameState.value)
                    }

                    GameTypeState.TWO_PLAYER_LOCAL -> {
                        gameState.value = twoPlayerLocalState(gameState = gameState.value)
                    }

                    GameTypeState.TWO_PLAYER_ONLINE -> {

                    }
                }

                MainScreen(
                    playerVsCpu =
                    {
                        gameTypeState.value = GameTypeState.PLAYER_VS_CPU
                    },
                    twoPlayerLocal =
                    {
                        gameTypeState.value = GameTypeState.TWO_PLAYER_LOCAL
                    },
                    twoPlayerOnline =
                    {
                        gameTypeState.value = GameTypeState.TWO_PLAYER_ONLINE
                    },
                    gameState = gameState,
                    gameTypeState = gameTypeState,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@ExperimentalComposeUiApi
@Composable
fun MainScreen(
    playerVsCpu: () -> Unit,
    twoPlayerLocal: () -> Unit,
    twoPlayerOnline: () -> Unit,
    gameState: MutableState<GameState>,
    gameTypeState: MutableState<GameTypeState>,
) {


    Surface(
        color = MaterialTheme.colors.background, modifier = Modifier
            .fillMaxSize()
    ) {

        Box(Modifier.padding(10.dp)) {

            when (gameTypeState.value) {
                GameTypeState.INITIAL -> {
                    GameBoard(gameState.value)
                }

                GameTypeState.PLAYER_VS_CPU -> {
                    GameBoard(gameState.value)
                }

                GameTypeState.TWO_PLAYER_LOCAL -> {
                    TwoPlayerGameBoard(playerVsCpu, gameState.value, twoPlayerLocal)
                }

                GameTypeState.TWO_PLAYER_ONLINE -> {

                }
            }

            if (!gameState.value.menuState.value) {
                GameMenu(
                    playerVsCpuState = playerVsCpu,
                    twoPlayerLocal = twoPlayerLocal,
                    twoPlayerOnline = twoPlayerOnline,
                    onGameButtonClick = { gameState.value.menuState.value = true })
            }
        }

    }
}