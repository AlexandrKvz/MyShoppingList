package com.xotkins.myshoppinglist.billing

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

//класс для встроенных покупок
class BillingManager(val activity: AppCompatActivity) {
    private var bClient: BillingClient? = null //создаём переменную bClient класса BillingClient

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient(){ //функция для покупок
        bClient = BillingClient.newBuilder(activity).setListener(getPurchaseListener()).enablePendingPurchases().build() //здесь вы инициализируем нашу переменную и с помощью этой переменной мы можем делать подключения к плеймаркет и т.п.
    }

    private fun savePref(isPurchase: Boolean){//в этой функции будем писать код для сохранения в память, была ли совершена покупка(true) или нет(false)
        val pref = activity.getSharedPreferences(MAIN_PREF, Context.MODE_PRIVATE) //этот код показывает покупал ли пользователь блок рекламы или нет(с премиум подпсикой он или нет)
        val editor = pref.edit() //с помощью переменной editor и с помощью классом getSharedPreferences, мы сможем записать данные
        editor.putBoolean(REMOVE_ADS_KEY, isPurchase) //под ключом REMOVE_ADS_KEY запишется какое то значение isPurchase
        editor.apply() //запускаем функцию
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener{ //создаём слушатель и этот слушатель возвращает нам PurchasesUpdatedListener
        return PurchasesUpdatedListener { //возвращает нам PurchasesUpdatedListener
                bResult, list ->
            run {
                if (bResult.responseCode == BillingClient.BillingResponseCode.OK) { //если результат равен
                    list?.get(0)?.let { nonConsumableItem(it) }// если список не пустой запустится эта функция { nonConsumableItem(it) }
                }
            }
        }
    }

    fun startConnection(){//функция соединения с плеймаркетом (вылезает диалог с ценой и купить)
        bClient?.startConnection(object : BillingClientStateListener{
            override fun onBillingServiceDisconnected() {
            }
            override fun onBillingSetupFinished(p0: BillingResult) {
                getItem()
            }
        })
    }

    private fun getItem(){//функция показывает наш продукт, его цену и возможность купить
        val skuList = ArrayList<String>()//создаём список покупок
        skuList.add(REMOVE_AD_ITEM)//здесь добавляем элемент для покупки(очень важно в playconsole указывать точно такую же константу)
        val skuDetails = SkuDetailsParams.newBuilder() //создаём переменную, в которую будем заносить данные о покупка
        skuDetails.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)//передаёмв перменную skuDetails наш список покупок и указываем какого типа эти продукты BillingClient.SkuType.INAPP в данном случае это просто встроенная покупка
        //используем функцию queryPurchaseHistoryAsync(), чтобы делать запрос и делать это асинхронно,чтобы не тормозить основной поток(т.е. на второстепенном потоке)
        bClient?.querySkuDetailsAsync(skuDetails.build()){
                bResult, list ->
                run {
                    if(bResult.responseCode == BillingClient.BillingResponseCode.OK){
                        if(list != null){ //список не равен null
                            if(list.isNotEmpty()) { // и что список не пустой
                                val bFlowParams = BillingFlowParams.newBuilder().setSkuDetails(list[0]).build() //здесь мы передаём подробную информацию о покупке
                                bClient?.launchBillingFlow(activity, bFlowParams)
                            }
                        }
                    }
                }
        }
    }

//Purchase это класс, который несёт в себе информацию о покупке
    private fun nonConsumableItem(purchase: Purchase){//создаём функцию покупки, покупается один раз на совсем
        //если состояние покупки равно  купленно
        if(purchase.purchaseState == Purchase.PurchaseState.PURCHASED){ //мы можем подверждать покупку
            if(!purchase.isAcknowledged){//если состояние покупки не одобрено
                val acParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build() //создаём переменную acParams, которую инициализируем, в неё передаём параметры(специальный индетификатор) покупки, т.е. подтверждение покупки
                //чтобы отправить подтверждение покупки используем bClient и в эту переменную мы передаем acParams()подтверждение покупки)
                bClient?.acknowledgePurchase(acParams){
                    if(it.responseCode == BillingClient.BillingResponseCode.OK) { //если подтвердили покупки, значит покупка прошла\
                        savePref(true)//покупка прошла успешно, показываем сообщение и даём премиум подписку
                        Toast.makeText(activity, "Спасибо за покупку!",Toast.LENGTH_LONG).show()
                    }else{ //в противном случае
                        savePref(false)//покупка не прошла, показываем сообщение и не даём премиум подписку
                        Toast.makeText(activity, "Не удалось совершить покупку!",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun closeConnection(){//функция для закрытия
        bClient?.endConnection()
    }

    companion object{ //константа индитификатор нашего продукта для покупки, добавляется на playconsole
        const val REMOVE_AD_ITEM = "remove_ad_item_id"
        const val MAIN_PREF = "main_pref"
        const val REMOVE_ADS_KEY = "remove_ads_key"
    }
}