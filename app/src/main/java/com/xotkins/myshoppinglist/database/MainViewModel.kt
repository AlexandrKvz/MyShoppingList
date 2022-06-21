package com.xotkins.myshoppinglist.database

import androidx.lifecycle.*
import com.xotkins.myshoppinglist.entities.LibraryItem
import com.xotkins.myshoppinglist.entities.NoteItem
import com.xotkins.myshoppinglist.entities.ShopListItem
import com.xotkins.myshoppinglist.entities.ShopListNameItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
//Это всё архитектура MVVM
class MainViewModel(dataBase: MainDataBase): ViewModel() { //создается MVVM класс, в этот класс передается база данных(DataBase)
    val dao = dataBase.getDao()
    val libraryItems = MutableLiveData<List<LibraryItem>>()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData() //переменная для всех наших заметок, в которой достается один раз данные
    val allShopListNamesItem: LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData() //переменная для всех наших названий списков, в которой достается один раз данные

    fun getAllItemsFromList(listId: Int): LiveData<List<ShopListItem>>{ //запуская эту функцию мы подключаемся к таблице данных ShopListItem и получаем отсортированные данные по индетификатору listId
        return dao.getAllShopListItems(listId).asLiveData()
    }

    //создаётся функция для записи заметок на второстепенном потоке
    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }
    //создаётся функция с помощью которой будем передавать буквы в библиотеку(Library)
    fun getAllLibraryItems(name: String) = viewModelScope.launch {
        libraryItems.postValue(dao.getAllLibraryItems(name)) //здесь мы передаём данные с помощью observer, которые получили из базы данных
    }

    //создаётся функция для записи элемента из списка покупок и подсказку на второстепенном потоке
    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertShopItem(shopListItem)
        if(!isLibraryItemExists(shopListItem.name)) dao.insertLibraryItem(LibraryItem(null, shopListItem.name))//если такое названия нет то добавляем подсказку, если оно уже есть, ничего не делаем
    }

    //создаётся функция для записи наименования списка покупок на второстепенном потоке
    fun insertShopListName(listNameItem: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(listNameItem)
    }

    //создаётся функция для изменения записи элемента из списка покупок на второстепенном потоке
    fun updateListItem(item: ShopListItem) = viewModelScope.launch {
        dao.updateListItem(item)
    }

    //создаётся функция для изменения записи заметок на второстепенном потоке
    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }

    //создаётся функция для изменения всплывающей подсказки на второстепенном потоке
    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(item)
    }

    //создаётся функция для изменения имени списка на второстепенном потоке
    fun updateListName(shopListNameItem: ShopListNameItem) = viewModelScope.launch {
        dao.updateShopListName(shopListNameItem)
    }

    //создаётся функция для удаления заметок на второстепенном потоке
    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }

    //создаётся функция для удаления всплывающей подсказки на второстепенном потоке
    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }

    fun deleteItemByListId(id: Int ) = viewModelScope.launch{
        dao.deleteItemByListId(id)
    }

    //создаётся функция для удаления названия списка на второстепенном потоке
    fun deleteShopList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if(deleteList)dao.deleteShopListName(id) //если придёт тру, то удаления списка
        dao.deleteShopItemsByListId(id) //удаления элементов из списка

    }
    //создаётся функция для добавления подсказки на второстепенном потоке
    private suspend fun isLibraryItemExists(name: String): Boolean{
        return dao.getAllLibraryItems(name).isNotEmpty() //возвращает данные для подсказки, если не пусто
    }

    class MainViewModelFactory(val dataBase: MainDataBase) : ViewModelProvider.Factory{ //Создается класс для инициализации класса MainViewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)){
                @Suppress("UNCHECKED_CAST") //анотация
                return MainViewModel(dataBase) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }

    }
}