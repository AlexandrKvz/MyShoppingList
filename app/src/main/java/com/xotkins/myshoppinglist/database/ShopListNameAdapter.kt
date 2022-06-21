package com.xotkins.myshoppinglist.database

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.databinding.ListNameItemBinding
import com.xotkins.myshoppinglist.entities.ShopListNameItem

//в конструкторе ShopListNameFragment передаем интерфейс Listener и запускаем через слушатель нажатия
class ShopListNameAdapter(private var listener: Listener): ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder>(ItemComparator()) { //создается класс NoteAdapter и наследуется от ListAdapter, в этом лист адаптере будет NoteItem и будет содержать разметку

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder { //здесь создаётся разметак ViewHolder
        return ItemHolder.create(parent) //здесь возвращается ItemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) { //здесь заполняется разметка ViewHolder
        holder.setData(getItem(position), listener)
    }

    class ItemHolder(view: View): RecyclerView.ViewHolder(view){ //Создается новый класс ItemHolder, в нём передаётся разметка RecyclerView
        private val binding = ListNameItemBinding.bind(view)

//создаётся функция setData, в неё поочереди передаётся shopListNameItem, так же добавляем интерфейс Listener
        fun setData(shopListNameItem: ShopListNameItem, listener: Listener) = with(binding){
            tvListName.text = shopListNameItem.name //записывается заголовок
            tvTime.text = shopListNameItem.time // записывается время создания
            progressBar.max = shopListNameItem.allItemCounter //здесь мы указываем максимальный размер progressBar - это кол-во элементов в списке
            progressBar.progress = shopListNameItem.checkedItemsCounter //здесь мы указываем продвижения  progressBar - это кол-во выбранных элементов в списке
            val colorState = ColorStateList.valueOf(getProgressColorState(shopListNameItem, binding.root.context))//создаём переменную colorState и передаём в неё функцию getProgressColorState(передаём item, context)
            progressBar.progressTintList = colorState //код который меняет в итоге цвет полоски
            counterCard.backgroundTintList = colorState //код который в итоге меняет цвет счётчика
            val counterText = "${shopListNameItem.checkedItemsCounter} / ${shopListNameItem.allItemCounter}" //создаём отдельную переменную, где мы составляем текст из отдельных частей
            tvCounter.text = counterText //тут мы показываем кол-во элементов в списке
            itemView.setOnClickListener {
                listener.onCLickItem(shopListNameItem)
            }
            imDelete.setOnClickListener{
                listener.deleteItem(shopListNameItem.id!!) //удаляем данный список покупок
            }
            imEdit.setOnClickListener{
                listener.editItem(shopListNameItem) //изменяем  данный список
              }
        }

        private fun getProgressColorState(item: ShopListNameItem, context: Context): Int{ //функция для изменения текста ProgressBar
            return if(item.checkedItemsCounter == item.allItemCounter){ //возвращает если кол-во выбранных элементов равно кол-ву всех элементов то--
                ContextCompat.getColor(context, R.color.green_main) //цвет меняется на зелёный
            }else{ //в противном случае
                ContextCompat.getColor(context, R.color.red_main) //цвет меняется на красный
            }
        }

        companion object{ //создаётся статическая функция
            fun create(parent: ViewGroup): ItemHolder{ //создаётся статическая функция, здесь надувается разметка ShopListNameItem
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_name_item, parent, false)) //возвращается разметка для дальнейшего заполнения
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<ShopListNameItem>() {
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {//функция сравненивает элементы, если они похожи
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean { //функция сравнивает весь контент
            return oldItem == newItem
        }

    }
    interface Listener{ //создаём интерфейс для
        fun deleteItem(id: Int) // функция удаления заметки(Этот интерфейс мы прикрепляем к нашему)
        fun editItem(shopListNameItem: ShopListNameItem)
        fun onCLickItem(shopListNameItem: ShopListNameItem) //функция выбирает данный список покупок
    }

}