## ✅ Features
- Filename & directory name search
- Wildcard mask support (`*.kt`, `*.pdf`, etc.)
- Optional full-text content search
- Coroutine-based async search
- Caching & indexing
- Live monitoring with WatchService
- CLI and easy-to-use API wrapper
- FuzzySearch
- Add custom plugins

## 📦 Usage
```kotlin

val customMatcher: suspend (Path, String) -> Boolean = { path, query ->
    try {
        Files.newBufferedReader(path).useLines { lines ->
            lines.take(10).any { it.contains(query, ignoreCase = true) }
        }
    } catch (e: Exception) {
        false
    }
}

val results = FastSearch {
    path = "/home/user/docs"
    mask = "*.pdf"
    content = "report"
    includeDirs = true
    includeFiles = true
    fuzzySearch = true
    customContentMatcher = customMatcher
}.run()

results.forEach { println(it.path) }
```

## Create plugins

```kotlin
fun main() = runBlocking {
    val results = FastSearch {
        path = "C:\\Users\\bkmzo\\Documents\\kotlin\\test1\\app"
        content = "plugins"
        includeFiles = true
        includeDirs = true
        plugins {
            add(object : SearchPlugin { // <- create plugin
                override suspend fun match(file: Path, query: String): Boolean {
                    return try {
                        Files.newInputStream(file).buffered().reader().use {
                            val buffer = CharArray(5000)
                            val read = it.read(buffer)
                            val text = String(buffer, 0, read)
                            text.contains(query, ignoreCase = true)
                        }
                    } catch (e: Exception) {
                        false
                    }
                }
            })
        }
    }.run().collect {
        println(it.path)
    }
}
```


## 🔧 Gradle
```kotlin
dependencies {
    implementation(files("path"))
}
```