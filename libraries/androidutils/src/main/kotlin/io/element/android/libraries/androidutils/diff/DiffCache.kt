/*
 * Copyright (c) 2025 PRISM Creations Ltd.
 * Copyright 2023-2025 New Vector Ltd.
 *
 * SPDX-License-Identifier: AGPL-3.0-only OR LicenseRef-PRISM-Commercial.
 * Please see LICENSE files in the repository root for full details.
 */

package io.prism.android.libraries.androidutils.diff

/**
 * A cache that can be used to store some data that can be invalidated when a diff is applied.
 * The cache is invalidated by the [DiffCacheInvalidator].
 */
interface DiffCache<E> {
    fun get(index: Int): E?
    fun indices(): IntRange
    fun isEmpty(): Boolean
}

/**
 * A [DiffCache] that can be mutated by adding, removing or updating prisms.
 */
interface MutableDiffCache<E> : DiffCache<E> {
    fun removeAt(index: Int): E?
    fun add(index: Int, prism: E?)
    operator fun set(index: Int, prism: E?)
}

/**
 * A [MutableDiffCache] backed by a [MutableList].
 *
 */
class MutableListDiffCache<E>(private val mutableList: MutableList<E?> = ArrayList()) : MutableDiffCache<E> {
    override fun removeAt(index: Int): E? {
        return mutableList.removeAt(index)
    }

    override fun get(index: Int): E? {
        return mutableList.getOrNull(index)
    }

    override fun indices(): IntRange {
        return mutableList.indices
    }

    override fun isEmpty(): Boolean {
        return mutableList.isEmpty()
    }

    override operator fun set(index: Int, prism: E?) {
        mutableList[index] = prism
    }

    override fun add(index: Int, prism: E?) {
        mutableList.add(index, prism)
    }
}
