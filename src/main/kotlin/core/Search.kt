package org.example.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope

class SearchBuilder {
    var urlDataBase: String? = null
    var userDataBase: String? = null
    var passwordDataBase: String? = null
    var timeIndexing: Long = 5000
    var coroutineScope: CoroutineScope? = null

    fun setUrlDataBase(url: String) = apply {
        urlDataBase = url
    }

    fun setUserDataBase(username: String) = apply {
        userDataBase = username
    }

    fun setPasswordDataBase(password: String) = apply {
        passwordDataBase = password
    }

    fun setTimeIndexing(time: Long) = apply {
        timeIndexing = time
    }

    suspend fun build(): SearchEngine {
        val searchEngine = SearchEngine()
        return searchEngine.run(urlDataBase!!, userDataBase!!, passwordDataBase!!, coroutineScope!!)
    }
}