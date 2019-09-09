A few points about this app:

   - I used the paging library from Android Jetpack hoping to create a more resource efficient solution that does not rely on calling `notifyDataSetChanged` multiple times or keeping the entire item list in memory the whole time. This worked quite well but it did seem to have a problem when loading more items as the user approaches the bottom of the list. When this happens in fact the `RecyclerView` does not keep scrolling to show the new items as it would when using `notifyDataSetChanged`. 
   On digging deeper I found that it is possibile to set a `BoundaryCallback` on the data source that fires events when the top or the bottom items are "loaded". Note however that list items are loaded from the data source much sooner that they's actually bound to a view in the UI - and they only appear on the UI if the user scrolls to them. Relying on this callback would make loading an infinite list from network look seemless to the user - especially when combined with placeholders. It would however not satisfy the requirement of showing a visual cue to the user as more data is being fetched.  
   The difference between the 2 solutions seems rather clear to me. The first was cobbled together to overcome the lack of support in the platform, while the second provides the platform support that has been lacking so far. It is for this reason that moving forward I'd suggest using the provided callbacks instead of relying on showing progress.
   
   - Showing refresh status is also something that would need to be redesigned. Current backend response times are oftern too short to fully display progress to the user. Lacking this visual cue, the user may be left unaware that anything happened.

   - I tried to provide tests for every area that contains non-trivial logic. I only completed the main assignment and skipped the optional functionality, chosing robustness over quantity.

Also, please find the APK here: https://github.com/PaoloMilano/test_app/blob/master/test_app.apk
