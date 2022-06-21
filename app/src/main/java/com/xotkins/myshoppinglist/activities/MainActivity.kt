package com.xotkins.myshoppinglist.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import androidx.preference.PreferenceManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.xotkins.myshoppinglist.R
import com.xotkins.myshoppinglist.billing.BillingManager
import com.xotkins.myshoppinglist.databinding.ActivityMainBinding
import com.xotkins.myshoppinglist.dialogs.NewListDialog
import com.xotkins.myshoppinglist.fragments.FragmentManager
import com.xotkins.myshoppinglist.fragments.NoteFragment
import com.xotkins.myshoppinglist.fragments.ShopListNamesFragment
import com.xotkins.myshoppinglist.settings.SettingsActivity

class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding // включение разметки  binding для активити
    private var currentMenuItemId = R.id.shop_list //создаём переменную, чтобы обновлять фрагменты, после изменений настроек, поумолчанию она shop_list
    private lateinit var defPref: SharedPreferences
    private  var currentTheme = "" // создаём переменную для обновления нашей темы, после изменения
    private var iAd: InterstitialAd? = null //переменная для создания рекламы
    private var adShowCounter = 0 //счётчик рекламы для нажатия
    private var adShowCounterMax = 7 //максимальное кол-во нажатий для появления рекламы
    private lateinit var pref: SharedPreferences //создаём переменную для премиум пользователя


    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)//инициализируем нашу переменную
        currentTheme = defPref.getString("theme_key", "green").toString()//выбор темы изначально
        setTheme(getSelectedTheme())

        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE) //инициализируем переменную pref, чтобы включить премиум аккаунт
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this) //когда запускаем приложение, будет открываться фрагмент ShopListNameFragments
        setBottomNavListener() //запуск функции, чтобы работала
        if(!pref.getBoolean(BillingManager.REMOVE_ADS_KEY, false)) loadInterAd() //если пользователь купил блок рекламы в память записывается true и реклама отключается, если нет то реклама будет работать
    }
    private fun loadInterAd(){
        val request = AdRequest.Builder().build() //здесь мы создали переменную запрос для получения рекламы
        InterstitialAd.load(this, getString(R.string.inter_ad_id), request, object : InterstitialAdLoadCallback(){ //здесь мы загружаем рекламу, если успешно загрузилось
            override fun onAdLoaded(ad: InterstitialAd) { //запускаем это функцию
                iAd = ad //загружаем нашу рекламу
            }
            override fun onAdFailedToLoad(ad: LoadAdError) { //запускается если реклама не загрузилась
                iAd = null //пусто
            }
        })
    }

    private fun showInterAd(adListener: AdListener){ //функция, чтобы показывать рекламу и с помощью интерфейса ждём что в нём происходит
        if(iAd != null && adShowCounter > adShowCounterMax && !pref.getBoolean(BillingManager.REMOVE_ADS_KEY, false)){//если реклама есть и счётчик нажатий  больше 5 раз на той кнопке где стоит реклама, то запускается callback ---
            iAd?.fullScreenContentCallback = object : FullScreenContentCallback(){ //этот callback следит, что происходит с обьявлением
                override fun onAdDismissedFullScreenContent() {// если пользователь просмотрел рекламу и нажал крестик
                    iAd = null //становиться пусто
                    loadInterAd() //подгружаем новую рекламу
                    adListener.onFinish() //реклама была просмотрена
                }
                override fun onAdFailedToShowFullScreenContent(p0: AdError) { //если во время рекламы возникла ошибка
                    iAd = null //становиться пусто
                    loadInterAd() //подгружаем новую рекламу
                }
                override fun onAdShowedFullScreenContent() { //когда реклама была показана
                    iAd = null //становиться пусто
                    loadInterAd() //подгружаем новую рекламу
                }
            }
            iAd?.show(this)//здесь мы показываем рекламу, но прежде всего нужно создать callback, чтобы следить, что происходит с рекламой
            adShowCounter = 0 //после показа рекламы счётчик снова 0
        }else{//если реклама не была загружена
            adShowCounter++ //увеличиваем счётчик нажатия кнопки на панели в которой есть реклама
            adListener.onFinish() //реклама была просмотрена
        }
    }

    private fun setBottomNavListener(){ //функция слушатель нажатий на нижней панели навигации
        binding.bNavigationView.setOnItemSelectedListener {
            when(it.itemId){ // проверка на какую кнопку нажимают
                R.id.settings ->{ //когда пользователь жмёт на кнопку settings, сначала ---
                    showInterAd(object : AdListener{ //открывается интерфейс
                        override fun onFinish() { // откроется реклама, когда пользователь нажимает Settings и после рекламы функция запускает settings
                            startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) //запускаем фрагмент настроек
                        }
                    })
                }
                R.id.notes ->{
                    showInterAd(object : AdListener{ //открывается интерфейс
                        override fun onFinish() { // откроется реклама, когда пользователь нажимает notes и после рекламы функция запускает notes
                            currentMenuItemId = R.id.notes //нажимаем на кнопку для обновления
                            FragmentManager.setFragment(NoteFragment.newInstance(), this@MainActivity) //запускаем фрагмент блокнот
                        }
                    })

                }
                R.id.shop_list ->{
                    currentMenuItemId = R.id.shop_list //нажимаем на кнопку для обновления
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this) //запускаем фрагмент список покупок
                }
                R.id.new_item ->{
                    FragmentManager.currentFrag?.onClickNew() //запускаем фрагмент добавить
                }
            }
            true
        }
    }

    override fun onResume() {//вызываем функцию onResume(), это когда мы возвращаемся с наших настроек
        super.onResume()
        binding.bNavigationView.selectedItemId = currentMenuItemId //указываем какая кнопка должна быть нажата
        if(defPref.getString("theme_key", "green") != currentTheme) recreate() //проверяем, если тема неравна нашей теме значит меняй тему, которую выбрали , в противном случае ничего не делаем
    }

    private fun getSelectedTheme(): Int{// функция для запуска темы из памяти настроек
        return if(defPref.getString("theme_key", "green") == "green"){ //если в памяти стоит green
            R.style.Theme_MyShoppingListGreen // то остаётся green
        }else{ //противном случае
            R.style.Theme_MyShoppingListBlue //ставится blue
        }
    }

    override fun onClick(name: String) {
    }

    interface AdListener{ //создаём интерфейс и  внём указываем функции
        fun onFinish()// функция озночает, что реклама была просмотрена
    }
}