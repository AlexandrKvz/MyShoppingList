package com.xotkins.myshoppinglist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.databinding.ActivityNewNoteBinding
import com.xotkins.myshoppinglist.entities.NoteItem
import com.xotkins.myshoppinglist.fragments.NoteFragment
import com.xotkins.myshoppinglist.utils.HtmlManager
import com.xotkins.myshoppinglist.utils.MyTouchListener
import com.xotkins.myshoppinglist.utils.TimeManager
import java.util.*

class NewNoteActivity : AppCompatActivity() { //создаётся класс NewNoteActivity наследуется от AppCompatActivity
    private lateinit var binding: ActivityNewNoteBinding
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null //создаём переменную pref типа SharedPreferences
    private lateinit var defPref: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        setContentView(binding.root)
        actionBarSettings() //запуск функции
        getNote() //запуск функции
        init() //запуск функции
        setTextSize() //запуск функции
        onCLickColorPicker() //запуск функции
        actionMenuCallback() //запуск функции
    }

    @SuppressLint("ClickableViewAccessibility") //анотация
    private fun init(){ //функция движение ColorPicker
        binding.colorPicker.setOnTouchListener(MyTouchListener()) //слушатель движения ColorPicker на экране
        pref = PreferenceManager.getDefaultSharedPreferences(this) //присваиваем переменной pref настройки который выбрал пользователь
    }

    private fun getNote() { //функция для получения нашей заметки
        val sNote =
            intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY) // переменная изначально пустая, поэтому создаём заметку
        if (sNote != null) {
            note =
                sNote as NoteItem //если есть заметка, то мы зашли для редактирования данной заметки, если заметки нет то для создания
            fillNote()//запускаем функцию для редактирования
        }
    }
