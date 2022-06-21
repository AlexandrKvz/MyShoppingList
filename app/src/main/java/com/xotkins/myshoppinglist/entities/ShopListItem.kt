package com.xotkins.myshoppinglist.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity (tableName = "shop_list_item")  //создаётся таблица ShoppingListItem, а так же модель для заполнения библиотеки Room persistance library (элементы из списка покупок)
data class ShopListItem( //Создание данных класса ShoppingListItem

    @PrimaryKey (autoGenerate = true)  //генерирует уникальный индефикатор для каждого элемента в списке(генерирует автоматически)
    val id: Int?, // начинать нужно Int? (null) чтобы работало автоматически

    @ColumnInfo (name = "name") // первая колонка, именование элемента из списка покупок
    val name: String, // принимает тип даннных строка

    @ColumnInfo (name = "itemInfo") //вторая колонка, дополнительная информация об элементе из списка покупок
    val itemInfo: String = "", // принимает тип даннных строка, может быть null(?)

    @ColumnInfo (name = "itemChecked") //третья колонка, в эту колонку записывается информация куплен элемент из списка покупок или нет
    val itemChecked: Boolean = false, // принимает тип даннных Boolean (начинаться должен с нуля, потому что он ещё пуст)

    @ColumnInfo (name = "listId") //четвертая колонка, колонка для сохранения индетификатора данного списка покупок, необходимо для того чтобы ссылаться на конкретный список для изменения в нём элементов
    val listId: Int, // принимает тип даннных целое число

    @ColumnInfo (name = "itemType") //пятая колонка, это специальная подсказка пользователю, чтобы не писать полное именование элемента из списка, если он уже заносил этот элемент в базу данных
    val itemType: Int = 0  // принимает тип даннных целое число

) // здесь не передаётся весь класс
