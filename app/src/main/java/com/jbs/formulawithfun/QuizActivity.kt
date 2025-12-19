package com.jbs.formulawithfun

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.jbs.formulawithfun.R

class QuizActivity : AppCompatActivity() {

    private lateinit var questionPool: List<Formula>
    private var currentIndex = 0

    private var correctCount = 0
    private var questionCount = 0
    private val totalQuestions = 10
    private val maxSkips = 3
    private var skipCount = 0

    // UI
    private lateinit var tvScore: TextView
    private lateinit var tvQuestionCount: TextView
    private lateinit var tvQuizQuestion: TextView
    private lateinit var btnOption1: Button
    private lateinit var btnOption2: Button
    private lateinit var btnOption3: Button
    private lateinit var btnOption4: Button
    private lateinit var btnSubmit: Button
    private lateinit var btnSkip: Button

    // Option logic
    private var correctOption = -1
    private var selectedOption = -1
    private var options: List<String> = emptyList()
    private var askForFormula = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // Bind UI
        tvScore = findViewById(R.id.tvScore)
        tvQuestionCount = findViewById(R.id.tvQuestionCount)
        tvQuizQuestion = findViewById(R.id.tvQuizQuestion)
        btnOption1 = findViewById(R.id.btnOption1)
        btnOption2 = findViewById(R.id.btnOption2)
        btnOption3 = findViewById(R.id.btnOption3)
        btnOption4 = findViewById(R.id.btnOption4)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnSkip = findViewById(R.id.btnNext)

        val prefs = getSharedPreferences("formula_fun_prefs", Context.MODE_PRIVATE)
        val standard = prefs.getInt("user_standard", 8)
        questionPool = (QuizData.formulasByStandard[standard] ?: emptyList()).shuffled()

        correctCount = 0
        questionCount = 0
        skipCount = 0
        updateScoreViews()

        btnOption1.setOnClickListener { selectOption(0) }
        btnOption2.setOnClickListener { selectOption(1) }
        btnOption3.setOnClickListener { selectOption(2) }
        btnOption4.setOnClickListener { selectOption(3) }

        btnSubmit.setOnClickListener {
            if (selectedOption == -1) {
                android.widget.Toast.makeText(this, "Please select an option!", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                checkAnswer()
            }
        }

        // ✅ Updated skip button logic
        btnSkip.text = "Skip ($maxSkips left)"
        btnSkip.setOnClickListener {
            if (skipCount < maxSkips) {
                skipCount++
                questionCount++
                val remaining = maxSkips - skipCount
                android.widget.Toast.makeText(this, "Question skipped! ($remaining skips left)", android.widget.Toast.LENGTH_SHORT).show()

                if (remaining > 0) {
                    btnSkip.text = "Skip ($remaining left)"
                } else {
                    btnSkip.text = "Skip (0 left)"
                    btnSkip.isEnabled = false
                }

                nextQuestion()
            }
        }

        nextQuestion()
    }

    private fun updateScoreViews() {
        tvScore.text = "Score: $correctCount"
        tvQuestionCount.text = "${questionCount + 1}/$totalQuestions"
    }

    private fun resetOptionsUI() {
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)
        for (btn in optionButtons) {
            btn.isEnabled = true
            btn.isSelected = false
            btn.setTypeface(null, Typeface.NORMAL)
        }
        selectedOption = -1
    }

    private fun selectOption(index: Int) {
        selectedOption = index
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)
        optionButtons.forEachIndexed { i, btn ->
            btn.isSelected = (i == index)
            btn.setTypeface(null, if (i == index) Typeface.BOLD else Typeface.NORMAL)

            if (i == index) {
                val anim = AnimationUtils.loadAnimation(this, R.anim.option_scale_up)
                btn.startAnimation(anim)
            } else {
                btn.clearAnimation()
            }
        }
    }

    private fun setOptionCorrect(btn: Button) {
        btn.isSelected = true
        btn.setTypeface(null, Typeface.BOLD)
    }

    private fun setOptionWrong(btn: Button) {
        btn.isSelected = false
        btn.setTypeface(null, Typeface.BOLD)
    }

    private fun nextQuestion() {
        if (questionCount >= totalQuestions) {
            showScoreDialog()
            return
        }
        resetOptionsUI()
        btnSubmit.isEnabled = true

        val qIndex = questionCount % questionPool.size
        val formula = questionPool[qIndex]

        askForFormula = (0..1).random() == 0
        if (askForFormula) {
            tvQuizQuestion.text = "What is the formula for:\n${formula.title}?"
        } else {
            tvQuizQuestion.text = "Which formula has this expression:\n${formula.explanation}?"
        }

        val (opts, correctIdx) = makeOptions(formula, askForFormula)
        options = opts
        correctOption = correctIdx
        btnOption1.text = options[0]
        btnOption2.text = options[1]
        btnOption3.text = options[2]
        btnOption4.text = options[3]

        updateScoreViews()
    }

    private fun makeOptions(formula: Formula, askForFormula: Boolean): Pair<List<String>, Int> {
        val all = questionPool.filter { it != formula }
        val correct = if (askForFormula) formula.explanation else formula.title

        val distractors = all
            .shuffled()
            .map { if (askForFormula) it.explanation else it.title }
            .filter { it != correct }
            .distinct()
            .take(3)
            .toMutableList()

        while (distractors.size < 3) {
            distractors.add("N/A")
        }

        val options = distractors.toMutableList()
        val correctIdx = (0..3).random()
        options.add(correctIdx, correct)

        return Pair(options, correctIdx)
    }

    private fun checkAnswer() {
        btnSubmit.isEnabled = false
        val optionButtons = listOf(btnOption1, btnOption2, btnOption3, btnOption4)
        if (selectedOption == correctOption) {
            setOptionCorrect(optionButtons[selectedOption])
            correctCount++
            showResultPopup(true)
        } else {
            setOptionWrong(optionButtons[selectedOption])
            setOptionCorrect(optionButtons[correctOption])
            showResultPopup(false)
        }
        for (btn in optionButtons) btn.isEnabled = false

        questionCount++
        updateScoreViews()
    }

    private fun showResultPopup(isCorrect: Boolean) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.quiz_result_popup)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        val imgResult = dialog.findViewById<ImageView>(R.id.imgResult)
        val tvResult = dialog.findViewById<TextView>(R.id.tvResult)

        if (isCorrect) {
            imgResult.setImageResource(R.drawable.ic_correct)
            tvResult.text = "Correct!"
            tvResult.setTextColor(resources.getColor(R.color.colorPrimary))
        } else {
            imgResult.setImageResource(R.drawable.ic_wrong)
            tvResult.text = "Wrong!"
            tvResult.setTextColor(resources.getColor(android.R.color.holo_red_dark))
        }

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            nextQuestion()
        }, 1200)

        dialog.show()
    }

    private fun showScoreDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Quiz Finished")
            .setMessage("Your score: $correctCount/$totalQuestions")
            .setCancelable(false)
            .setPositiveButton("Try Again") { _, _ ->
                correctCount = 0
                questionCount = 0
                skipCount = 0
                btnSkip.isEnabled = true
                btnSkip.text = "Skip ($maxSkips left)"
                nextQuestion()
            }
            .setNegativeButton("Exit") { _, _ ->
                finish()
            }
            .show()

        btnOption1.isEnabled = false
        btnOption2.isEnabled = false
        btnOption3.isEnabled = false
        btnOption4.isEnabled = false
        btnSubmit.isEnabled = false
        btnSkip.isEnabled = false
    }
}