//функция слушателя нажатий ColorPicker для каждого цвета
    private fun onCLickColorPicker() = with(binding){
        imRed.setOnClickListener {
            setColorForSelectedText(R.color.picker_red) //применить цвет для изменения изменения текста
        }
        imBlack.setOnClickListener {
            setColorForSelectedText(R.color.picker_black) //применить цвет для изменения изменения текста
        }
        imBlue.setOnClickListener {
            setColorForSelectedText(R.color.picker_blue) //применить цвет для изменения изменения текста
        }
        imGreen.setOnClickListener {
            setColorForSelectedText(R.color.picker_green) //применить цвет для изменения изменения текста
        }
        imYellow.setOnClickListener {
            setColorForSelectedText(R.color.picker_yellow) //применить цвет для изменения изменения текста
        }
        imOrange.setOnClickListener {
            setColorForSelectedText(R.color.picker_orange) //применить цвет для изменения изменения текста
        }

    }

    //создаём функцию для редактирования заметки
    private fun fillNote() = with(binding) {
        edTitle.setText(note?.title) // заполняем
        edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())// заполняем

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { //создаётся функция для поключения меню
        menuInflater.inflate(R.menu.new_note_menu, menu) //надуваем(вызываем) нашу разметку
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {           //функция, слушатель нажатий данных кнопок
        if (item.itemId == R.id.id_save) {            //проверяем условие
            setMainResult()             //то делаем новую заметку
        } else if (item.itemId == android.R.id.home) {             //проверяем условие
            finish()            // закрываем
        } else if (item.itemId == R.id.id_bold) {              //проверяем условие
            setBoldForSelectedText()            //меняем шрифт
        } else if (item.itemId == R.id.id_color) {              //проверяем условие
            if (binding.colorPicker.isShown) { //проверяем условие, если наш ColorPicker видимый --
                closeColorPicker() //то, мы закрываем ColorPicker
            } else { //если закрыт ---
                openColorPicker() //то, мы открываем наш ColorPicker
            }
        }
        return super.onOptionsItemSelected(item) //возвращаем
    }

    // функция для изменения шрифта текста в заметке, а именно в описании
    private fun setBoldForSelectedText() = with(binding) {
        val startPos = edDescription.selectionStart //начало позиции выделенного текста
        val endPos = edDescription.selectionEnd //конец позиции выделенного текста

        val styles = edDescription.text.getSpans(
            startPos,
            endPos,
            StyleSpan::class.java
        )//создаём переменную, в которой указываем ввыделенный текст от начала до конца в данной заметке
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) { //если текст уже жирный то --
            edDescription.text.removeSpan(styles[0]) //меняем на обычный
        } else { //в противмно случае
            boldStyle = StyleSpan(Typeface.BOLD) //меняем на жирный
        }
        edDescription.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//меняет
        edDescription.text.trim()
        edDescription.setSelection(startPos)
    }

    // функция для изменения цвета текста в заметке, а именно в описании
    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        val startPos = edDescription.selectionStart //начало позиции выделенного текста
        val endPos = edDescription.selectionEnd //конец позиции выделенного текста

        val styles = edDescription.text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)//создаём переменную, в которой указываем ввыделенный текст от начала до конца в данной заметке

        if (styles.isNotEmpty())  edDescription.text.removeSpan(styles[0])//если текст уже цветной то меняем на обычный
            edDescription.text.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@NewNoteActivity, colorId)), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//меняем цвет
            edDescription.text.trim()
            edDescription.setSelection(startPos)

    }

    private fun setMainResult() { //функция для отправки результата обратно лаунчера
        var editState = "new" //создаём переменную для того чтобы отправить её и понять, что изменяем заметку
        val tempNote: NoteItem? //создаем временную переменную tempNote, которая принадлежит NoteItem, которая может быть null
        if (note == null) { //проверяем, если заметка пустая --
            tempNote = createNewNote() //то создаём новую заметку с помощью функции createNewNote()
        } else { //в противном случае, если заметка не пустая --
            editState = "update" //константа для понимания, что мы изменяем заметку(note)
            tempNote = updateNote() //то изменяем заметку с помощью функции updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote) //отправляем наш NoteItem на наш NoteFragment
            putExtra(NoteFragment.EDIT_STATE_KEY, editState) //отправляем наш NoteItem на наш NoteFragment
        }
        setResult(RESULT_OK, i) // отправляем результат и данные
        finish() //закрываем
    }

    private fun createNewNote(): NoteItem { //функция которая заполняет нашу заметку NoteItem
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            ""
        ) //возвращает заполненую заметку NoteItem
    }

    //функция для изменения заметки(т.е. перезаписываем её и отправляем старую заметку с изменениями)
    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text)
        )
    }

    private fun actionBarSettings() { //функция для настройки, здесь мы делаем возврат  на предыдущую страницу нажав кнопку стрелка
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun openColorPicker() {   //создаём функция для открытия ColoPicker
        binding.colorPicker.visibility = View.VISIBLE //делаем ColorPicker видимым на экране
        val openAnim = AnimationUtils.loadAnimation(
            this,
            R.anim.open_color_picker
        ) //создаём переменную для открытия анимации и загружаем в эту переменную нашу анимация из xml файла
        binding.colorPicker.startAnimation(openAnim) //передаём нашу анимацию в layout
    }

    private fun closeColorPicker() {   //создаём функция для открытия ColoPicker
        val openAnim = AnimationUtils.loadAnimation(
            this,
            R.anim.close_color_picker
        ) //создаём переменную для открытия анимации и загружаем в эту переменную нашу анимация из xml файла
        openAnim.setAnimationListener(object :
            Animation.AnimationListener { //добавляем слушатель анимации для закрытия анимации
            override fun onAnimationStart(p0: Animation?) { //здесь запускается анимация

            }

            override fun onAnimationEnd(p0: Animation?) { //здесь заканчивается анимация
                binding.colorPicker.visibility = View.GONE //ColorPicker становится невидимим
            }

            override fun onAnimationRepeat(p0: Animation?) { //здесь анимация повторяется

            }
        })
        binding.colorPicker.startAnimation(openAnim) //передаём нашу анимацию в layout
    }

    private fun actionMenuCallback(){ // функция для удаления панели(копировать, удалить, передать и т.п.) при выделения текста
        val actionCallback = object : ActionMode.Callback{
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }
            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return true
            }
            override fun onDestroyActionMode(mode: ActionMode?) {

            }
        }
        binding.edDescription.customSelectionActionModeCallback = actionCallback //выполнять при выделении текста
    }
    //создаём функцию, где мы достаём наши EditText и меняем размер
    private fun setTextSize() = with(binding){
        edTitle.setTextSize(pref?.getString("title_text_key", "16")) //меняем размер текста для заголовков
        edDescription.setTextSize(pref?.getString("content_text_key", "14")) //меняем размер текста для описания
    }

    private fun EditText.setTextSize(size: String?){ //создаём  у EditText,  новую функция setTextSize ---- всё это для настроек
        if(size != null) this.textSize = size.toFloat() //если size не null, то этот EditText изменяем на выбранный размер
    }

    private fun getSelectedTheme(): Int{// функция для запуска темы из памяти настроек
        return if(defPref.getString("theme_key", "green") == "green"){ //если в памяти стоит green
            R.style.Theme_NewNoteGreen // то остаётся green
        }else{ //противном случае
            R.style.Theme_NewNoteBlue //ставится blue
        }
    }
}