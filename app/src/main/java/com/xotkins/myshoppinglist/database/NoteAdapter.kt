package com.xotkins.myshoppinglist.database

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowId
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.databinding.NoteListItemBinding
import com.xotkins.myshoppinglist.entities.NoteItem
import com.xotkins.myshoppinglist.utils.HtmlManager
import com.xotkins.myshoppinglist.utils.TimeManager

//в конструкторе NoteAdapter передаем интерфейс Listener и запускаем через слушатель нажатия
class NoteAdapter(private val listener: Listener, private val defPref: SharedPreferences): ListAdapter<NoteItem, NoteAdapter.ItemHolder>(ItemComparator()) { //создается класс NoteAdapter и наследуется от ListAdapter, в этом лист адаптере будет NoteItem и будет содержать разметку

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder { //здесь создаётся разметак ViewHolder
        return ItemHolder.create(parent) //здесь возвращается ItemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) { //здесь заполняется разметка ViewHolder
        holder.setData(getItem(position), listener, defPref)
    }

    class ItemHolder(view: View): RecyclerView.ViewHolder(view){ //Создается новый класс ItemHolder, в нём передаётся разметка RecyclerView
        private val binding = NoteListItemBinding.bind(view)

//создаётся функция setData, в неё поочереди передаётся NoteItem, так же добавляем интерфейс Listener
        fun setData(note: NoteItem, listener: Listener, defPref: SharedPreferences) = with(binding){
            tvTitle.text = note.title //записывается заголовок
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()  // записывается описание
            tvTime.text = TimeManager.getTimeFormat(note.time, defPref) // записывается время создания
            itemView.setOnClickListener {
                listener.onCLickItem(note) // здесь добавляем слушатель нажатия (выбрать всю заметку, и передать её)
            }
            imDelete.setOnClickListener{
                listener.deleteItem(note.id!!) // здесь добавляем слушатель нажатия (удаления заметки, передаём id заметки)
            }
        }
        companion object{ //создаётся статическая функция
            fun create(parent: ViewGroup): ItemHolder{ //создаётся статическая функция, здесь надувается разметка NoteListItem
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.note_list_item, parent, false)) //возвращается разметка для дальнейшего заполнения
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<NoteItem>() {
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {//функция сравненивает элементы, если они похожи
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean { //функция сравнивает весь контент
            return oldItem == newItem
        }

    }
    interface Listener{ //создаём интерфейс для NoteAdapter
        fun deleteItem(id: Int) // функция удаления заметки(Этот интерфейс мы прикрепляем к нашему NoteFragment)
        fun onCLickItem(note: NoteItem) //функция выбрать весь элемент(всю заметку)
    }

}