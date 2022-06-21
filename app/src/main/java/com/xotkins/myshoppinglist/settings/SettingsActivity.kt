package com.xotkins.myshoppinglist.settings

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceManager
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.fragments.SettingsFragment

class SettingsActivity : AppCompatActivity() {
    private lateinit var defPref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        setContentView(R.layout.activity_settings)


        if (savedInstanceState == null) { //проверям savedInstanceState  равно null, то заного запускаем наш фрагмент
            supportFragmentManager.beginTransaction().replace(R.id.placeHolder, SettingsFragment())
                .commit() //заменяем placeHolder на наш фрагмент SettingsFragment()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //показываем стрелку вернуться назад
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //функция для того элемента который мы выбрали, здесь мы делаем возврат на предыдущую страницу нажав кнопку стрелка

        if (item.itemId == android.R.id.home) finish() // если мы нажали на кнопку СТРЕЛКА мы делаем возврат на предыдущую страницу
        return super.onOptionsItemSelected(item)
    }
    private fun getSelectedTheme(): Int{// функция для запуска темы из памяти настроек
        return if(defPref.getString("theme_key", "green") == "green"){ //если в памяти стоит green
            R.style.Theme_MyShoppingListGreen // то остаётся green
        }else{ //противном случае
            R.style.Theme_MyShoppingListBlue //ставится blue
        }
    }
}
