package com.silkysys.warehouse.app.autentcation.login

/**
 *  1- email is empty
 *  2- password is empty
 */
    object ValidateLogin {

        // check id email is empty
        fun validateEmail(email: String): Boolean {
            return email.isEmpty()
        }

        // check id email is password
        fun validatePassword(password: String): Boolean {
            return password.isEmpty()
        }

    }
