package com.example.jogo_da_velha

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jogo_da_velha.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Jogodavelha()
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun Jogodavelha() {
    val jogo = remember { Jogo() }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Scaffold(
            content = { Tabuleiro(jogo) }
        )
    }
}

@Composable
fun Tabuleiro(jogo: Jogo) {
    Image(
        modifier = Modifier.fillMaxSize(),
        painter = painterResource(id = R.drawable.fundo),
        contentDescription = "Background",
        contentScale = ContentScale.Crop,
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            modifier = Modifier.size(250.dp),
            contentDescription = "Jogo da Velha"
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
        ) {
            Bloco(jogo, 0, 0)
            Bloco(jogo, 0, 1)
            Bloco(jogo, 0, 2)
        }
        Row(
        ) {
            Bloco(jogo, 1, 0)
            Bloco(jogo, 1, 1)
            Bloco(jogo, 1, 2)
        }
        Row(
        ) {
            Bloco(jogo, 2, 0)
            Bloco(jogo, 2, 1)
            Bloco(jogo, 2, 2)
        }
        if (jogo.fimdejogo) {
            Button(
                onClick = { jogo.reiniciar() },
                modifier = Modifier.padding(top = 35.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
            ) {
                Text(text = "JOGAR NOVAMENTE", color = Color.White)
            }
        }
    }
}

@Composable
fun Bloco(jogo: Jogo, row: Int, col: Int) {
    val blocoValue by remember { jogo.blocos[row][col] }

    Box(
        modifier = Modifier
            .size(64.dp)
            .clickable(enabled = blocoValue == null && !jogo.fimdejogo) {
                jogo.iniciar(row, col)
            },
        contentAlignment = Alignment.Center,
    ) {
        when (blocoValue) {
            Jogador.X -> Text(text = "X", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            Jogador.O -> Text(text = "O", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 25.sp)

            else -> {}
        }

        if (jogo.celuaselecionada.contains(row to col)) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(BorderStroke(2.dp, Color.Green), RoundedCornerShape(8.dp))
            )
        } else {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(BorderStroke(2.dp, Color.Black), RoundedCornerShape(8.dp))
            )
        }
    }
}

enum class Jogador {
    X,
    O
}

class Jogo {
    private var jogadoratual: Jogador = Jogador.X
    private var movimento: Int = 0
    val blocos = List(3) { row ->
        List(3) { col ->
            mutableStateOf<Jogador?>(null)
        }
    }
    var fimdejogo by mutableStateOf(false)
        private set
    var ganhador by mutableStateOf<Jogador?>(null)
        private set
    var celuaselecionada by mutableStateOf<List<Pair<Int, Int>>>(emptyList())
        private set

    fun iniciar(row: Int, col: Int) {
        if (fimdejogo || blocos[row][col].value != null) {
            return
        }
        movimento++
        blocos[row][col].value = jogadoratual
        conferir_ganhador(row, col)
        if (ganhador == null && movimento == 9) {
            fimdejogo = true
        }
        jogadoratual = if (jogadoratual == Jogador.X) Jogador.O else Jogador.X
    }

    private fun conferir_ganhador(row: Int, col: Int) {
        if (blocos[row][0].value == jogadoratual &&
            blocos[row][1].value == jogadoratual &&
            blocos[row][2].value == jogadoratual
        ) {
            ganhador = jogadoratual
            celuaselecionada = listOf(row to 0, row to 1, row to 2)
            fimdejogo = true
        } else if (blocos[0][col].value == jogadoratual &&
            blocos[1][col].value == jogadoratual &&
            blocos[2][col].value == jogadoratual
        ) {
            ganhador = jogadoratual
            celuaselecionada = listOf(0 to col, 1 to col, 2 to col)
            fimdejogo = true
        } else if (row == col &&
            blocos[0][0].value == jogadoratual &&
            blocos[1][1].value == jogadoratual &&
            blocos[2][2].value == jogadoratual
        ) {
            ganhador = jogadoratual
            celuaselecionada = listOf(0 to 0, 1 to 1, 2 to 2)
            fimdejogo = true
        } else if (row + col == 2 &&
            blocos[0][2].value == jogadoratual &&
            blocos[1][1].value == jogadoratual &&
            blocos[2][0].value == jogadoratual
        ) {
            ganhador = jogadoratual
            celuaselecionada = listOf(0 to 2, 1 to 1, 2 to 0)
            fimdejogo = true
        }
    }

    fun reiniciar() {
        jogadoratual = Jogador.X
        movimento = 0
        fimdejogo = false
        ganhador = null
        celuaselecionada = emptyList()
        blocos.forEach { row ->
            row.forEach { bloco ->
                bloco.value = null
            }
        }
    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        Jogodavelha()
    }
}
