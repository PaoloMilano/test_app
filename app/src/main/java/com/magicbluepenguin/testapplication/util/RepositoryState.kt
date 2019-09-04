package com.magicbluepenguin.testapplication.util

sealed class RepositoryState

class NetworkOperationInProgress(val value: Boolean) : RepositoryState()

class HasMoreItems(val value: Boolean) : RepositoryState()

object NetworkError : RepositoryState()
