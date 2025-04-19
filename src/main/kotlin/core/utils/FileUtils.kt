package org.example.core.utils

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.Path

object FileUtils {
    fun resolvePath(path: String): Path = Paths.get(path).toAbsolutePath().normalize()

    fun wildcardToRegex(pattern: String): Regex {
        val regex = pattern.replace(".", "\\.").replace("*", ".*").replace("?", ".")
        return Regex("^$regex$", RegexOption.IGNORE_CASE)
    }
}