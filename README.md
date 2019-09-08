A few points about this app:

   - I used the paging library from Android Jetpack. This works quite well but it does seem to interrupt scrolling when new elements are added to the list. I would probably opt not to use this library again unless I found a way to address this.

   - Showing refresh status is also something that would need to be redesigned. Current backend response times are oftern too short to fully display progress to the user. Lacking this visual cue, the user may be left unaware that anything happened.

   - I tried to provide tests for every area that contains non-trivial logic. I only completed the main assignment and skipped the optional functionality, chosing robustness over quantity.

Also, please find the APK here: https://github.com/PaoloMilano/test_app/blob/master/test_app.apk
