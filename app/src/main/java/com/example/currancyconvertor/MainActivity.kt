package com.example.currancyconvertor

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import com.example.currancyconvertor.models.CurrencyData
import com.example.currancyconvertor.utils.isInternet
import okhttp3.*
import java.io.IOException
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var spinner: Spinner
    private lateinit var convertButton: Button
    private lateinit var editText: EditText
    private lateinit var resultTextView: TextView
    private var currencies: List<String> = listOf()
    private var selectedCurrency: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        setupSpinnerAdapter()

        convertButton.setOnClickListener {
            handleConvertButtonClick()
        }
    }

    private fun initView() {
        spinner = findViewById(R.id.spinner)
        convertButton = findViewById(R.id.button)
        editText = findViewById(R.id.editTextNumber)
        resultTextView = findViewById(R.id.textView)
    }

    private fun setupSpinnerAdapter() {
        if (isInternet(this)) {
            lifecycleScope.launch {
                currencies = loadItemsFromAPI()

                if (currencies.isEmpty()) {
                    showToast(getString((R.string.api_doesnt_work)))
                } else {
                    setupSpinner(this@MainActivity, spinner, currencies)
                }
            }
        } else {
            currencies = resources.getStringArray(R.array.Currencies).toList()
            setupSpinner(this@MainActivity, spinner, currencies)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (currencies.isNotEmpty()) {
                    selectedCurrency = currencies[position]
                    showToast(getString((R.string.selected_currency), selectedCurrency))
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun handleConvertButtonClick() {
        if (isInternet(this)) {
            lifecycleScope.launch {
                try {
                    val nominal = editText.text.toString().toIntOrNull()
                    if (nominal != null) {
                        val response = Convert().getCurrencyData(selectedCurrency, nominal)
                        resultTextView.text = getString(R.string.result, response)
                    } else {
                        showToast(getString(R.string.invalid_input))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showToast(getString(R.string.error))
                }
            }
        } else {
            showToast(getString(R.string.internet_isnt_avalible))
        }

        if (spinner.adapter.count <= 3 && isInternet(this)) {
            lifecycleScope.launch {
                currencies = loadItemsFromAPI()
                if (currencies.isEmpty()) {
                    showToast(getString(R.string.api_doesnt_work))
                } else {
                    setupSpinner(this@MainActivity, spinner, currencies)
                }
            }
        }
    }

    private fun setupSpinner(context: Context, spinner: Spinner, currencies: List<String>) {
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, currencies)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private suspend fun loadItemsFromAPI(): List<String> {
        return try {
                Convert().getCurrencyNames()
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}