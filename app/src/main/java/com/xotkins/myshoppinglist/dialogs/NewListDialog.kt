package com.xotkins.myshoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.databinding.NewListDialogBinding

object NewListDialog {   //создаём класс NewListDialog
    fun showDialog(context: Context, listener: Listener, name: String) { //создаём функцию showDialog, передаёт сюда context
        var dialog: AlertDialog? = null //создаём переменную dialog от класса AlertDialog, который может быть null
        val builder = AlertDialog.Builder(context) // создаём переменную builder и инициализируем его
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context)) // здесь мы делаем разметку, где все наши элементы
        builder.setView(binding.root)


        //здесь присваиваем слушатель нажатий
        binding.apply {
            editNewListName.setText(name) //этот код используется для редактирование списка покупок, чтобы в всплывающем окне показывало предыдущее название
           //создаём слушатель нажатий
            if(name.isNotEmpty()) buttomCreate.text = context.getString(R.string.update) // условие -- если имя у нас не пустое, то это будет редактирование и кнопка меняется на update
            buttomCreate.setOnClickListener {
                val listName = editNewListName.text.toString() //создаём переменную listName в которую будем передавать тип данных строку
                if (listName.isNotEmpty()) { //проверяем условие, если listName не пустой --
                    listener.onClick(listName) //то запускаем наш интерфейс и передаём название
                }  //в противном случае, если listName пустой --
                    dialog?.dismiss() //то просто закрываем диалог

            }
        }
        dialog = builder.create() //создаётся диалог(экран)
        dialog.window?.setBackgroundDrawable(null) //убираем стандартный фон вокруг нашего диалога
        dialog.show() //здесь мы показываем диалог
    }

    interface Listener{ //Создаём интерфейс Listener
        fun onClick(name: String) //функция которая возвращает name обьект типа String
    }
}