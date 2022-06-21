package com.xotkins.myshoppinglist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xotkins.myshoppinglist.entities.LibraryItem
import com.xotkins.myshoppinglist.entities.NoteItem
import com.xotkins.myshoppinglist.entities.ShopListItem
import com.xotkins.myshoppinglist.entities.ShopListNameItem
import kotlinx.coroutines.flow.Flow


@Dao //интерфейс Dаo (Data access object)
interface Dao { // это интерфейс
    @Query ("SELECT * FROM note_list") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(Взять все из заметки))
    fun getAllNotes(): Flow<List<NoteItem>> //функция получает все заметки без фильтрации, с помощью Flow(специальный класс крутины, который автоматически подключает базу данных и обновляет базу, со списком NoteItem)

    @Query ("SELECT * FROM shopping_list_names") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(Взять все из наименования списков))
    fun getAllShopListNames(): Flow<List<ShopListNameItem>> //функция получает все наименование списков без фильтрации, с помощью Flow(специальный класс крутины, который автоматически подключает базу данных и обновляет базу, со списком ShoppingListName)

    @Query ("SELECT * FROM shop_list_item WHERE listId LIKE :listId") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(ВЫБРАТЬ ВСЕ ЭЛЕМЕНТЫ из shop_list_item ГДЕ listId такой же, как мы передали))
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>> //функция получает все наименование элементов из списков без фильтрации, с помощью Flow(специальный класс крутины, который автоматически подключает базу данных и обновляет базу, со списком ShopListItems)

    @Query ("SELECT * FROM library WHERE name LIKE :name") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(ВЫБРАТЬ ВСЕ ЭЛЕМЕНТЫ из library ГДЕ name такой же, как мы передали name))
    suspend fun getAllLibraryItems(name: String): List<LibraryItem> //функция получает все наименование элементов из списков без фильтрации, с помощью Flow(специальный класс крутины, который автоматически подключает библиотеку и обновляет библиотеку, со списком ShopListItems)

    @Insert //анотация для записи заметки
    suspend fun insertNote(note: NoteItem) //функция для записи заметки внутри крутины, функция передаёт @Entity NoteItem

    @Insert //анотация для записи всплывающей подсказки
    suspend fun insertLibraryItem(libraryItem: LibraryItem) //функция для записи всплывающей подсказки внутри крутины, функция передаёт @Entity LibraryItem

    @Insert //анотация для записи описания элемента
    suspend fun insertShopItem(shopListItem: ShopListItem) //функция для записи описания элемента внутри крутины, функция передаёт @Entity ShopListItem

    @Query ("DELETE FROM shop_list_item WHERE listId LIKE :listId") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(УДАЛИТЬ ЭЛЕМЕНТЫ из shop_list_item ГДЕ listId такой же, как мы передали))
    suspend fun deleteShopItemsByListId(listId: Int) // функция удаления элементов из конкретного списка

    @Query ("DELETE FROM shop_list_item WHERE id LIKE :id") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(удалить из shop_list_item, где id это id))
    suspend fun deleteItemByListId(id: Int) //функция для удаления элемента из списка покупков

    @Query ("DELETE FROM note_list WHERE id IS :id") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(удалить из note_list, где id это id))
    suspend fun deleteNote(id: Int) //функция удаления и нужно именно функция suspend, где мы конкретно указываем, что удаляем

    @Insert //анотация для записи названия списка
    suspend fun insertShopListName(nameItem: ShopListNameItem) //функция для записи названия списка внутри крутины, функция передаёт @Entity ShoppingListItem

    @Query ("DELETE FROM shopping_list_names WHERE id IS :id") //антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(удалить из shop_list_name, где id это id))
    suspend fun deleteShopListName(id: Int) //функция удаления и нужно именно функция suspend, где мы конкретно указываем, что удаляем

    @Update //анотация для изменения заметки (изменить)
    suspend fun updateNote(note: NoteItem) //функция для изменения записи заметки внутри крутины, функция передаёт @Entity NoteItem

    @Update //анотация для изменения всплывающей подсказки (изменить)
    suspend fun updateLibraryItem(item: LibraryItem) //функция для изменения записи подсказки внутри крутины, функция передаёт @Entity LibraryItem

    @Query ("DELETE FROM library WHERE id IS :id")//антоация для запроса получения данных(в скобках указывается синтаксис базы данных SQlite, а именно(удалить из library, где id это id))
    suspend fun deleteLibraryItem(id: Int) //функция для удаления записи подсказки внутри крутины, функция передаёт @Entity LibraryItem

    @Update //анотация для изменения записи описания
    suspend fun updateListItem(item: ShopListItem) //функция для записи описания внутри крутины, функция передаёт @Entity ShopListItem

    @Update //анотация для изменения записи названия списка покупок
    suspend fun updateShopListName(shopListNameItem: ShopListNameItem) //функция для изменения имени списка внутри крутины, функция передаёт @Entity ShoppingListItem
}