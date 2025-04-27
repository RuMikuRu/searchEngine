## ✅ Features
- Filename & directory name search
- Wildcard mask support (`*.kt`, `*.pdf`, etc.)
- Optional full-text content search
- Coroutine-based async search
- Caching & indexing
- Live monitoring with WatchService
- CLI and easy-to-use API wrapper
- FuzzySearch

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

## 🔧 Gradle
```kotlin
dependencies {
    implementation(files("path"))
}
```