package com.xotkins.myshoppinglist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.xotkins.myshoppinglist.activities.MainApp
import com.xotkins.myshoppinglist.activities.ShopListActivity
import com.xotkins.myshoppinglist.database.MainViewModel
import com.xotkins.myshoppinglist.database.ShopListNameAdapter
import com.xotkins.myshoppinglist.databinding.FragmentShopListNamesBinding
import com.xotkins.myshoppinglist.dialogs.DeleteDialog
import com.xotkins.myshoppinglist.dialogs.NewListDialog
import com.xotkins.myshoppinglist.entities.ShopListNameItem
import com.xotkins.myshoppinglist.utils.TimeManager


class  ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.Listener {   //Создается класс NoteFragment, который наследуется от BaseFragment
    private lateinit var binding: FragmentShopListNamesBinding //создаём разметку
    private lateinit var adapter: ShopListNameAdapter // создаём адаптер, куда записывам

    private val mainViewModel: MainViewModel by activityViewModels { //сюда передаётся класс ViewModel, база данных
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun onClickNew() { //запускаем абстрактную функцию
        NewListDialog.showDialog(activity as AppCompatActivity, object: NewListDialog.Listener{
            override fun onClick(name: String) { //возвращаем имя списка которое выбрал пользователь
                val shopListName = ShopListNameItem(null, name, TimeManager.getCurrentTime(), 0, 0, "") //создаём переменную shopListName и заполняем ее с помощью конструктора ShoppingListName(..., ..., ..., ..., ..., ...)
                mainViewModel.insertShopListName(shopListName) //вводим и заполняем данными и сохраняем это в базу данных
            }
        }, "") // при создании нового списка, значение пустое используется
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root //показываем наш фрагмент с разметкой
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //эта функция запускается, когда уже созданы все View
        super.onViewCreated(view, savedInstanceState)
        initRcView() // запускается функция
        observer() //запускается функция
    }

    //функция где инициализируется RecyclerView и adapter
    private fun initRcView() = with(binding){
        rcViewShopListName.layoutManager = LinearLayoutManager(activity) //здесь делаем чтобы шло списком
        adapter = ShopListNameAdapter(this@ShopListNamesFragment) //здесь инициализируем адаптер, и передаем listener конкретного фрагмента
        rcViewShopListName.adapter = adapter //передаём адаптер в rcViewShopListName
    }

    private fun observer(){ //функция которая следить за изменениями названий наших списков
        mainViewModel.allShopListNamesItem.observe(viewLifecycleOwner, {
        adapter.submitList(it) //сюда приходит обновленный список, который обновляет адаптер
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun deleteItem(id: Int) { //функиця для удаления списка покупок
        DeleteDialog.showDialog(context as AppCompatActivity, object : DeleteDialog.Listener{ //здесь спрашивает действительно ли мы хотим удалить
            override fun onClick() { //и если мы жмём на кнопку удалить, тогда запускается эта функция и удаляется список, а если нажмём Cancel то просто закроется
                mainViewModel.deleteShopList(id, true)
            }
        })
    }

    override fun editItem(shopListNameItem: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object: NewListDialog.Listener{
            override fun onClick(name: String) { //возвращаем имя списка которое выбрал пользователь
                mainViewModel.updateListName(shopListNameItem.copy(name = name)) //вводим и заполняем данными и сохраняем новое название в базу данных
            }
        }, shopListNameItem.name) //показывает старое название
    }

    override fun onCLickItem(shopListNameItem: ShopListNameItem) {
        val i = Intent(activity, ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem) // здесь мы передаём данные, какой список мы выбрали и отправляем в функцию fun init() в ShopListActivity
        }
        startActivity(i)
    }
}