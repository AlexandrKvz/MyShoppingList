package com.xotkins.myshoppinglist.fragments

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.billing.BillingManager

//Создаём класс SettingsFragment наследуемый от PreferenceFragmentCompat() -- этот класс отвечает за разметку
class SettingsFragment : PreferenceFragmentCompat(){
    private lateinit var removeAdsPref: Preference //создаём переменную, чтобы найти наж элемент
    private lateinit var bManager: BillingManager //создаём переменную, чтобы запускать BillingManager
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey) //выбираем из какого ресурса будет запущен экран настроек
        init()//запускаем функцию
    }

    private fun init(){//создаём функцию, где будем инициализировать слушатель нажатий
        bManager = BillingManager(activity as AppCompatActivity) //здесь мы инициализируем bManager и явно указываем (activity as AppCompatActivity)
        removeAdsPref = findPreference("remove_ads_key")!! //находим наш элемент по ключу ключ
        //здесь мы присваиваем слушатель нажатий
        removeAdsPref.setOnPreferenceClickListener {
            bManager.startConnection() // здесь запускается диалог для оплаты и т.п.
            true
        }
    }

    override fun onDestroy() { //здесь просто закрывается соединения
        bManager.closeConnection()
        super.onDestroy()
    }
}
