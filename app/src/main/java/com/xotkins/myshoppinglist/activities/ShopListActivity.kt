package com.xotkins.myshoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.database.MainViewModel
import com.xotkins.myshoppinglist.database.ShopListItemAdapter
import com.xotkins.myshoppinglist.databinding.ActivityShopListBinding
import com.xotkins.myshoppinglist.databinding.ShopListItemBinding

import com.xotkins.myshoppinglist.dialogs.EditListItemDialog
import com.xotkins.myshoppinglist.entities.LibraryItem
import com.xotkins.myshoppinglist.entities.ShopListItem
import com.xotkins.myshoppinglist.entities.ShopListNameItem
import com.xotkins.myshoppinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var binding: ActivityShopListBinding
    private var shopListNameItem: ShopListNameItem? = null //создаётся переменная, чтобы, когда нажимаем на список появлялись все элементы списка
    private lateinit var saveItem: MenuItem // создаём переменную для кнопки Save
    private var edItem: EditText? = null //создаём переменную чтобы присвоить разметку EditText из app:actionLayout="@layout/edit_action_layout"
    private var adapter: ShopListItemAdapter? = null /// создаём адаптер, куда записывам
    private lateinit var textWatcher: TextWatcher
    private lateinit var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by viewModels { //сюда передаётся класс ViewModel, база данных
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme()) //
        setContentView(binding.root)
        initRcView() //запускаем функцию
        init() //запускаем функцию
        listItemObserver() //запускаем функцию
        actionBarSettings() //запускаем функцию

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // создаём функцию для подключения меню
        menuInflater.inflate(R.menu.shop_list_menu, menu) //надуваем(вызываем) нашу разметку
        saveItem = menu?.findItem(R.id.save_item)!! //здесь мы находим эту кнопку Save item на экране
        val newItems = menu.findItem(R.id.new_items)!! //здесь мы находим эту кнопку New items на экране
        edItem = newItems.actionView.findViewById(R.id.edNewShopItem) as EditText //присваиваем edItem тот самый EditText, который мы ввели
        newItems.setOnActionExpandListener(expandActionView()) //добавляем New items слушатель нажатия
        saveItem.isVisible = false // здесь делаем кнопки Save не видимой
        textWatcher = textWatcher()

        return true
    }

    private fun textWatcher(): TextWatcher{ //функция для подсвечивания всплывающей подсказки на основе библиотеки(функция которая следит за изменениями нашего текста)
        return object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mainViewModel.getAllLibraryItems("%$s%") //здесь мы передаём буквы для всплывающей подсказки из LibraryItem через fun libraryItemObserver(), далее указываем через %$s%
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //создаём слушатель нажатия на кнопку
        when (item.itemId) {//проверяем условие,
            R.id.save_item -> {  //если жмём на кнопку Save
                addNewShopItem(edItem?.text.toString()) //то запускаем эту функцию
            }
            R.id.delete_list -> { //удалить список покупок
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> { //очищение списка покупок
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true)
            }
            R.id.share_list -> { //передать список покупок
                startActivity(Intent.createChooser( //с помощью createChooser выбираем через какую соц.сеть хотим передать наш список
                    ShareHelper.shareShopList(adapter?.currentList!!, shopListNameItem?.name!!),
                "Share by" //поделиться с помощью
                ))
            }
            android.R.id.home ->{ //если нажимаем на стрелку назад, то выходим и обновляем экран с последними изменениями
                saveItemCount()
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String){ //создаём функцию, которая записывает наш элемент в базу данных
        if(name.isEmpty())return //проверяет если какой-нибудь текст, если нет то ничего не происходит
        val item = ShopListItem(null, name, "", false, shopListNameItem?.id!!, 0) //тут вписываем элемент и сохраняем его в базу данных
        edItem?.setText("") //после того как мы сохранили наш элемент в список покупок мы строку для ввода элемента очищаем
        mainViewModel.insertShopItem(item) //передаём эти данные в базу данных для записи
    }

    private fun listItemObserver(){ //функция для обновления нашего списка покупок, где обновляются элементы этого списка
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this, {
            adapter?.submitList(it) // сюда приходит новый список
            binding.tvEmpty.visibility = if(it.isEmpty()){ //проверяем условие, если список покупоку нас пустой ---
                View.VISIBLE //то показываем тескт Empty
            } else { //в противном случае, если список покупок у нас не пустой ---
                View.GONE //убираем текст Empty
            }
        })

    }

    private fun libraryItemObserver(){ //функция observer, которая передаёт обновленные данные всплывающей подсказки
        mainViewModel.libraryItems.observe(this, {
            val tempShopList = ArrayList<ShopListItem>() //создаём временную переменную tempShopList, который будет состоять из элементов ShopListItem
            //каждый раз когда мы берём список LibraryItem, мы его просто перегружаем в ShopListItem, потому что мы не можем брать напрямую данные из ShopListItem
            it.forEach {    item ->
                val shopItem = ShopListItem( //здесь мы берём необходимые данные
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem) //все данные помещаем в tempShopList
            }
            adapter?.submitList(tempShopList) //через адаптер передаём наш готовый список tempShopList
            binding.tvEmpty.visibility = if(it.isEmpty()){ //проверяем условие, если список покупоку нас пустой ---
                View.VISIBLE //то показываем тескт Empty
            } else { //в противном случае, если список покупок у нас не пустой ---
                View.GONE //убираем текст Empty
            }
        })
    }

    //функция где инициализируется RecyclerView и adapter
    private  fun initRcView() = with(binding){
        adapter = ShopListItemAdapter(this@ShopListActivity) //здесь инициализируем адаптер, и передаем listener конкретного фрагмента
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)  //здесь делаем чтобы RecyclerView шёл списком
        rcView.adapter = adapter //передаём адаптер в rcView
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener{ //создаём функцию, чтобы слушатель Save замечал, что наш ActionView появился для заполнения текста
        return object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean { //ActionView появился для заполнения текста
                saveItem.isVisible = true // здесь делаем кнопку Save видимой
                edItem?.addTextChangedListener(textWatcher)//здесь добавляется текст подсказка того элемента который мы уже добавляли
                libraryItemObserver()//здесь мы открывает всплывающей подсказки
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity)//здесь убираем observer
                mainViewModel.getAllLibraryItems("%%")//здесь мы запускаем функцию для поиска всплывающей подсказки
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean { //ActionView закрылся для заполнения текста
                saveItem.isVisible = false // здесь делаем кнопки Save невидимой
                edItem?.removeTextChangedListener(textWatcher) //здесь удаляется текст подсказка того элемента который мы уже добавляли
                invalidateOptionsMenu() // возвращаем меню, где видима кнопка только New items
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity) //здесь закрываем всплывающей подсказки
                edItem?.setText("")//делаем пустую строку
                listItemObserver()//запускаем функцию
                return true
            }

        }
    }


    private fun init(){ //функция, чтобы получать данный список, который мы открыли
        shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem //здесь мы получаем данные

    }

    companion object{
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    override fun deleteItem(id: Int) {//функция удаения элемента из списка покупок
        mainViewModel.deleteItemByListId(id)//перезаписывает наш список
    }



    override fun onCLickItem(shopListItem: ShopListItem, state: Int) { //функция запускается  в fun setItemData
        when(state){ //проверяем условие, что было нажато, если --
            ShopListItemAdapter.DELETE -> deleteItem(shopListItem.id!!)//удаляет элемент из списка покупок
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateListItem(shopListItem) // запись элементов из списка в базу данных
            ShopListItemAdapter.EDIT -> editListItem(shopListItem) // редактирование элементов из списка в базу данных - запускаем функцию
            ShopListItemAdapter.ADD -> addNewShopItem(shopListItem.name) // выбираем вспылвающую подсказку и добавляем её в список покупок
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shopListItem) //редактирование всплывающей подсказки - запускаем функцию
            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> { //удаление всплывающей подсказки - запускаем функцию
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%") //здесь мы показываем то, до того, что мы изменили
            }
        }
    }
    private fun editListItem(item: ShopListItem){ //функция для проверки что нажали, если нажали Edit
        EditListItemDialog.showDialog(this, item, object : EditListItemDialog.Listener{ //запускается диалог для редактирования
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateListItem(item) //здесь перезаписывает наш элемент
            }
        })
    }


    private fun editLibraryItem(item: ShopListItem){ //функция вызывает диалог и мы передаёт ShopListItem
        EditListItemDialog.showDialog(this, item, object : EditListItemDialog.Listener{ //запускается диалог для редактирования
            override fun onClick(item: ShopListItem) { //передаёт ShopListItem
                mainViewModel.updateLibraryItem(LibraryItem(item.id, item.name)) //здесь перезаписывает наш элемент
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%") //здесь мы показываем то, до того, что мы изменили
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCounter = 0 //счётчик отмеченных элементов
        //перебираем элементы в данном списке, который находится в данный момент в адаптере с помощью цикла
        adapter?.currentList?.forEach {
            if(it.itemChecked) checkedItemCounter++ //если есть отмеченные элементы в списке то увеличиваем счётчик
        }
        val tempShopListNameItem = shopListNameItem?.copy(
            allItemCounter = adapter?.itemCount!!, //здесь вы записали в allItemCounter сколько сейчас в данный момент находится элементов в нашем списке
            checkedItemsCounter = checkedItemCounter //здесь передаём кол-во отмеченных элементов из списка
        )
        mainViewModel.updateListName(tempShopListNameItem!!) //обновляем список  передавая значение tempShopListNameItem
    }

    private fun actionBarSettings() { //функция для настройки, здесь мы делаем возврат  на предыдущую страницу нажав кнопку стрелка
        val ab = supportActionBar
        saveItemCount()
        ab?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onBackPressed() {
       saveItemCount() //запускаем функцию
        super.onBackPressed()
    }

    private fun getSelectedTheme(): Int{// функция для запуска темы из памяти настроек
        return if(defPref.getString("theme_key", "green") == "green"){ //если в памяти стоит green
            R.style.Theme_MyShoppingListGreen // то остаётся green
        }else{ //противном случае
            R.style.Theme_MyShoppingListBlue //ставится blue
        }
    }
}