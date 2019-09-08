package com.magicbluepenguin.testapplication.util

sealed class RepositoryState

class IsFetchingMoreRecentItems(val value: Boolean) : RepositoryState() {
    override fun equals(other: Any?): Boolean {
        return other is IsFetchingMoreRecentItems && other.value == value
    }
}

class IsFetchingMoreOlderItems(val value: Boolean) : RepositoryState() {
    override fun equals(other: Any?): Boolean {
        return other is IsFetchingMoreOlderItems && other.value == value
    }
}

class RefreshInProgress(val value: Boolean) : RepositoryState() {
    override fun equals(other: Any?): Boolean {
        return other is RefreshInProgress && other.value == value
    }
}

sealed class NetworkError : RepositoryState()

object GenericNetworkError : NetworkError()

object NetworkUnavailableError : NetworkError()
