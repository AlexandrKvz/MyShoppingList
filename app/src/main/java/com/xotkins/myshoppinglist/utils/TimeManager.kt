package com.xotkins.myshoppinglist.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
    private const val DEF_TIME_FORMAT = "hh:mm:ss - dd/MM/yyyy"
     fun getCurrentTime(): String { //функция которая берёт настоящее время
        val formatter = SimpleDateFormat(
            DEF_TIME_FORMAT,
            Locale.getDefault()
        ) //получаем в каком формате получаем время
        return formatter.format(Calendar.getInstance().time) //возвращает уже формат времени
    }
    //создаём функцию getTimeFormat в которой будет сохранено время по умолчанию, передаём сюда с то что достаём из памяти то, что мы сохранили с экрана настроек изменения и это всё нам возвращает время в новом формате
    fun getTimeFormat(time: String, defPreferences: SharedPreferences): String{
        val defFormatter = SimpleDateFormat(DEF_TIME_FORMAT, Locale.getDefault()) //здесь мы создаём переменную defFormatter, которая разбивает наше время и дату типа String на отдельные состовляющие согласно формуле "hh:mm:ss - yy/MM/dd"
        val defDate = defFormatter.parse(time) // defFormatter передаёт все разбитые данные формата времени в переменную defDate
        val newFormat = defPreferences.getString("time_format_key", DEF_TIME_FORMAT)// новый формат, который пользователь выбирает типа String
        val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault()) //новая переменная которая уже хранит готовый новый выбранный формат времени
        return if(defDate != null){ //если defDate не null
            newFormatter.format(defDate) //то форматируем время
        }else{ //если null
            time //то просто возвращаем время
        }
    }
}