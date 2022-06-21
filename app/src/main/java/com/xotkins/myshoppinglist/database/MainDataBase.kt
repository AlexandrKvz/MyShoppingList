package com.xotkins.myshoppinglist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.xotkins.myshoppinglist.entities.LibraryItem
import com.xotkins.myshoppinglist.entities.NoteItem
import com.xotkins.myshoppinglist.entities.ShopListItem
import com.xotkins.myshoppinglist.entities.ShopListNameItem
//Это стандартный шаблон создания и доступ к таблице базе данных
@Database (entities = [LibraryItem::class, NoteItem::class, ShopListItem::class, ShopListNameItem::class ], version = 1) //База данных в которую передаётся массив с нашими таблицами и в конце указывается версия для миграции данных с одной таблице в другую
 abstract class MainDataBase: RoomDatabase () { // Создание асбтрактного класса MainDataBase наследуется от библиотеки Room persistance library  (создание основного класса базы данных)
    abstract fun getDao(): Dao // асбстрактная функция, которая возвращает интерфейс Dao

     companion object{ // companion object даёт возможность использоваться функцию без инциализации класса MainDataBase
         @Volatile //анотация
         private var INSTANCE: MainDataBase? = null //создание переменной инстанс, чтобы получить инстанцию класса MainDataBase, чтобы была доступна на всех потоках

         fun getDataBase(context: Context): MainDataBase{ //создаётся функция, в неё передаётся контекст и возвращает MainDataBase
             return INSTANCE ?: synchronized(this){ //возвращает инстанцию, оператор(?:) означает, что если переменная INSTANCE = null, то начинает выполняться блок кода после оператора ?:, если не null то берётся имеющиеся инстанция
                 val instance = Room.databaseBuilder(context.applicationContext, MainDataBase::class.java, "shopping_list.db").build() //блок кода, который создаёт файл базы данных и даёт ссылку, чтобы дальше её считытвать
                 instance   //
             }
         }
     }
}