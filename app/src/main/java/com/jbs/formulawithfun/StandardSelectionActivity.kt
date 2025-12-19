package com.jbs.formulawithfun

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.jbs.formulawithfun.R

class StandardSelectionActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.standard_selection)

        prefs = getSharedPreferences("formula_fun_prefs", MODE_PRIVATE)

        findViewById<Button>(R.id.btnStd8).setOnClickListener {
            saveStandardAndContinue(8)
        }
        findViewById<Button>(R.id.btnStd9).setOnClickListener {
            saveStandardAndContinue(9)
        }
        findViewById<Button>(R.id.btnStd10).setOnClickListener {
            saveStandardAndContinue(10)
        }
    }

    private fun saveStandardAndContinue(std: Int) {
        prefs.edit().putInt("user_standard", std).apply()
        // Use flags to clear selection activity from back stack
        val intent = Intent(this, OptionsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
}
