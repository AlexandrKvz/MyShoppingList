package com.xotkins.myshoppinglist.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.xotkins.myshoppinglist.activities.MainApp
import com.xotkins.myshoppinglist.activities.NewNoteActivity
import com.xotkins.myshoppinglist.database.MainViewModel
import com.xotkins.myshoppinglist.database.NoteAdapter
import com.xotkins.myshoppinglist.databinding.FragmentNoteBinding
import com.xotkins.myshoppinglist.entities.NoteItem


class  NoteFragment : BaseFragment(), NoteAdapter.Listener {   //Создается класс NoteFragment, который наследуется от BaseFragment
  private lateinit var binding: FragmentNoteBinding
  private lateinit var editLauncher: ActivityResultLauncher<Intent> //создаётся лаунчер для отправки и принятия данных
  private lateinit var adapter: NoteAdapter // создаём адаптер, куда записывам
  private lateinit var defPref: SharedPreferences // создаём defPref, куда записываем выбранное время из настроек

    private val mainViewModel: MainViewModel by activityViewModels { //сюда передаётся класс ViewModel, база данных
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() { //запускаем абстрактную функцию
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java)) //запускаем лаунчер и ждём результат
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult() //запуск функции лаунчера
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root //показываем наш фрагмент с разметкой
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //эта функция запускается, когда уже созданы все View
        super.onViewCreated(view, savedInstanceState)
        initRcView() // запускается функция
        observer() //запускается функция
    }


//функция где инициализируется RecyclerView и adapter
    private fun initRcView() = with(binding){
        defPref = activity?.let { PreferenceManager.getDefaultSharedPreferences(it) }!! //здесь записываем время которое выбирается либо из настроек либо стандартно
        rcViewNote.layoutManager = getLayoutManager() //здесь показывает экран, который выбран из памяти настроек
        adapter = NoteAdapter(this@NoteFragment, defPref) //здесь инициализируем адаптер, и передаем listener конкретного фрагмента
        rcViewNote.adapter = adapter //передаём адаптер в rcViewNote
    }

    private fun getLayoutManager(): RecyclerView.LayoutManager{ //функция возвращает экран в зависимости какой стиль выбран из памяти настроек
        return if(defPref.getString("note_style_key", "Linear") == "Linear"){ //если выбран стиль "Linear"
            LinearLayoutManager(activity) //то выбираем стиль будет списком
        }else{ //в противно случае будет---
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) //выбрано таблицей
        }
    }

    private fun observer(){ //функция которая следить за изменениями наших заметок
        mainViewModel.allNotes.observe(viewLifecycleOwner, {
            adapter.submitList(it) //сюда приходит обновленный список, который обновляет адаптер
        })
    }

    private fun onEditResult(){ //здесь мы принимаем результат лаунчера
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK) {
                val editState = it.data?.getStringExtra(EDIT_STATE_KEY) //создаём переменную для получения данных с ключем EDIT_STATE_KEY для изменения заметок
                if (editState == "update") { // проверяем условии, если editState == "update", то ----
                    mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem) // здесь мы отправляем целый класс NoteItem и получаем обратно(изменяем заметку)

                } else {
                    mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem) // здесь мы отправляем целый класс NoteItem и получаем обратно(создаём новую заметку)
                }
            }
        }
    }
    override fun deleteItem(id: Int) { //функция при нажатии на кнопку удаление созданной заметки
        mainViewModel.deleteNote(id) // теперь удаления работает через архитектуру MVVM, а не на напрямую через класс
    }

    override fun onCLickItem(note: NoteItem) { //создаем функцию при нажатии на кнопку мы получаем заметку на которую мы нажали
        //создаём переменную, которая будет хранить данные для передачи туда, куда мы хотим
        val intent = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY, note)//передаем заметку через ключ
        }
        editLauncher.launch(intent) //запускаем лаунчер
    }
    companion object {
        const val NEW_NOTE_KEY = "new_note_key"
        const val EDIT_STATE_KEY = "edit_state_key"
        @JvmStatic
        fun newInstance() = NoteFragment()
    }
}