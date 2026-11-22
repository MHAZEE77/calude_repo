package com.silkysys.warehouse.app.picking.pick.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import com.silkysys.warehouse.app.ui.activity.BaseActivity
import com.silkysys.warehouse.app.ui.compose_ui.pick.PickScreen

class PickActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!requireAuthenticatedSession()) return

        setContent {
            PickScreen(onBackClick = { finish() })
        }
    }
}