package com.xotkins.myshoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "shopping_list_names")   //создаётся таблица ShoppingListNames, а так же модель для заполнения библиотеки Room persistance library (список покупок)
data class ShopListNameItem( //Создание данных класса ShoppingListNames

    @PrimaryKey (autoGenerate = true)  //   генерирует уникальный индефикатор для каждого списка (генерирует автоматически)
    val id: Int?,   // начинать нужно Int? (null) чтобы работало автоматически

    @ColumnInfo (name = "name")     // первая колонка для записи данных, именнование списка
    val name: String,   // принимает тип даннных строка

    @ColumnInfo (name = "time")     // вторая колонка для записи времени, когда был создан список
    val time: String,   // принимает тип даннных строка

    @ColumnInfo (name = "allItemCounter")   //третья колонка, общее количество элементов в данном списке
    val allItemCounter: Int,    // принимает тип даннных целое число

    @ColumnInfo (name = "checkedItemsCounter")  //четвертая колонка, сколько элементов уже купленно в данном списке
    val checkedItemsCounter: Int,   // принимает тип даннных целое число

    @ColumnInfo (name = "itemsIds")     // пятая колонка, индефикаторы всех элементов в данном списке
    val itemsIds: String,   // принимает тип даннных строка

):Serializable  // передача всего класса SHoppingListNames с помощью Serializable (а не поотдельности каждую переменную)
