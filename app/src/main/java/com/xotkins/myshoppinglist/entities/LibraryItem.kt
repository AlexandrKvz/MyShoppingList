package com.xotkins.myshoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "library")  //создаётся таблица LibraryItem, а так же модель для заполнения библиотеки Room persistance library (библиотека для подсказок, чтобы не вводить по новой элементы для списка покупок)
data class LibraryItem( //Создание данных класса LibraryItem

    @PrimaryKey (autoGenerate = true) //генерирует уникальный индефикатор для каждого элемента в списке(генерирует автоматически)
    val id: Int?, // начинать нужно Int? (null) чтобы работало автоматически

    @ColumnInfo(name = "name") // первая колонка, именование элемента из списка покупок
    val name: String, // принимает тип даннных строка

)
