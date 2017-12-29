## Improve scroll in nested Recycler Views
This piece of code aims to improve the scroll angle when 2 recyclers views are nested : a recycler view that contains other recycler views, just like the Play Store.

The objective is to change the scroll angle in the Parent Recycler View and override the onInterceptTouchEvent method. When this method return `false`, the parent recycler view will not get the touch event. Instead, the 
event will be dispatched to the underlying child view (another recycler view)

### Classic
The classic implementation seems to be less than 45° to allow an horizontal scroll.
<img src="https://raw.githubusercontent.com/MalikDE/NestedRecyclerView/master/doc/img/classic.gif" width="300">

### Improved
In the example bellow, angle is customized up to 70 degrees.
<img src="https://raw.githubusercontent.com/MalikDE/NestedRecyclerView/master/doc/img/imp.gif" width="300">
