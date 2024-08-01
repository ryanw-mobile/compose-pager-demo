# Jetpack Compose Endless Horizontal Pager Animation Demo ![Gradle Build](https://github.com/ryanw-mobile/compose-pager-demo/actions/workflows/main_build.yml/badge.svg)

Complementary article:

* [Reasons to Love the New Jetpack Compose Pager](https://medium.com/@callmeryan/reasons-to-love-the-new-jetpack-compose-pager-a53366fb6906)
* [Implementing an Endless Pager in Jetpack Compose]()

<p align="center">
  <img src="screenshots/240801_animated_wide.gif" width="320" alt="animated screenshot"/>
</p>

This is an app demonstrating the official Jetpack Compose Horizontal Pager.

This app shows how straightforward we can set up a Horizontal Pager, feed in whatever content we want, and apply animations.

No more custom views, adapters, fragments and complex lifecycle handling! Imagine how much extra work you need to build this using XML Views?

&nbsp;&nbsp;

## Animations

The page animations are all done using the `graphicsLayer` modifier at the page composable. It calculates the offset of that specific page relative to the current active page, and applies transformations.

```
 Card(
        modifier = modifier
            .graphicsLayer {
                val pageOffset = (
                        (pagerState.currentPage - thisPageIndex) + pagerState
                            .currentPageOffsetFraction
                        )

                alpha = lerp(
                    start = 0.4f,
                    stop = 1f,
                    fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f),
                )

                cameraDistance = 8 * density
                rotationY = lerp(
                    start = 0f,
                    stop = 40f,
                    fraction = pageOffset.coerceIn(-1f, 1f),
                )

                lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f),
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }
            }
    )
```

To make the page composable cleaner and not tied to the pager & animations, I have defined a custom `Modifier.pagerAnimation()` which is equivalent to the code above. You can find it at `com.rwmobi.composepager.ui.PagerAnimationModifier`. 

To make the coupling looser, as the best practice, the `PageLayout` composable has a `modifier` parameter, so we only have to apply the `pagerAnimation` modifier when calling it from the `HorizontalPager()`, without a need to pass the `pagerState` to the `PageLayout`.

&nbsp;

&nbsp;&nbsp;

## Haptic Feedback

Let's try `CompositionLocal`! We can perform haptic feedback in two lines of code.

The following `LaunchedEffect` can perform haptic feedback during a page-change event. You may do some extra work related to the page change within the same collector. 


```
    var currentPageIndex by remember { mutableStateOf(0) }
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { currentPage ->
            // This is required to avoid the trigger when the pager is first loaded
            if (currentPageIndex != currentPage) {
                hapticFeedback.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
                currentPageIndex = currentPage
            }
            // Anything to be triggered by page-change can be done here
        }
    }
```

The `snapshotFlow` approach was recommended by the previous Accompanist documentation.

&nbsp;
&nbsp;

## Endless Pager

By manipulating the `pagerState`, we can make the pager scroll endlessly. We simply multiply the original number of pages by a relatively large number, set the `initialPage` to around the middle of the range, and then, when we need to resolve the index for contents, we take the remainder of the multiplied page index divided by the actual number of items, and we are good to go.

```
    val endlessPagerMultiplier = 1000
    val pageCount = endlessPagerMultiplier * drawables.size
    val initialPage = pageCount / 2

    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f,
        pageCount = { pageCount },
    )
    
    ...
    
    val resolvedPageContentIndex = absolutePageIndex % drawables.size
```


## Let's download and run it!

This project was configured to build using Android Studio Iguana | 2023.2.1. You will need to have Java 17 to build the project.

Alternatively, you can find the ready-to-install APKs and App Bundles under the [release section](https://github.com/ryanw-mobile/compose-pager-demo/releases).

## Technical details

### Dependencies

* [AndroidX Core KTX](https://developer.android.com/jetpack/androidx/releases/core) - Apache 2.0 - Extensions to Java APIs for Android development
* [JUnit](https://junit.org/junit5/) - EPL 2.0 - A simple framework to write repeatable tests
* [AndroidX Test Ext JUnit](https://developer.android.com/jetpack/androidx/releases/test) - Apache 2.0 - Extensions for Android testing
* [AndroidX Espresso](https://developer.android.com/training/testing/espresso) - Apache 2.0 - UI testing framework
* [AndroidX Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle) - Apache 2.0 - Lifecycles-aware components
* [Jetpack Compose](https://developer.android.com/jetpack/compose) - Apache 2.0 - Modern toolkit for building native UI
* [AndroidX Material3](https://developer.android.com/jetpack/androidx/releases/compose-material3) - Apache 2.0 - Material Design components for Jetpack Compose

### Plugins

* [Android Application Plugin](https://developer.android.com/studio/build/gradle-plugin-3-0-0-migration) - Google - Plugin for building Android applications
* [Jetbrains Kotlin Android Plugin](https://kotlinlang.org/docs/gradle.html) - JetBrains - Plugin for Kotlin Android projects
* [Ktlint Plugin](https://github.com/JLLeitschuh/ktlint-gradle) - JLLeitschuh - Plugin for Kotlin linter
