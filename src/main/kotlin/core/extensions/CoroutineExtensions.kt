package org.example.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.core.FileSearch.api.SearchPlugin

fun CoroutineScope.startRepeatingJob(intervalMs: Long, block: suspend () -> Unit): Job {
    return launch (Dispatchers.IO) {
        while (true) {
            block()
            delay(intervalMs)
        }
    }
}
