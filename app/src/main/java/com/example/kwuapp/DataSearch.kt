package com.example.kwuapp

class DataSearch {
    var kata: String? = null
    var listSearch: ArrayList<String> = arrayListOf()

    constructor() {
        //Empty Constructor For Firebase
    }

    constructor(kata: String?, listSearch: ArrayList<String>) {
        this.kata = kata
        this.listSearch = listSearch
    }
}