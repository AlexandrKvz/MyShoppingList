package com.xotkins.myshoppinglist.database

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.databinding.ShopLibraryListItemBinding
import com.xotkins.myshoppinglist.databinding.ShopListItemBinding
import com.xotkins.myshoppinglist.entities.ShopListItem

//в конструкторе ShopListNameFragment передаем интерфейс Listener и запускаем через слушатель нажатия
class ShopListItemAdapter(private var listener: Listener): ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) { //создается класс NoteAdapter и наследуется от ListAdapter, в этом лист адаптере будет NoteItem и будет содержать разметку

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder { //здесь создаётся разметак ViewHolder
        return if(viewType == 0) ItemHolder.createSHopItem(parent) //здесь возвращается ItemHolder для записи в базу данных
        else ItemHolder.createLibraryItem(parent) //здесь возвращает ItemHolder для библиотеки (для подсказки)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) { //здесь заполняется разметка ViewHolder
        if(getItem(position).itemType == 0){ //ViewHolder, если равен 0, то
            holder.setItemData(getItem(position), listener) //заполняем элемент в базу данных
        } else { //в противном случае
            holder.setLibraryData(getItem(position), listener) //заполняем элемент в библиотеку(для всплывающей подсказки)
        }
    }

    override fun getItemViewType(position: Int): Int { //функция для получения числа к элементу
        return getItem(position).itemType
    }

    class ItemHolder(val view: View): RecyclerView.ViewHolder(view){ //Создается новый класс ItemHolder, в нём передаётся разметка RecyclerView

    //создаётся функция setItemData, в неё поочереди передаётся ShopListItem, так же добавляем интерфейс Listener
        fun setItemData(shopListItem: ShopListItem, listener: Listener){
        val binding = ShopListItemBinding.bind(view) //находим разметку для добавления в базу данных
        binding.apply {
            tvName.text = shopListItem.name //Здесь мы добавляем нашему элементу из списка именование
            tvInfo.text = shopListItem.itemInfo // здесь мы заполняем информацию об эелементе из списка покупок
            tvInfo.visibility = infoVisibility(shopListItem) //здесь выполняется проверка и делает видимым или нет
            checkBox.isChecked = shopListItem.itemChecked //здесь будет показывать состояние checkBox
            setPaintFlagAndColor(binding) //тут берутся элементы по индефикаторам
            //создаётся слушатель нажатия checkBox
            checkBox.setOnClickListener{
                listener.onCLickItem(shopListItem.copy(itemChecked = checkBox.isChecked), CHECK_BOX) //здесь сохраняется состояние checkBox выбраным и записывается в базу данных, если нажали на CheckBox
            }
                //создаём слушатель нажатия кнопки редактрирование элемента в списке
            imageEdit.setOnClickListener {
                listener.onCLickItem(shopListItem, EDIT)// если нажали на Edit
            }
            imDeleted.setOnClickListener{
                listener.onCLickItem(shopListItem, DELETE) // здесь добавляем слушатель нажатия (удаления элемента, передаём id элемента)
            }
        }
        }
    //создаётся функция setLibraryData, в неё поочереди передаётся ShopListItem, так же добавляем интерфейс Listener
        fun setLibraryData(shopListItem: ShopListItem, listener: Listener){
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name //выводим название - всплывающей подсказки из библиотеки
                //создаём слушатель нажатия кнопки редактрирование элемента в списке
                imageEdit.setOnClickListener {
                    listener.onCLickItem(shopListItem, EDIT_LIBRARY_ITEM)// если нажали на Edit
                }
                imageDelete.setOnClickListener {
                    listener.onCLickItem(shopListItem, DELETE_LIBRARY_ITEM)//если нажали удалить
                }
                itemView.setOnClickListener {
                    listener.onCLickItem(shopListItem, ADD)//если нажали всплывающую подсказку
                }
            }
        }

        private fun setPaintFlagAndColor(binding: ShopListItemBinding){ //функция для отметки, что элемент из списка куплен, меняет цвет и перечёркивает, функция запускается в fun setItemData
           binding.apply {
               if(checkBox.isChecked){ //если чекбокс нажат
                   tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG //то элемент из списка перечеркивается
                   tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG //то описание элемента из списка перечеркивается
                   tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray_light)) //то элемент из списка меняет цвет на серый
                   tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray_light)) //то описание элемента из списка меняет цвет на серый
               } else { //впротивном случае
                   tvName.paintFlags = Paint.ANTI_ALIAS_FLAG //то элемент из списка снова без перечеркивания
                   tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG //то описание элемента из списка снова без перечеркивания
                   tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black)) //то элемент из списка меняет цвет на чёрный
                   tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black)) //то описание элемента из списка меняет цвет на чёрный
               }
           }
        }

         private fun infoVisibility(shopListItem: ShopListItem): Int{ //создаём функцию для проверки, есть ли информация об элементе и запускаем её в функции SetItemData
           return if(shopListItem.itemInfo.isNullOrEmpty()){ //если в информации об элементе из списка покупок отсутствует
               View.GONE // то описание скрыто
           } else {//в противном случае, если в информации об элементе из списка покупок есть
               View.VISIBLE //то показывает эту информацию
           }
        }

        companion object{ //создаётся статическая функция
            fun createSHopItem(parent: ViewGroup): ItemHolder{ //создаётся статическая функция, здесь надувается разметка ShoppingListItem
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.shop_list_item, parent, false)) //возвращается разметка для дальнейшего заполнения
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder{ //создаётся статическая функция, здесь надувается разметка ShoppingListItem
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.shop_library_list_item, parent, false)) //возвращается разметка для дальнейшего заполнения
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<ShopListItem>() {
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {//функция сравненивает элементы, если они похожи
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean { //функция сравнивает весь контент
            return oldItem == newItem
        }


    }
    interface Listener{ //создаём интерфейс для ShopListItem

        fun onCLickItem(shopListItem: ShopListItem, state: Int) //функция выбирает данный элемент из списка покупок
        fun deleteItem(id: Int)
    }

    companion object{
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4
        const val DELETE = 5



    }

}