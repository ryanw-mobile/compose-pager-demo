# Jetpack Compose Horizontal Pager Animation Demo ![Gradle Check on Main](https://github.com/ryanw-mobile/compose-pager-demo/actions/workflows/main_check.yml/badge.svg)

Complementary article: [Reasons to Love the New Jetpack Compose Pager](https://medium.com/@callmeryan/reasons-to-love-the-new-jetpack-compose-pager-a53366fb6906)

<p align="center">
  <img src="greggs.gif" width="400" />
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


## Just download and run it!

This project was created using Android Studio Iguana | 2023.2.1 Canary 18. You will need to have Java 17 to run it in that case.


## Some final words

I am not a mathematician, and I no longer have some-mathematician-coworker 👨🏻‍🦲 with me to play with this. There might have room for improvement. Feel free to optimise everything here to meet your needs. 🙂

I hope those who worked with me remember my meal deals contact list. 🙂 
