## ✅ Features
- Filename & directory name search
- Wildcard mask support (`*.kt`, `*.pdf`, etc.)
- Optional full-text content search
- Coroutine-based async search
- Caching & indexing
- Live monitoring with WatchService
- CLI and easy-to-use API wrapper

## 📦 Usage
```kotlin
val results = FastSearch {
    path = "/home/user/docs"
    mask = "*.pdf"
    content = "report"
}.run()

results.forEach { println(it.path) }
```

## 🔧 Gradle
```kotlin
dependencies {
    implementation("com.yourorg:fastsearch-api:1.0.0")
}
```