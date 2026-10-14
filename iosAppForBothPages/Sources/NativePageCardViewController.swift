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

import UIKit
import OpenA2UIIOSRender

/// Native Page single-card sample.
///
/// iOS mirror of `androidAppForBothPages/NativePageCardActivity`. Embeds an
/// [A2UISurfaceView] directly inside a plain UIKit view tree and pushes one
/// hand-crafted A2UI v0.9 weather-card payload through
/// `writeStream()` / `finishStream()`. No Kuikly Pager involved.
class NativePageCardViewController: UIViewController {

    /// Hand-crafted A2UI v0.9 weather-card envelopes. Kept identical (in
    /// intent) to the weather card used by the KuiklyPage's DSL entry, so
    /// users can eyeball that both integration paths render the same DSL.
    /// Split into two envelopes: createSurface + updateComponents.
    private static let CREATE_SURFACE_JSON = """
    {
      "version": "v0.9",
      "createSurface": { "surfaceId": "both-native-weather" }
    }
    """

    private static let UPDATE_COMPONENTS_JSON = """
    {
      "version": "v0.9",
      "updateComponents": {
        "surfaceId": "both-native-weather",
        "components": [
          {
            "id": "root", "component": "Card", "child": "outer",
            "styles": {
              "padding": 10,
              "backgroundLinearGradient": {
                "direction": "to-bottom",
                "colorStops": [
                  { "color": "#3A6DF0", "stop": 0.0 },
                  { "color": "#5B4BE1", "stop": 0.55 },
                  { "color": "#7A3FD8", "stop": 1.0 }
                ]
              }
            }
          },
          {
            "id": "outer", "component": "Column", "align": "stretch",
            "children": ["title", "subtitle", "hero", "detail"]
          },

          {
            "id": "title", "component": "Text", "text": "深圳 · 今日天气", "variant": "h3",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 15,
              "fontWeight": "medium",
              "letterSpacing": 1,
              "opacity": 0.85,
              "textAlign": "center",
              "marginTop": 8,
              "marginBottom": 0
            }
          },
          {
            "id": "subtitle", "component": "Text", "text": "周一 · 9月8日 · 体感 29°", "variant": "caption",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 11,
              "fontWeight": "normal",
              "letterSpacing": 0.5,
              "opacity": 0.6,
              "textAlign": "center",
              "marginTop": 0,
              "marginBottom": 2,
              "textShadow": {
                "offsetX": 0, "offsetY": 1, "radius": 2, "color": "#40000000"
              }
            }
          },

          {
            "id": "hero", "component": "Row", "align": "center",
            "children": ["temp", "cond"],
            "styles": {
              "marginTop": 0,
              "marginBottom": 4,
              "paddingLeft": 4,
              "paddingRight": 4
            }
          },
          {
            "id": "temp", "component": "Text", "text": "27°", "variant": "h1",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 84,
              "fontWeight": "light",
              "letterSpacing": -2,
              "flexGrow": 1,
              "marginTop": 0,
              "marginBottom": 0,
              "transform": {
                "scale": 1.02,
                "translate": [0, -0.02],
                "anchor": [0, 0.5]
              },
              "textShadow": {
                "offsetX": 0, "offsetY": 2, "radius": 12, "color": "#66000000"
              }
            }
          },
          {
            "id": "cond", "component": "Text", "text": "多云转晴", "variant": "body",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 17,
              "fontWeight": "medium",
              "opacity": 0.9,
              "textAlign": "right",
              "flexGrow": 1,
              "marginTop": 0,
              "marginBottom": 0,
              "textShadow": {
                "offsetX": 0, "offsetY": 1, "radius": 4, "color": "#4D000000"
              }
            }
          },

          {
            "id": "detail", "component": "Row", "justify": "spaceBetween",
            "children": ["hum", "wind", "aqi"],
            "styles": {
              "marginTop": 4,
              "paddingTop": 0
            }
          },

          {
            "id": "hum", "component": "Row", "align": "center",
            "children": ["humText"],
            "styles": {
              "backgroundColor": "#22FFFFFF",
              "borderRadius": 8,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 3,
              "paddingBottom": 3,
              "marginLeft": 8,
              "marginTop": 0,
              "marginBottom": 8,
              "boxShadow": {
                "offsetX": 0, "offsetY": 2, "radius": 6, "color": "#33000000", "fill": false
              }
            }
          },
          {
            "id": "humText", "component": "Text", "text": "湿度 68%", "variant": "caption",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 12,
              "fontWeight": "medium",
              "opacity": 0.9,
              "marginTop": 0,
              "marginBottom": 0
            }
          },

          {
            "id": "wind", "component": "Row", "align": "center",
            "children": ["windText"],
            "styles": {
              "backgroundColor": "#22FFFFFF",
              "borderRadius": 8,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 3,
              "paddingBottom": 3,
              "marginTop": 0,
              "marginBottom": 8,
              "boxShadow": {
                "offsetX": 0, "offsetY": 2, "radius": 6, "color": "#33000000", "fill": false
              }
            }
          },
          {
            "id": "windText", "component": "Text", "text": "东南风 3 级", "variant": "caption",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 12,
              "fontWeight": "medium",
              "opacity": 0.9,
              "marginTop": 0,
              "marginBottom": 0
            }
          },

          {
            "id": "aqi", "component": "Row", "align": "center",
            "children": ["aqiText"],
            "styles": {
              "backgroundColor": "#22FFFFFF",
              "borderRadius": 8,
              "paddingLeft": 10,
              "paddingRight": 10,
              "paddingTop": 3,
              "paddingBottom": 3,
              "marginRight": 8,
              "marginTop": 0,
              "marginBottom": 8,
              "boxShadow": {
                "offsetX": 0, "offsetY": 2, "radius": 6, "color": "#33000000", "fill": false
              }
            }
          },
          {
            "id": "aqiText", "component": "Text", "text": "空气 良 · 58", "variant": "caption",
            "styles": {
              "color": "#FFFFFF",
              "fontSize": 12,
              "fontWeight": "medium",
              "opacity": 0.9,
              "marginTop": 0,
              "marginBottom": 0
            }
          }
        ]
      }
    }
    """

