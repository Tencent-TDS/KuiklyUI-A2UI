/*
 * Tencent is pleased to support the open source community by making KuiklyUI
 * available.
 * Copyright (C) 2025 Tencent. All rights reserved.
 * Licensed under the License of KuiklyUI;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://github.com/Tencent-TDS/KuiklyUI-A2UI/blob/main/LICENSE
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tencent.kuikly.a2ui.render.android

import android.content.Context
import com.tencent.kuikly.core.render.android.IKuiklyRenderExport
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderShadowExport
import com.tencent.kuikly.core.render.android.export.IKuiklyRenderViewExport
import java.util.Collections
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

/**
 * Process-global registry for user-supplied A2UI custom render views on Android.
 *
 * This registry is shared by:
 *  - [A2UISurfaceView] — the standalone native-page host. It registers its own
 *    per-surface [IKuiklyRenderExport] on init, replays existing entries, and
 *    listens for future changes.
 *  - [A2UIKuiklyBaseViewSupport] — the Kuikly-embedded-page host. It wraps
 *    the user-supplied `KuiklyRenderViewBaseDelegatorDelegate` and, when the
 *    core framework calls back with `registerExternalRenderView(export)`,
 *    installs every currently-registered entry onto that export **and** keeps
 *    a weak reference to the export so subsequent register / unregister events
 *    fan-out to it automatically.
 *  - [A2UIAndroidNativeMethodHandler] — bridges register / unregister events
 *    into the KMP-side [com.tencent.kuikly.a2ui.dsl.A2UICustomViewRegistry] so
 *    that the DSL applier can distinguish custom components in both modes.
 *
 * Registration is process-global and idempotent; every host sees the same
 * table so an app only needs to register a custom view once during startup.
 *
 * Threading: this object is fully synchronised via `ConcurrentHashMap` /
 * `Collections.synchronizedSet`. Callers do not need to hold any external
 * lock, and change listeners may fire on the caller's thread.
 */
object A2UIAndroidCustomViewRegistry {

    /**
     * A single registration entry — the view factory plus an optional shadow
     * factory (for text-like components that need JVM-side measurement).
     */
    data class Entry(
        val creator: (Context) -> IKuiklyRenderViewExport,
        val shadowCreator: (() -> IKuiklyRenderShadowExport)?
    )

    /** viewName → entry. */
    private val entries: MutableMap<String, Entry> = ConcurrentHashMap()

    /**
     * Live [IKuiklyRenderExport]s the wrap-delegate has seen. Weak so we don't
     * hold Kuikly page instances alive. Set-of-map-keys pattern (à la
     * `Collections.newSetFromMap`) so removal semantics match a regular Set.
     */
    private val attachedExports: MutableSet<IKuiklyRenderExport> =
        Collections.newSetFromMap(WeakHashMap<IKuiklyRenderExport, Boolean>())

    /**
     * Listener notified when the process-global registry changes. Used by
     * [A2UISurfaceView] (native-page mode) to fan-out to every live surface's
     * Kotlin core, and by [A2UIAndroidNativeMethodHandler] (Kuikly-embedded
     * mode) to bridge register events into the KMP-side registry.
     */
    interface ChangeListener {
        fun onRegister(viewName: String, entry: Entry)
        fun onUnregister(viewName: String)
    }

    private val listeners: MutableSet<ChangeListener> =
        Collections.synchronizedSet(mutableSetOf())

    // region public API

    /**
     * Register a custom render view **globally**. Existing hosts (both native-
     * page A2UISurfaceView and Kuikly-embedded A2UISurfaceDSLView) pick up the
     * new view immediately via the ChangeListener fan-out below.
     */
    fun register(
        viewName: String,
        creator: (Context) -> IKuiklyRenderViewExport,
        shadowCreator: (() -> IKuiklyRenderShadowExport)? = null
    ) {
        if (viewName.isEmpty()) return
        val entry = Entry(creator, shadowCreator)
        entries[viewName] = entry
        // Fan-out to every already-attached host [IKuiklyRenderExport]. Doing
        // this here (rather than only inside ChangeListener) means an
        // A2UIKuiklyBaseViewSupport host that has *already* completed
        // registerExternalRenderView still sees the new viewName without
        // requiring its own listener.
        val exportSnapshot = synchronized(attachedExports) { attachedExports.toList() }
        for (export in exportSnapshot) {
            installEntry(export, viewName, entry)
        }
        val listenerSnapshot = synchronized(listeners) { listeners.toList() }
        for (l in listenerSnapshot) l.onRegister(viewName, entry)
    }

    fun unregister(viewName: String) {
        if (viewName.isEmpty()) return
        entries.remove(viewName) ?: return
        // Note: we intentionally do NOT try to remove the factory from
        // previously-installed exports. IKuiklyRenderExport does not expose
        // a public "remove" operation, and any surfaces already using the
        // view would fault mid-render. Unregister-after-use is best-effort:
        // it prevents future register cycles from re-inserting the entry
        // and it forwards the KMP-side unregister so newly-created surfaces
        // treat the name as unknown again.
        val listenerSnapshot = synchronized(listeners) { listeners.toList() }
        for (l in listenerSnapshot) l.onUnregister(viewName)
    }

    /** Snapshot of the current entries; safe to iterate on any thread. */
    fun snapshot(): List<Pair<String, Entry>> =
        entries.entries.map { it.key to it.value }

    /** Return the names currently registered. */
    fun viewNames(): List<String> = entries.keys.toList()

    fun get(viewName: String): Entry? = entries[viewName]

    fun isRegistered(viewName: String): Boolean = entries.containsKey(viewName)

    fun addChangeListener(listener: ChangeListener) {
        listeners.add(listener)
    }

    fun removeChangeListener(listener: ChangeListener) {
        listeners.remove(listener)
    }

    // endregion

    // region attached-exports (used by A2UIKuiklyBaseViewSupport)

    /**
     * Attach a host [IKuiklyRenderExport] to this registry so that:
     *   1. every entry currently in the table is installed on it immediately;
     *   2. any subsequent [register] call also fans out to it.
     *
     * Called by [A2UIKuiklyBaseViewSupport] from the wrap-delegate's
     * `registerExternalRenderView(export)` hook, i.e. once per Kuikly page
     * initialisation, before the KMP core issues any `createRenderView(name, …)`.
     *
     * Idempotent — attaching the same export twice is a no-op.
     */
    fun attachExport(export: IKuiklyRenderExport) {
        val added = synchronized(attachedExports) { attachedExports.add(export) }
        if (!added) return
        // Replay every existing entry onto the freshly-attached export.
        for ((name, entry) in snapshot()) {
            installEntry(export, name, entry)
        }
    }

    /**
     * Reverse of [attachExport]. Not strictly required — the WeakHashMap
     * cleans up on GC — but exposed for tests / explicit teardown.
     */
    fun detachExport(export: IKuiklyRenderExport) {
        synchronized(attachedExports) { attachedExports.remove(export) }
    }

    // endregion

    private fun installEntry(export: IKuiklyRenderExport, name: String, entry: Entry) {
        // IKuiklyRenderExport is a plain map wrapper: put is safe to call at
        // any time and lookups by createRenderView / createRenderShadow are
        // per-request. Duplicated puts simply replace the previous factory.
        export.renderViewExport(name, entry.creator, entry.shadowCreator)
    }
}
