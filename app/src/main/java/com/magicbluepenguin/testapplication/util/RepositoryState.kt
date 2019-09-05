package com.magicbluepenguin.testapplication.util

sealed class RepositoryState

class IsFetchingMoreItems(val value: Boolean) : RepositoryState()

class RefreshInProgress(val value: Boolean) : RepositoryState()

object NetworkError : RepositoryState()
