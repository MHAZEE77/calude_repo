package com.silkysys.warehouse.sharedcode.domain.autentcation.login.entity

import com.silkysys.warehouse.domain.autentcation.login.entity.Companies

/**
 * Created by: Mahmoud Ibrahim on 3/5/2023
 * Email: mahmoudibrahiem57@gmail.com
 **/
data class LoginEntity(
    var id : Int,
    var name: String,
    var email: String,
    var token: String,
    var companies: Companies?,
    var refreshToken:String,
    var clientId:String
)
