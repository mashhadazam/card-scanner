package com.lemon.cardscanner

import com.lemon.cardscanner.banks.amex.AmexCatalog
import com.lemon.cardscanner.banks.bmo.BmoCatalog
import com.lemon.cardscanner.banks.cibc.CibcCatalog
import com.lemon.cardscanner.banks.rbc.RbcCatalog
import com.lemon.cardscanner.banks.td.TdCatalog
import com.lemon.cardscanner.core.CardConfig

/**
 * The compiled catalog: every bank module rolls its cards into one list,
 * and this is what the app displays. Card facts live in the bank modules —
 * the app never invents them.
 *
 * To add a bank: create :banks/<bank>, list it in settings.gradle.kts,
 * add its module here, and add its offer pages to backend/sources.yaml.
 */
object BankCatalogs {
    val all: List<CardConfig> = listOf(
        TdCatalog.cards,
        RbcCatalog.cards,
        CibcCatalog.cards,
        BmoCatalog.cards,
        AmexCatalog.cards,
    ).flatten()
}
