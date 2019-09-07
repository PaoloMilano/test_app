package com.magicbluepenguin.testapplication.util

sealed class RepositoryState

class IsFetchingMoreRecentItems(val value: Boolean) : RepositoryState() {
    override fun equals(other: Any?): Boolean {
        return other is IsFetchingMoreRecentItems && other.value == value
    }

    override fun hashCode(): Int {
        return toString().hashCode() + super.hashCode()
    }
}

class IsFetchingMoreOlderItems(val value: Boolean) : RepositoryState() {
    override fun equals(other: Any?): Boolean {
        return other is IsFetchingMoreOlderItems && other.value == value
    }

    override fun hashCode(): Int {
        return toString().hashCode() + super.hashCode()
    }
}

class RefreshInProgress(val value: Boolean) : RepositoryState() {
    override fun equals(other: Any?): Boolean {
        return other is RefreshInProgress && other.value == value
    }

    override fun hashCode(): Int {
        return toString().hashCode() + super.hashCode()
    }
}

object NetworkError : RepositoryState()
