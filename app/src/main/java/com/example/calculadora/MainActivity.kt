package com.example.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var tvEntrada: TextView
    private lateinit var tvOperacion: TextView

    private var numeroActual   = ""
    private var numeroAnterior = ""
    private var operador       = ""
    private var resultado      = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvEntrada   = findViewById(R.id.tvEntrada)
        tvOperacion = findViewById(R.id.tvOperacion)

        // Dígitos
        mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9"
        ).forEach { (id, digito) ->
            findViewById<MaterialButton>(id).setOnClickListener { ingresarDigito(digito) }
        }

        // Punto decimal
        findViewById<MaterialButton>(R.id.btnPunto).setOnClickListener { ingresarPunto() }

        // Operadores
        findViewById<MaterialButton>(R.id.btnSumar).setOnClickListener       { seleccionarOperador("+") }
        findViewById<MaterialButton>(R.id.btnRestar).setOnClickListener      { seleccionarOperador("−") }
        findViewById<MaterialButton>(R.id.btnMultiplicar).setOnClickListener { seleccionarOperador("×") }
        findViewById<MaterialButton>(R.id.btnDividir).setOnClickListener     { seleccionarOperador("÷") }

        // Funciones
        findViewById<MaterialButton>(R.id.btnIgual).setOnClickListener    { calcularResultado() }
        findViewById<MaterialButton>(R.id.btnLimpiar).setOnClickListener  { limpiar() }
        findViewById<MaterialButton>(R.id.btnPlusMinus).setOnClickListener { cambiarSigno() }
        findViewById<MaterialButton>(R.id.btnBorrar).setOnClickListener   { borrarUltimo() }
    }

    private fun ingresarDigito(digito: String) {
        if (resultado) { numeroActual = ""; resultado = false }
        if (numeroActual == "0" && digito == "0") return
        if (numeroActual == "0" && digito != ".") numeroActual = ""
        numeroActual += digito
        tvEntrada.text = numeroActual
    }

    private fun ingresarPunto() {
        if (resultado) { numeroActual = "0"; resultado = false }
        if (numeroActual.isEmpty()) numeroActual = "0"
        if (!numeroActual.contains(".")) {
            numeroActual += "."
            tvEntrada.text = numeroActual
        }
    }

    private fun seleccionarOperador(op: String) {
        if (numeroActual.isEmpty() && numeroAnterior.isEmpty()) return
        if (numeroAnterior.isNotEmpty() && numeroActual.isNotEmpty()) calcularResultado()
        operador = op
        if (numeroActual.isNotEmpty()) {
            numeroAnterior = numeroActual
            numeroActual   = ""
        }
        resultado = false
        tvOperacion.text = "$numeroAnterior $operador"
    }

    private fun calcularResultado() {
        if (operador.isEmpty() || numeroAnterior.isEmpty() || numeroActual.isEmpty()) return
        val n1 = numeroAnterior.toDoubleOrNull() ?: return
        val n2 = numeroActual.toDoubleOrNull()   ?: return

        val res = when (operador) {
            "+"  -> n1 + n2
            "−"  -> n1 - n2
            "×"  -> n1 * n2
            "÷"  -> {
                if (n2 == 0.0) {
                    tvEntrada.text   = "Error"
                    tvOperacion.text = "No se puede dividir entre 0"
                    limpiarEstado(); return
                }
                n1 / n2
            }
            else -> return
        }

        val resTexto = if (res == res.toLong().toDouble()) {
            res.toLong().toString()
        } else {
            "%.10f".format(res).trimEnd('0').trimEnd('.')
        }

        tvOperacion.text = "$numeroAnterior $operador $numeroActual ="
        tvEntrada.text   = resTexto
        numeroAnterior   = resTexto
        numeroActual     = resTexto
        operador         = ""
        resultado        = true
    }

    private fun borrarUltimo() {
        if (resultado) { limpiar(); return }
        if (numeroActual.isNotEmpty()) {
            numeroActual = numeroActual.dropLast(1)
            tvEntrada.text = if (numeroActual.isEmpty()) "0" else numeroActual
        }
    }

    private fun limpiar() {
        limpiarEstado()
        tvEntrada.text   = "0"
        tvOperacion.text = ""
    }

    private fun limpiarEstado() {
        numeroActual   = ""
        numeroAnterior = ""
        operador       = ""
        resultado      = false
    }

    private fun cambiarSigno() {
        if (numeroActual.isEmpty() || numeroActual == "0") return
        numeroActual = if (numeroActual.startsWith("-")) {
            numeroActual.substring(1)
        } else {
            "-$numeroActual"
        }
        tvEntrada.text = numeroActual
    }
}