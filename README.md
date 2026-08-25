# RBVerticalTicker

A Jetpack Compose vertical ticker: shows the most recent items in a rolling
window, newest at the bottom, animating as new entries arrive. Usable from
Compose directly, or from XML/Java via `VerticalTickerView`.

## Installation

Add the JitPack repository, then depend on a tagged release:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("com.github.RBWare:RBVerticalTicker:<version>")
}
```

Replace `<version>` with a released tag (e.g. `1.0.0`) - see
[Releases](https://github.com/RBWare/RBVerticalTicker/releases) for what's
been published, or [jitpack.io/#RBWare/RBVerticalTicker](https://jitpack.io/#RBWare/RBVerticalTicker)
to build any tag/commit on demand.

## Usage

### Compose

```kotlin
val state = rememberVerticalTickerState(listOf("First", "Second", "Third"))
VerticalTicker(
    state = state,
    visibleCount = 3,
    topFadeAlpha = 0.15f,
    animationDurationMillis = 300,
)

state.showNext()                 // advance through the list
state.showNext("Ad-hoc alert!")  // or show something not in the list
state.setOnItemShownListener { item -> /* react elsewhere in the UI */ }
```

### XML / Java

```xml
<com.rbware.rbverticalticker.VerticalTickerView
    android:id="@+id/ticker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:textSize="18sp"
    android:textColor="#6750A4"
    android:fontFamily="sans-serif-medium"
    app:visibleCount="3"
    app:topFadeAlpha="0.15"
    app:animationDurationMillis="300" />
```

```java
VerticalTickerView ticker = findViewById(R.id.ticker);
ticker.setItems(Arrays.asList("First", "Second", "Third"));
ticker.showNext();
ticker.setOnItemShownListener(item -> { /* react elsewhere in the UI */ });
```

## Releasing

Push a tag matching `v*.*.*` (e.g. `v1.0.0`) - `.github/workflows/release.yml`
runs the library's tests, builds the release `.aar`, and publishes a GitHub
Release with the `.aar` attached. JitPack needs no separate publish step: it
builds whatever tag a consumer requests directly from this repo the first
time it's asked for.
