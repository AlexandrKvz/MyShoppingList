package com.xotkins.myshoppinglist.fragments

import androidx.fragment.app.Fragment

 abstract class BaseFragment : Fragment() { //создание абстрактного класса BaseFragment, который наследуется от Fragment
     abstract fun onClickNew() //абстрактная функция, которая будет выполняться для каждого фрагмента по своему
}