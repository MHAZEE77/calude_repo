package com.silkysys.warehouse.domain.entity

import android.view.View

abstract class MainMenu (
    var title: String,
    var description: String,
    var icon: Int,
    var clickListener: View.OnClickListener
)

class NoneMenuType(
    title: String,
    description: String,
    icon: Int,
    clickListener: View.OnClickListener
) : MainMenu(title, description, icon, clickListener)

class ProgressMenuType(
    title: String,
    description: String,
    icon: Int,
    clickListener: View.OnClickListener,
    val value: Int,
    val total: Int
): MainMenu(title, description, icon, clickListener)

class BadgeMenuType(
    title: String,
    description: String,
    icon: Int,
    clickListener: View.OnClickListener,
    val value: Int
) : MainMenu(title, description, icon, clickListener)
