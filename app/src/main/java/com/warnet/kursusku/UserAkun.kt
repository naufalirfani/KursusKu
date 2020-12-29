package com.warnet.kursusku

class UserAkun {
    //Getters and Setters
    var username: String? = null
    var email: String? = null
    var userId: String? = null

    constructor() {
        //Empty Constructor For Firebase
    }

    constructor(username: String?, email: String?, userId: String?) {
        this.username = username //Parameterized for Program-Inhouse objects.
        this.email = email
        this.userId = userId
    }

}