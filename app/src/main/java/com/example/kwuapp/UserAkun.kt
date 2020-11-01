package com.example.kwuapp

class UserAkun {
    //Getters and Setters
    var username: String? = null
    var email: String? = null

    constructor() {
        //Empty Constructor For Firebase
    }

    constructor(username: String?, email: String?) {
        this.username = username //Parameterized for Program-Inhouse objects.
        this.email = email
    }

}