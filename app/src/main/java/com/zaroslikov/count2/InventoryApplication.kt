package com.zaroslikov.count2

import android.app.Application
import com.zaroslikov.count2.data.InventoryDatabase

class InventoryApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    val database: InventoryDatabase by lazy { InventoryDatabase.getDatabase(this) }
}
