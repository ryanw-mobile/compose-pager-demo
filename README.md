# Jetpack Compose Horizontal Pager Animation Demo

<p align="center">
  <img src="greggs.gif" width="400" />
</p>

This is a demo app demonstrating the official Jetpack Compose Horizontal Pager.

In this app, you can see how simple we can set up a Horizontal Pager, feed in whatever content we want, and apply animations.

No more custom views, adapters, fragments and complex lifecycle handling!

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

To make the page composable cleaner and not tied to the pager & animations, I have defined a custom `Modifier.pagerAnimation()` modifier which is equivalent to the code above at:
 `com.rwmobi.composepager.ui.PagerAnimationModifier`. 

To make the coupling looser, within the `HorizontalPager()`, we only have to apply this modifier to the pageContent composable, without a need to pass the pagerState to the page composable.

I am not a mathematician, and I no longer have some-mathematician-coworker 👨🏻‍🦲 with me to play with this. There might have room for improvement. Feel free to optimise everything here to meet your needs. 🙂

## Haptic Feedback

Let's try `CompositionLocal`! We can perform haptic feedback in two lines of code.

To following `LaunchedEffect` can perform haptic feedback during a page-change event. Within the same collector, you may do some extra work related to the page change. 

```
    var currentPageIndex by remember { mutableStateOf(0) }
    val hapticFeedback = LocalHapticFeedback.current
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { currentPage ->
            if (currentPageIndex != currentPage) {
                hapticFeedback.performHapticFeedback(hapticFeedbackType = HapticFeedbackType.LongPress)
                currentPageIndex = currentPage
            }
            // Anything to be triggered by page-change can be done here
        }
    }

```

The `snapshotFlow` approach was recommended by the previous Accompanist documentation.


## Just download and run it!

This project was created using Android Studio Giraffe | 2022.3.1 Canary 11. You will need to have Java 17 to run it in that case.
