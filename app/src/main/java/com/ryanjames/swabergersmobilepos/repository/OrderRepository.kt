package com.ryanjames.swabergersmobilepos.repository

import android.content.SharedPreferences
import com.ryanjames.swabergersmobilepos.database.realm.OrderRealmDao
import com.ryanjames.swabergersmobilepos.database.realm.executeRealmTransaction
import com.ryanjames.swabergersmobilepos.domain.LineItem
import com.ryanjames.swabergersmobilepos.domain.Order
import com.ryanjames.swabergersmobilepos.mappers.toDomain
import com.ryanjames.swabergersmobilepos.mappers.toEntity
import com.ryanjames.swabergersmobilepos.mappers.toRemoteEntity
import com.ryanjames.swabergersmobilepos.network.retrofit.SwabergersService
import io.reactivex.Single

class OrderRepository(sharedPreferences: SharedPreferences) {

    private val swabergersService = SwabergersService(sharedPreferences)
    private val orderRealmDao = OrderRealmDao()

    fun getLocalBag(): Single<List<LineItem>> {
        return orderRealmDao.getLineItems().map { it.lineItems.map { lineItem -> lineItem.toDomain() } }
    }

    fun insertLineItem(lineItem: LineItem) {
        executeRealmTransaction { realm -> orderRealmDao.insertLineItem(realm, lineItem.toEntity(realm)) }
    }

    fun updateLineItem(lineItem: LineItem) {
        executeRealmTransaction { realm -> orderRealmDao.updateLineItem(realm, lineItem.toEntity(realm)) }
    }

    fun postOrder(order: Order): Single<Boolean> {
        return swabergersService.postOrder(order.toRemoteEntity()).map { true }
    }

    fun clearLocalBag() {
        executeRealmTransaction { realm -> orderRealmDao.deleteAllLineItems(realm) }
    }
}