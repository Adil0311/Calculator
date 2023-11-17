package com.example.calculator


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttons= ArrayList<Button>()
        val zeroButton = findViewById<Button>(R.id.zero)
        buttons.add(zeroButton)
        val oneButton = findViewById<Button>(R.id.one)
        buttons.add(oneButton)
        val twoButton = findViewById<Button>(R.id.two)
        buttons.add(twoButton)
        val threeButton = findViewById<Button>(R.id.three)
        buttons.add(threeButton)
        val fourButton = findViewById<Button>(R.id.four)
        buttons.add(fourButton)
        val fiveButton = findViewById<Button>(R.id.five)
        buttons.add(fiveButton)
        val sixButton = findViewById<Button>(R.id.six)
        buttons.add(sixButton)
        val sevenButton = findViewById<Button>(R.id.seven)
        buttons.add(sevenButton)
        val eightButton = findViewById<Button>(R.id.eight)
        buttons.add(eightButton)
        val nineButton = findViewById<Button>(R.id.nine)
        buttons.add(nineButton)
        val dotButton = findViewById<Button>(R.id.buttonDot)
        buttons.add(dotButton)
        val plusButton = findViewById<Button>(R.id.plus)
        buttons.add(plusButton)
        val minusButton = findViewById<Button>(R.id.buttonMinus)
        buttons.add(minusButton)
        val multiplyButton = findViewById<Button>(R.id.multiply)
        buttons.add(multiplyButton)
        val divideButton = findViewById<Button>(R.id.divide)
        buttons.add(divideButton)
        val equalButton = findViewById<Button>(R.id.equals)
        buttons.add(equalButton)
        val clearButton = findViewById<Button>(R.id.cancel_button)
        buttons.add(clearButton)

        val resultText=findViewById<TextView>(R.id.result)

        buttons.forEach{ button ->
            if(button.text.toString()!= "=" && button.text.toString()!= "C")
                button.setOnClickListener{
                    inserisciSimbolo(button.text.toString(), resultText)
                }

        }

        clearButton.setOnClickListener{
            clear(resultText)
        }

        equalButton.setOnClickListener{
            equals(resultText)
        }




    }
    fun inserisciSimbolo(simbolo: String, resultText: TextView){
        if(resultText.text.toString()=="0")
            resultText.text=""
        resultText.text=resultText.text.toString()+simbolo
    }

    fun clear(resultText: TextView){
        resultText.text="0"
    }

    fun equals(resultText: TextView){
        resultText.text = eval(resultText.text.toString()).toString()


    }

    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm() // addition
                    else if (eat('-'.code)) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor() // multiplication
                    else if (eat('/'.code)) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return +parseFactor() // unary plus
                if (eat('-'.code)) return -parseFactor() // unary minus
                var x: Double
                val startPos = pos
                if (eat('('.code)) { // parentheses
                    x = parseExpression()
                    if (!eat(')'.code)) throw RuntimeException("Missing ')'")
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) { // numbers
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code) { // functions
                    while (ch >= 'a'.code && ch <= 'z'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    if (eat('('.code)) {
                        x = parseExpression()
                        if (!eat(')'.code)) throw RuntimeException("Missing ')' after argument to $func")
                    } else {
                        x = parseFactor()
                    }
                    x =
                        if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(
                            Math.toRadians(
                                x
                            )
                        ) else if (func == "cos") Math.cos(
                            Math.toRadians(x)
                        ) else if (func == "tan") Math.tan(Math.toRadians(x)) else throw RuntimeException(
                            "Unknown function: $func"
                        )
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                if (eat('^'.code)) x = Math.pow(x, parseFactor()) // exponentiation
                return x
            }
        }.parse()
    }
}