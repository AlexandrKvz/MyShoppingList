package com.xotkins.myshoppinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import com.xotkins.myshoppinglist.R

object FragmentManager { //Создание object FragmentManager
    var currentFrag: BaseFragment? = null // создается переменная currentFrag, которая наследуется от BaseFragment, т.е. сюда добавляется новый данный фрагмент, который подключен в активити, а старый удаляется

    fun setFragment(newFrag: BaseFragment, activity: AppCompatActivity) { //функция с помощью которой переключаемся между фрагментами
        val transaction = activity.supportFragmentManager.beginTransaction() // создаем переменную транзакция, с помощью которой можем управлять, менять, удалять фрагменты
        transaction.replace(R.id.placeHolder, newFrag) //Здесь указывается место куда хотим поместить наш новый фрагмент
        transaction.commit() // применения всех действий, которые мы указали
        currentFrag = newFrag //здесь мы указываем, что новый фрагмент это и есть наш currentFrag, т.е. currentFrag больше не null
    }
}