package com.xotkins.myshoppinglist.activities

import android.app.Application
import com.xotkins.myshoppinglist.database.MainDataBase

class MainApp: Application() { //Создание класса MainApp наследуется от главного класса Application, т.е. от всего приложения
    val database by lazy { MainDataBase.getDataBase(this) } // Когда database = null, этот блок запуститься 1 раз, выдаст инстанцию нашей базы данных (используется для этого by lazy)
    // Когда запускается приложение, запускается наш класс Application() и следующим запускается код --- val database by lazy { MainDataBase.getDataBase(this)
}