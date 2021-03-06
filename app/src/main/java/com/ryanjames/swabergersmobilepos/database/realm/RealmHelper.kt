package com.ryanjames.swabergersmobilepos.database.realm

import io.realm.Realm

fun executeRealmTransaction(action: (realm: Realm) -> Unit) {
    val realm = Realm.getDefaultInstance()
    if (!realm.isInTransaction) {
        realm.beginTransaction()
    }
    action.invoke(realm)
    realm.commitTransaction()
    realm.close()
}