    private var surfaceView: A2UISurfaceView?

    override func viewDidLoad() {
        super.viewDidLoad()
        title = "Native 页面接入示例"
        view.backgroundColor = UIColor(red: 0.96, green: 0.96, blue: 0.96, alpha: 1.0)

        let header = UIView()
        header.backgroundColor = UIColor(red: 0.27, green: 0.35, blue: 0.39, alpha: 1.0)
        header.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(header)

        let headerTitle = UILabel()
        headerTitle.text = "Native 页面接入示例"
        headerTitle.font = .boldSystemFont(ofSize: 20)
        headerTitle.textColor = .white
        headerTitle.translatesAutoresizingMaskIntoConstraints = false
        header.addSubview(headerTitle)

        let headerSubtitle = UILabel()
        headerSubtitle.text = "宿主：Native UIViewController + A2UISurfaceView"
        headerSubtitle.font = .systemFont(ofSize: 11)
        headerSubtitle.textColor = UIColor(red: 0.81, green: 0.85, blue: 0.86, alpha: 1.0)
        headerSubtitle.translatesAutoresizingMaskIntoConstraints = false
        header.addSubview(headerSubtitle)

        let cardWrap = UIView()
        cardWrap.backgroundColor = .white
        cardWrap.layer.cornerRadius = 8
        cardWrap.layer.masksToBounds = true
        cardWrap.translatesAutoresizingMaskIntoConstraints = false
        view.addSubview(cardWrap)

        let surface = A2UISurfaceView()
        surface.translatesAutoresizingMaskIntoConstraints = false
        cardWrap.addSubview(surface)

        NSLayoutConstraint.activate([
            header.topAnchor.constraint(equalTo: view.topAnchor),
            header.leadingAnchor.constraint(equalTo: view.leadingAnchor),
            header.trailingAnchor.constraint(equalTo: view.trailingAnchor),

            headerTitle.topAnchor.constraint(equalTo: header.safeAreaLayoutGuide.topAnchor, constant: 12),
            headerTitle.leadingAnchor.constraint(equalTo: header.leadingAnchor, constant: 24),

            headerSubtitle.topAnchor.constraint(equalTo: headerTitle.bottomAnchor, constant: 4),
            headerSubtitle.leadingAnchor.constraint(equalTo: header.leadingAnchor, constant: 24),
            headerSubtitle.trailingAnchor.constraint(equalTo: header.trailingAnchor, constant: -24),
            headerSubtitle.bottomAnchor.constraint(equalTo: header.bottomAnchor, constant: -16),

            cardWrap.topAnchor.constraint(equalTo: header.bottomAnchor, constant: 12),
            cardWrap.leadingAnchor.constraint(equalTo: view.leadingAnchor, constant: 12),
            cardWrap.trailingAnchor.constraint(equalTo: view.trailingAnchor, constant: -12),

            surface.topAnchor.constraint(equalTo: cardWrap.topAnchor),
            surface.leadingAnchor.constraint(equalTo: cardWrap.leadingAnchor),
            surface.trailingAnchor.constraint(equalTo: cardWrap.trailingAnchor),
            surface.bottomAnchor.constraint(equalTo: cardWrap.bottomAnchor)
        ])

        surface.initSurface(configJson: "{}")
        surface.surfaceListener = self
        // Feed the two pre-built v0.9 envelopes directly. Each writeStream()
        // call must be a single JSON object per the A2UI protocol.
        surface.writeStream(Self.CREATE_SURFACE_JSON)
        surface.writeStream(Self.UPDATE_COMPONENTS_JSON)
        surface.finishStream()
        surfaceView = surface
    }

    deinit {
        surfaceView?.destroy()
        surfaceView = nil
    }
}

extension NativePageCardViewController: A2UISurfaceViewDelegate {

    func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didReceiveAction action: String, fromTag tag: Int, inSurface surfaceId: String) {
        print("[NativePageOneCard] didReceiveAction tag=\(tag) action=\(action)")
    }

    func a2uiSurfaceView(_ surfaceView: A2UISurfaceView, didEncounterError message: String) {
        print("[NativePageOneCard] didEncounterError message=\(message)")
    }
}
