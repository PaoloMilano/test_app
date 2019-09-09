A few points about this app:

   
   - I tried to provide tests for every area that contains non-trivial logic. I only completed the main assignment and skipped the optional functionality, chosing robustness over quantity.

   - I used the paging library from Android Jetpack in order to more efficiently manage list items. This seemed to work quite well at the beginning but eventually presented a problem when I added functions to fetch more items from the top/bottom of the list. In particular I found it hard to display progress. At the same time the `RecyclerView` did not keep scrolling to show the newly added items as it normally would if had I just managed the list myself and called `notifyDataSetChanged` after each update.
   On digging deeper I found that it is possibile to set a `BoundaryCallback` on the data source that fires events when the top/bottom items are "loaded". However, it is important to note that list items are loaded from the data source much earlier than they're actually bound to a view in the UI - and they only appear in the UI if the user scrolls to them. So while I thought of trying this, is also clear that such solution would have not satisfied the requirement of fetching more items when a specific element is displayed. It would have also been difficult to display progress since - given the low latency of the provided API - the data would have already been available by the time the use scrolled to it. 
   At the end of my investigation I must therefore conclude that the pattern suggested (fetch after item at position + show progress) is effectively obsolete. The platform now supports displaying infinite list that appear seamles to user while doing all the heavy work in the background. If I had the possibility to work on this further I'd suggest an approach that relies on callbacks/placeholders to achieve this.

Also, please find the APK here: https://github.com/PaoloMilano/test_app/blob/master/test_app.apk
