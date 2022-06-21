package com.xotkins.myshoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "note_list") //создаётся таблица NoteItem, а так же модель для заполнения библиотеки Room persistance library (таблица для хранения заметок блокнота)
data class NoteItem( //Создание данных класса NoteItem

    @PrimaryKey (autoGenerate = true) //генерирует уникальный индефикатор для каждого заметки в списке(генерирует автоматически)
    val id: Int?, // начинать нужно Int? (null) чтобы работало автоматически

    @ColumnInfo(name = "title") //первая колонка, наименование заголовка заметки для блокнота
    val title: String, // принимает тип даннных строка

    @ColumnInfo(name = "content") //вторая колонка, описание заметки
    val content: String, // принимает тип даннных строка

    @ColumnInfo(name = "time") //третья колонака, время создания заметки
    val time: String, // принимает тип даннных строка

    @ColumnInfo(name = "category") //четвертая колонка, для фильтрации категории заметок
    val category: String, // принимает тип даннных строка
): Serializable // передача всего класса NoteItem с помощью Serializable (а не поотдельности каждую переменную)
