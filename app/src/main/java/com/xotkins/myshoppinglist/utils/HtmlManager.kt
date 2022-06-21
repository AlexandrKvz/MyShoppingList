package com.xotkins.myshoppinglist.utils

import android.text.Html
import android.text.Spanned
// создаём класс object HtmlManager, для изменения текста по стилю
object HtmlManager {
    fun getFromHtml(text: String): Spanned{ //создаём функцию класса Spanned, где берем выделенный тексти передаем его(превращается в класс Spanned) и с помощью класса Spanned изменяем текст
       return if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N){ //проверка условия для старых версий андроида
           Html.fromHtml(text) //выполняем
       }else{ //в противмно случае
           Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT) //выполняем для новых версий андроида
       }
    }

    fun toHtml(text: Spanned): String{  //функция наоборот,  берётся класс Spanned и выдаёт стринг
        return if(android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.N){ //проверка условия для старых версий андроида
            Html.toHtml(text) //выполняем
        }else{ //в противмно случае
            Html.toHtml(text, Html.FROM_HTML_MODE_COMPACT) //выполняем для новых версий андроида
        }
    }
}
//HtmlManager добавляем туда, где есть создание и изменения текста, т.е. в fun fillNote(), в fun createNewNote(), в fun updateNote(), а так же в адаптере в fun setData