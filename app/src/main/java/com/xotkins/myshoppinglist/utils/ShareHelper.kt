package com.xotkins.myshoppinglist.utils

import android.content.Intent
import com.xotkins.myshoppinglist.entities.ShopListItem
import java.lang.StringBuilder

//создём класс для передачи нашего списка по соц.сетям
object ShareHelper {
    fun shareShopList(shopList : List<ShopListItem>, listName: String): Intent { //функция будет передавать Список покупок и его название
        val intent = Intent(Intent.ACTION_SEND) //создаём переменную, где указываем константу, что мы хотим что то отправить
        intent.type = "text/pane" //тип данных которые мы хотим отправить
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList, listName)) //сюда мы помещаем наши данные текст из makeShareText(название списка и его элементы)
        }
        return intent //возваращаем наши данные
    }

    private fun makeShareText(shopList : List<ShopListItem>, listName: String): String{ //функция с помощью который мы собираем текст, название и сами элементы, возвращает готовый текст
        val stringBuilder = StringBuilder()//создаём переменную класса StringBuilder(), переменная будет собирать
        stringBuilder.append("<<$listName>>") //заполняет название списка
        stringBuilder.append("\n") //переводит на следующую строку
        var counter = 0 //счётчик для элементов
       //с помощью этого цикла пробегает все элементы и заполняет наш список
        shopList.forEach {
            stringBuilder.append("${++counter} - ${it.name} (${it.itemInfo})") //заполнение число название элемента из списка и доп.информация(описание элемента из списка)
            stringBuilder.append("\n")
        }
        return stringBuilder.toString() //возвращает наш текст
    }
}