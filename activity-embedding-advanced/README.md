# Advanced Activity Embedding

The activity embedding sample app is the codebase of advanced activity
embedding codelab.

The codelab guides you step by step through the process of implementing 
activity embedding features:

* Pane expansion (interactive divider): allows apps to display a fixed or 
draggable divider between the two activities in a split presentation.
* ActivityStack Pinning: allows apps to pin the content in one container 
and have its navigation isolated from the other container.
* Fullscreen Dialog Dimming: allows apps to specify the dialog dim area,
to either dim the entire task window or only dim the container that shows
the dialog.


And you have the choice of working in Kotlin or Java.

## Background

Large displays enable app layouts and UIs that enhance the user experience and 
increase user productivity. But if your app is designed for the small displays 
of non-foldable phones, it probably doesn’t take advantage of the extra display 
space offered by tablets, foldables, and ChromeOS devices.

Activity embedding enables you to optimize your activity-based apps for large 
screens. Introduced in Android 12L (API level 32), activity embedding displays
multiple activities onscreen simultaneously to create two-pane layouts such as
list-detail.

All without refactoring any existing code!

Learn how in the activity embedding codelab.

