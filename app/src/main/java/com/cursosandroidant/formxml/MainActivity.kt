package com.cursosandroidant.formxml

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.cursosandroidant.formxml.databinding.ActivityMainBinding
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

/****
 * Project: Form XML
 * From: com.cursosandroidant.formxml
 * Created by Alain Nicolás Tello on 05/11/22 at 12:17
 * All rights reserved 2022.
 *
 * All my Udemy Courses:
 * https://www.udemy.com/user/alain-nicolas-tello/
 * And Frogames formación:
 * https://cursos.frogamesformacion.com/pages/instructor-alain-nicolas
 *
 * Web: www.alainnicolastello.com
 ***/

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    
    private var textSurname = ""
    private var textHeight = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val countries = arrayOf("Argentina", "Bolivia", "Chile", "Colombia", "Ecuador", "España",
            "Estados Unidos", "México", "Panamá", "Peru", "Uruguay")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,
            countries)
        
        binding.actvCountries.setAdapter(adapter)
        binding.actvCountries.setOnItemClickListener { _, view, _, _ ->
            binding.etPlaceBirth.requestFocus()
            Toast.makeText(this, (view as AppCompatCheckedTextView).text.toString(), Toast.LENGTH_SHORT).show()
        }
        
        binding.etDateBirth.setOnClickListener {
            val builder = MaterialDatePicker.Builder.datePicker()
            val picker = builder.build()
            
            picker.addOnPositiveButtonClickListener { timeInMilliseconds ->
                val dateStr = SimpleDateFormat("dd/MM/yyyy",
                    Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }.format(timeInMilliseconds)
                
                binding.etDateBirth.setText(dateStr)
            }
            
            picker.show(supportFragmentManager, picker.toString())
        }
        
        binding.etSurname.setContent {
            MdcTheme {
                EtSurname{ textSurname = it.trim() }
            }
        }
        binding.etHeight.setContent {
            MdcTheme {
                EtHeight{ textHeight = it.trim()}
            }
        }
    }
    
    @Composable
    private fun EtSurname(onValueChanged: (String) -> Unit) {
        var textValue by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        Column {
            OutlinedTextField(value = textValue,
                onValueChange = {
                    textValue = it
                    isError = it.isEmpty()
                    onValueChanged(it)},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimensionResource(id = R.dimen.common_padding_default)),
                label = { Text(text = stringResource(id = R.string.hint_surname))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Words),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.ic_person), contentDescription = null)
                },
                isError = isError
            )
            Text(text = stringResource(id = R.string.help_required),
                style = MaterialTheme.typography.caption,
                color = if (isError) MaterialTheme.colors.error
                        else MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.common_padding_default),
                    top = dimensionResource(id = R.dimen.common_padding_micro)))
        }
    }
    
    @Composable
    fun EtHeight(onValueChanged: (String) -> Unit) {
        var textValue by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }
        Column {
            OutlinedTextField(value = textValue,
                onValueChange = {
                    textValue = it
                    isError = it.isEmpty() || it.toInt() < 50
                    onValueChanged(it)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(40f)
                    .padding(end = dimensionResource(id = R.dimen.common_padding_min)),
                label = { Text(text = stringResource(id = R.string.hint_height))},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.ic_height), contentDescription = null)
                },
                isError = isError)
            Text(text = if(isError) stringResource(id = R.string.help_min_height_valid)
                        else stringResource(id = R.string.help_min_height),
                style = MaterialTheme.typography.caption,
                color = if (isError) MaterialTheme.colors.error
                        else MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium),
                modifier = Modifier.padding(
                    start = dimensionResource(id = R.dimen.common_padding_default),
                    top = dimensionResource(id = R.dimen.common_padding_micro)))
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_send){
            if (validFields()) {
                val name: String = findViewById<TextInputEditText>(R.id.etName).text.toString().trim()
                //val surname = binding.etSurname.text.toString().trim()
                //val height = binding.etHeight.text.toString().trim()
                val dateBirth = binding.etDateBirth.text.toString().trim()
                val country = binding.actvCountries.text.toString().trim()
                val placeBirth = binding.etPlaceBirth.text.toString().trim()
                val notes = binding.etNotes.text.toString().trim()
                
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.dialog_title))
                builder.setMessage(joinData(name, textSurname, textHeight, dateBirth, country, placeBirth, notes))
                builder.setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                    with(binding) {
                        etName.text?.clear()
                        //etSurname.text?.clear()
                        //etHeight.text?.clear()
                        etDateBirth.text?.clear()
                        actvCountries.text?.clear()
                        etPlaceBirth.text?.clear()
                        etNotes.text?.clear()
                    }
                }
                builder.setNegativeButton(getString(R.string.dialog_cancel), null)
                
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
        
        return super.onOptionsItemSelected(item)
    }
    
    private fun joinData(vararg fields: String): String {
        var result = ""
        
        fields.forEach { field ->
            if (field.isNotEmpty()) {
                result += "$field\n"
            }
        }
        
        return result
    }
    
    private fun validFields(): Boolean{
        var isValid = true
        
        /*if (binding.etHeight.text.isNullOrEmpty()){
            binding.tilHeight.run {
                error = getString(R.string.help_required)
                requestFocus()
            }
            isValid = false
        } else if (binding.etHeight.text.toString().toInt() < 50) {
            binding.tilHeight.run {
                error = getString(R.string.help_min_height_valid)
                requestFocus()
            }
            isValid = false
        } else {
            binding.tilHeight.error = null
        }*/
        
        /*if (binding.etSurname.text.isNullOrEmpty()){
            binding.tilSurname.run {
                error = getString(R.string.help_required)
                requestFocus()
            }
            isValid = false
        } else {
            binding.tilSurname.error = null
        }*/
        
        if (binding.etName.text.isNullOrEmpty()){
            binding.tilName.run {
                error = getString(R.string.help_required)
                requestFocus()
            }
            isValid = false
        } else {
            binding.tilName.error = null
        }
        
        return isValid
    }
}