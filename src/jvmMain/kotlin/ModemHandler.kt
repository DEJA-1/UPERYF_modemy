import com.fazecast.jSerialComm.SerialPort
import java.io.InputStream
import java.io.OutputStream

class ModemHandler(
    private val portName: String,
    private val baudRate: Int,
) {
    private var port: SerialPort? = null

    fun connect() {
        val port = SerialPort.getCommPort(portName)
        port.baudRate = baudRate
        port.numDataBits = 8
        port.numStopBits = SerialPort.ONE_STOP_BIT
        port.parity = SerialPort.NO_PARITY

        if (!port.openPort()) {
            throw Exception("Nie udało się otworzyć portu $portName")
        }
        println("Podłączono do portu $portName z prędkością $baudRate")

        this.port = port
    }

    fun sendCommand(command: String) {
        port?.let {
            try {
                val outputStream: OutputStream = it.outputStream
                outputStream.write((command + "\r").toByteArray())
                outputStream.flush()
                println("Wysłano: $command")
            } catch (e: Exception) {
                println("Błąd: ${e.message}")
            }
        } ?: error("Port nie zainicjalizowany. Użyj .connect() przed wysyłaniem komend.")
    }

    fun readResponse(): String? {
        port?.let {
            try {
                val inputStream: InputStream = it.inputStream
                val response = StringBuilder()
                val buffer = ByteArray(1024)
                var numBytes: Int
                Thread.sleep(100)
                while (inputStream.available() > 0) {
                    numBytes = inputStream.read(buffer)
                    response.append(String(buffer, 0, numBytes))
                }
                return response.toString()
            } catch (e: Exception) {
                println("Błąd: ${e.message}")
            }
        } ?: error("Port nie zainicjalizowany. Użyj .connect() przed odbieraniem odpowiedzi.")

        return null
    }

    fun disconnect() {
        port?.let {
            if (it.isOpen) {
                it.closePort()
                println("Port $portName został zamknięty.")
            } else {
                println("Port $portName był już zamknięty.")
            }
            port = null
        }  ?: error("Port nie zainicjalizowany. Użyj .connect() przed zerwaniem połączenia.")
    }
}