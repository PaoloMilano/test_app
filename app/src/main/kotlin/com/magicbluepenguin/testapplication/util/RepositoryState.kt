package com.magicbluepenguin.testapplication.util

sealed class RepositoryState

class IsFetchingMoreItems(val value: Boolean) : RepositoryState() {
    override fun equals(other: Any?): Boolean {
        return other is IsFetchingMoreItems && other.value == value
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
