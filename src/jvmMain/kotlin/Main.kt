import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlin.system.exitProcess

@Composable
@Preview
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }

    MaterialTheme {
        Button(onClick = {
            text = "Hello, Desktop!"
        }) {
            Text(text)
        }
    }
}

fun main() {
    val portName = "COM0"
    val baudRate = 9600
    val modemHandler = ModemHandler(portName = portName, baudRate = baudRate)

    modemHandler.tryMakeAction { connect() }

    modemHandler.tryMakeAction { sendCommand(command = "AT") }
    modemHandler.tryMakeAction { println("Odpowiedź: ${readResponse()}") }
    modemHandler.tryMakeAction { sendCommand(command = "ATD123456789") }
    modemHandler.tryMakeAction { println("Odpowiedź: ${readResponse()}") }
    modemHandler.tryMakeAction { sendCommand(command = "ATH") }
    modemHandler.tryMakeAction { println("Odpowiedź: ${readResponse()}") }

    modemHandler.tryMakeAction { disconnect() }
}

private fun ModemHandler.tryMakeAction(operation: ModemHandler.() -> Unit) {
    try {
        this.operation()
    } catch (e: Exception) {
        println("Błąd: ${e.message}")
        exitProcess(status = 1)
    }
}
