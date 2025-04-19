package org.example.core.FileSearch.indexing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchKey
import java.nio.file.WatchService
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.isDirectory
import kotlin.io.path.notExists

object WatchServiceManager {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activeWatches = ConcurrentHashMap<Path, WatchKey>()

    fun startWatching(path: Path, onChanged: (Path) -> Unit) {
        if (activeWatches.containsKey(path)) return // уже отслеживается

        scope.launch {
            try {
                val watcher = FileSystems.getDefault().newWatchService()
                registerRecursively(path, watcher)

                while (true) {
                    val key = watcher.take()
                    val dir = key.watchable() as? Path ?: continue

                    for (event in key.pollEvents()) {
                        val kind = event.kind()
                        if (kind == StandardWatchEventKinds.OVERFLOW) continue

                        val changedPath = dir.resolve(event.context() as Path)

                        if (changedPath.notExists()) continue

                        onChanged(path) // Триггерим обновление/инвалидацию
                    }

                    val valid = key.reset()
                    if (!valid) break
                }
            } catch (e: IOException) {
                println("WatchService error: ${e.message}")
            }
        }
    }

    private fun registerRecursively(start: Path, watcher: WatchService) {
        Files.walk(start).filter { it.isDirectory() }.forEach { dir ->
            val key = dir.register(
                watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY
            )
            activeWatches[dir] = key
        }
    }
}