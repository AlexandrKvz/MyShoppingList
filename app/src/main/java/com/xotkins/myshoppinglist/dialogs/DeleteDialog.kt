package com.xotkins.myshoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.xotkins.myshoppinglist.databinding.DeleteDialogBinding
import com.xotkins.myshoppinglist.databinding.NewListDialogBinding

object DeleteDialog {   //создаём класс DeleteDialog
    fun showDialog(context: Context, listener: Listener) { //создаём функцию showDialog, передаёт сюда context
        var dialog: AlertDialog? = null //создаём переменную dialog от класса AlertDialog, который может быть null
        val builder = AlertDialog.Builder(context) // создаём переменную builder и инициализируем его
        val binding = DeleteDialogBinding.inflate(LayoutInflater.from(context)) // здесь мы делаем разметку, где все наши элементы
        builder.setView(binding.root)


        //здесь присваиваем слушатель нажатий
        binding.apply {
           //создаём слушатель нажатий для удаления списка покупок
            bDelete.setOnClickListener {
                    listener.onClick() //то запускаем наш интерфейс и передаём название, чтобы удалить
                    dialog?.dismiss() //то просто закрываем диалог
            }
            bCancel.setOnClickListener {
                dialog?.dismiss() //то просто закрываем диалог
            }

        }
        dialog = builder.create() //создаётся диалог(экран)
        dialog.window?.setBackgroundDrawable(null) //убираем стандартный фон вокруг нашего диалога
        dialog.show() //здесь мы показываем диалог
    }

    interface Listener{ //Создаём интерфейс Listener
        fun onClick() //функция
    }
}