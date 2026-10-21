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

import Foundation
import OpenKuiklyA2UIIOSRender

/// Shared A2UI Icon registration used by every iosAppFor* target.
///
/// Once at least one icon is registered here, EVERY A2UI Icon component
/// (including those whose `name` is data-bound at runtime) renders via
/// KRImageView instead of the built-in unicode/emoji glyph. This demo
/// ships one PNG for every entry in the built-in ICON_MAP (63 icons + a
/// `_fallback` placeholder) under `Resources/a2ui_icons/`, so any name
/// declared by the A2UI protocol resolves to a concrete image; unknown
/// names still land on the reserved `_fallback` entry.
///
/// The images are loaded through `A2UIComponentExpandHandler`, which maps
/// `assets://…` URLs to `Bundle.main.pathForResource(...)`.
///
/// Mirrors the same registration list used by
/// `androidAppForKuiklyPage/A2UIDemoApplication`.
enum A2UIDemoIconRegistration {

    /// Register the demo's icon set. Safe to call from every AppDelegate's
    /// `application(_:didFinishLaunchingWithOptions:)`; A2UIIconRegistry is
    /// a process-global singleton, so repeated registration is a no-op
    /// beyond overwriting existing name → uri entries.
    static func register() {
        A2UISurfaceView.registerA2UIIcons([
            "accountCircle":     "assets://a2ui_icons/accountCircle.png",
            "add":               "assets://a2ui_icons/add.png",
            "arrowBack":         "assets://a2ui_icons/arrowBack.png",
            "arrowForward":      "assets://a2ui_icons/arrowForward.png",
            "arrowUpward":       "assets://a2ui_icons/arrowUpward.png",
            "attachFile":        "assets://a2ui_icons/attachFile.png",
            "calendarToday":     "assets://a2ui_icons/calendarToday.png",
            "call":              "assets://a2ui_icons/call.png",
            "camera":            "assets://a2ui_icons/camera.png",
            "check":             "assets://a2ui_icons/check.png",
            "close":             "assets://a2ui_icons/close.png",
            "delete":            "assets://a2ui_icons/delete.png",
            "directionsRun":     "assets://a2ui_icons/directionsRun.png",
            "download":          "assets://a2ui_icons/download.png",
            "edit":              "assets://a2ui_icons/edit.png",
            "event":             "assets://a2ui_icons/event.png",
            "error":             "assets://a2ui_icons/error.png",
            "fastForward":       "assets://a2ui_icons/fastForward.png",
            "favorite":          "assets://a2ui_icons/favorite.png",
            "favoriteOff":       "assets://a2ui_icons/favoriteOff.png",
            "folder":            "assets://a2ui_icons/folder.png",
            "help":              "assets://a2ui_icons/help.png",
            "home":              "assets://a2ui_icons/home.png",
            "info":              "assets://a2ui_icons/info.png",
            "locationOn":        "assets://a2ui_icons/locationOn.png",
            "lock":              "assets://a2ui_icons/lock.png",
            "lockOpen":          "assets://a2ui_icons/lockOpen.png",
            "mail":              "assets://a2ui_icons/mail.png",
            "menu":              "assets://a2ui_icons/menu.png",
            "moreVert":          "assets://a2ui_icons/moreVert.png",
            "moreHoriz":         "assets://a2ui_icons/moreHoriz.png",
            "notificationsOff":  "assets://a2ui_icons/notificationsOff.png",
            "notifications":     "assets://a2ui_icons/notifications.png",
            "pause":             "assets://a2ui_icons/pause.png",
            "payment":           "assets://a2ui_icons/payment.png",
            "person":            "assets://a2ui_icons/person.png",
            "priorityHigh":      "assets://a2ui_icons/priorityHigh.png",
            "phone":             "assets://a2ui_icons/phone.png",
            "photo":             "assets://a2ui_icons/photo.png",
            "play":              "assets://a2ui_icons/play.png",
            "print":             "assets://a2ui_icons/print.png",
            "refresh":           "assets://a2ui_icons/refresh.png",
            "rewind":            "assets://a2ui_icons/rewind.png",
            "search":            "assets://a2ui_icons/search.png",
            "send":              "assets://a2ui_icons/send.png",
            "settings":          "assets://a2ui_icons/settings.png",
            "share":             "assets://a2ui_icons/share.png",
            "shoppingCart":      "assets://a2ui_icons/shoppingCart.png",
            "skipNext":          "assets://a2ui_icons/skipNext.png",
            "skipPrevious":      "assets://a2ui_icons/skipPrevious.png",
            "star":              "assets://a2ui_icons/star.png",
            "starHalf":          "assets://a2ui_icons/starHalf.png",
            "starOff":           "assets://a2ui_icons/starOff.png",
            "stop":              "assets://a2ui_icons/stop.png",
            "trendingUp":        "assets://a2ui_icons/trendingUp.png",
            "upload":            "assets://a2ui_icons/upload.png",
            "visibility":        "assets://a2ui_icons/visibility.png",
            "visibilityOff":     "assets://a2ui_icons/visibilityOff.png",
            "volumeDown":        "assets://a2ui_icons/volumeDown.png",
            "volumeMute":        "assets://a2ui_icons/volumeMute.png",
            "volumeOff":         "assets://a2ui_icons/volumeOff.png",
            "volumeUp":          "assets://a2ui_icons/volumeUp.png",
            "warning":           "assets://a2ui_icons/warning.png",
            A2UISurfaceView.iconFallbackKey: "assets://a2ui_icons/_fallback.png"
        ])
    }
}
