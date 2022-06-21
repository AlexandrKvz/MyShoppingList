package com.xotkins.myshoppinglist.utils

import android.view.MotionEvent
import android.view.View

class MyTouchListener: View.OnTouchListener { //создаём класс MyTouchListener, который наследуется от View.OnTouchListener
    var xDelta = 0.0f //создаём переменную координат
    var yDelta = 0.0f //создаём переменную координат

    override fun onTouch(v: View, event: MotionEvent?): Boolean { //функция действия элемента(его движение, отпускание и т.п.)
        when(event?.action){ //проверяем, какое действие происходит
            MotionEvent.ACTION_DOWN ->{ //действие когда мы отпустили элемент
                xDelta = v.x - event.rawX //координаты куда мы двигаем, т.е. берём начальную позицию элемента минус позиция куда передвинули, и записываем новое положение
                yDelta = v.y - event.rawY //координаты куда мы двигаем, т.е. берём начальную позицию элемента минус позиция куда передвинули, и записываем новое положение
            }
            MotionEvent.ACTION_MOVE ->{ //действие когда мы двигаем элемент
                v.x = xDelta + event.rawX //координаты куда мы двигаем, т.е. берём начальную позицию элемента плюс позиция куда передвинули, и записываем новое положение
                v.y = yDelta + event.rawY //координаты куда мы двигаем, т.е. берём начальную позицию элемента плюс позиция куда передвинули, и записываем новое положение
            }
        }
        return true
    }

}