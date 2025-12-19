package com.jbs.formulawithfun

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OptionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Guard: if user hasn't chosen a standard, redirect to StandardSelectionActivity
        val prefs = getSharedPreferences("formula_fun_prefs", Context.MODE_PRIVATE)
        if (!prefs.contains("user_standard")) {
            startActivity(Intent(this, StandardSelectionActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_options)

        val btnAllFormulas = findViewById<Button>(R.id.btnAllFormulas)
        val btnSpecificFormula = findViewById<Button>(R.id.btnSpecificFormula)
        val btnMarkedFormulas = findViewById<Button>(R.id.btnMarkedFormulas)
        val btnQuiz = findViewById<Button>(R.id.btnQuizFromOptions)

        btnAllFormulas.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("skipRedirect", true)
            startActivity(intent)
        }

        btnSpecificFormula.setOnClickListener {
            val intent = Intent(this, SpecificFormulaActivity::class.java)
            startActivity(intent)
        }

        btnMarkedFormulas.setOnClickListener {
            val intent = Intent(this, MarkedFormulasActivity::class.java)
            startActivity(intent)
        }

        btnQuiz.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

    }
}
