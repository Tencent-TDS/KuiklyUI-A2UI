#!/usr/bin/env python3
"""
Generate a professional architecture diagram for Kuikly A2UI.
Requirements: pip install matplotlib
"""

import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch
import matplotlib.font_manager as fm
import os


def get_cjk_font():
    """Find a CJK-capable font on the system."""
    # macOS system fonts that support CJK
    candidates = [
        '/System/Library/Fonts/PingFang.ttc',
        '/System/Library/Fonts/STHeiti Light.ttc',
        '/System/Library/Fonts/Hiragino Sans GB.ttc',
        '/Library/Fonts/Arial Unicode.ttf',
    ]
    for path in candidates:
        if os.path.exists(path):
            return fm.FontProperties(fname=path)
    # Fallback: try to find any CJK font
    for f in fm.fontManager.ttflist:
        if 'PingFang' in f.name or 'Heiti' in f.name or 'Noto Sans CJK' in f.name:
            return fm.FontProperties(fname=f.fname)
    return fm.FontProperties()


def create_architecture_diagram():
    cjk_font = get_cjk_font()

    fig, ax = plt.subplots(1, 1, figsize=(6, 5.2), dpi=200)
    ax.set_xlim(0.5, 11.5)
    ax.set_ylim(2.4, 13.8)
    ax.axis('off')
    fig.patch.set_facecolor('#FFFFFF')

    # Color scheme
    colors = {
        'server': '#4A90D9',
        'core': '#E8833A',
        'native': '#50B86C',
        'arrow': '#555555',
        'text_white': '#FFFFFF',
        'text_dark': '#333333',
        'subtitle': '#666666',
    }

    # Title
    ax.text(6, 13.5, 'Kuikly A2UI Architecture', fontsize=12, fontweight='semibold',
            ha='center', va='center', color=colors['text_dark'],
            fontfamily='sans-serif')
    ax.text(6, 13.05, u'\u534f\u8bae \u2192 \u6d88\u606f \u2192 DSL \u2192 \u539f\u751f\u89c6\u56fe',
            fontsize=6.5, ha='center', va='center', color=colors['subtitle'],
            fontproperties=cjk_font, style='italic')

    # ========== Layer 1: Server / LLM / Agent ==========
    box1 = FancyBboxPatch((1.5, 10.5), 9, 1.8, boxstyle="round,pad=0.15",
                          facecolor=colors['server'], edgecolor='#3A7BC0',
                          linewidth=0.8, alpha=0.95)
    ax.add_patch(box1)

    ax.text(6, 11.7, 'Server / LLM / Agent', fontsize=9, fontweight='medium',
            ha='center', va='center', color=colors['text_white'],
            fontfamily='sans-serif')
    ax.text(6, 11.1, u'\u751f\u6210 JSON \u6d41 (JSONL)', fontsize=6,
            ha='center', va='center', color='#D4E6F9',
            fontproperties=cjk_font)

    # ========== Arrow 1: JSONL (box1 bottom=10.5, box2 top=9.8) ==========
    ax.annotate('', xy=(6, 9.8), xytext=(6, 10.5),
                arrowprops=dict(arrowstyle='->', color=colors['arrow'],
                               lw=1.0))
    ax.text(6.3, 10.15, u'JSONL \u6d41', fontsize=6, ha='left', va='center',
            color=colors['arrow'], fontweight='medium', fontproperties=cjk_font)

    # ========== Layer 2: A2UI Core (Kotlin Multiplatform) ==========
    box2 = FancyBboxPatch((1.5, 5.8), 9, 4.0, boxstyle="round,pad=0.15",
                          facecolor='#FFF3E8', edgecolor=colors['core'],
                          linewidth=0.8, alpha=0.95)
    ax.add_patch(box2)

    ax.text(6, 9.4, 'A2UI Core', fontsize=9, fontweight='medium',
            ha='center', va='center', color=colors['core'],
            fontfamily='sans-serif')
    ax.text(6, 9.0, u'Kotlin \u8de8\u5e73\u53f0', fontsize=6,
            ha='center', va='center', color='#B85C1A',
            fontproperties=cjk_font)

    # Pipeline boxes
    pipeline_y = 8.2
    pipeline_boxes = [
        ('StreamParser', 2.2, 2.2),
        ('Adapter', 4.8, 2.0),
        ('Applier', 7.2, 2.0),
    ]

    for (label, x, w) in pipeline_boxes:
        inner_box = FancyBboxPatch((x, pipeline_y - 0.35), w, 0.7,
                                   boxstyle="round,pad=0.08",
                                   facecolor=colors['core'],
                                   edgecolor='#C06A28', linewidth=0.6)
        ax.add_patch(inner_box)
        ax.text(x + w / 2, pipeline_y, label, fontsize=6.5, fontweight='medium',
                ha='center', va='center', color=colors['text_white'],
                fontfamily='sans-serif')

    # Arrows between pipeline boxes
    ax.annotate('', xy=(4.75, pipeline_y), xytext=(4.4, pipeline_y),
                arrowprops=dict(arrowstyle='->', color=colors['arrow'], lw=0.8))
    ax.annotate('', xy=(7.15, pipeline_y), xytext=(6.8, pipeline_y),
                arrowprops=dict(arrowstyle='->', color=colors['arrow'], lw=0.8))

    # Down arrow inside core (pipeline bottom=7.85, dsl_box top=7.0)
    ax.annotate('', xy=(6, 7.0), xytext=(6, 7.85),
                arrowprops=dict(arrowstyle='->', color=colors['arrow'], lw=0.8))

    # Kuikly DSL Component Tree box
    dsl_box = FancyBboxPatch((2.5, 6.2), 7, 0.8, boxstyle="round,pad=0.08",
                             facecolor='#FDEBD0', edgecolor='#D4883A',
                             linewidth=0.6)
    ax.add_patch(dsl_box)
    ax.text(6, 6.6, u'Kuikly DSL \u7ec4\u4ef6\u6811 (FlexBox \u5e03\u5c40)', fontsize=6.5,
            fontweight='medium', ha='center', va='center', color='#8B4513',
            fontproperties=cjk_font)

    # ========== Arrow 2: NativeBridge (box2 bottom=5.8, box3 top=5.0) ==========
    ax.annotate('', xy=(6, 5.0), xytext=(6, 5.8),
                arrowprops=dict(arrowstyle='->', color=colors['arrow'], lw=1.0))
    ax.text(6.3, 5.4, u'\u539f\u751f\u6865\u63a5', fontsize=6, ha='left', va='center',
            color=colors['arrow'], fontweight='medium', fontproperties=cjk_font)

    # ========== Layer 3: Native View Tree ==========
    box3 = FancyBboxPatch((1.5, 2.8), 9, 2.2, boxstyle="round,pad=0.15",
                          facecolor='#E8F8ED', edgecolor=colors['native'],
                          linewidth=0.8, alpha=0.95)
    ax.add_patch(box3)

    ax.text(6, 4.55, u'\u539f\u751f\u89c6\u56fe\u6811', fontsize=9, fontweight='medium',
            ha='center', va='center', color='#2D8B4E',
            fontproperties=cjk_font)
    ax.text(6, 4.1, u'\u4e09\u7aef\u4e00\u81f4\u6e32\u67d3', fontsize=6,
            ha='center', va='center', color='#5A9E72',
            fontproperties=cjk_font)

    # Platform boxes (no emoji, use text only)
    platforms = [
        ('Android', 3.0, 3.35, '#3DDC84', '#1B5E20'),
        ('iOS', 6.0, 3.35, '#007AFF', '#003D99'),
        ('HarmonyOS', 9.0, 3.35, '#E53935', '#7F0000'),
    ]

    for (label, x, y, bg_color, text_color) in platforms:
        platform_box = FancyBboxPatch((x - 1.1, y - 0.3), 2.2, 0.6,
                                      boxstyle="round,pad=0.08",
                                      facecolor=bg_color,
                                      edgecolor=bg_color,
                                      linewidth=0.5, alpha=0.2)
        ax.add_patch(platform_box)
        ax.text(x, y, label, fontsize=7, fontweight='medium',
                ha='center', va='center', color=text_color,
                fontfamily='sans-serif')



    # ========== Right side annotations (Chinese) ==========
    right_x = 11.2
    ax.text(right_x, 11.4, u'AI\n\u751f\u6210\u5c42', fontsize=7, ha='center',
            va='center', color=colors['server'], fontproperties=cjk_font, fontweight='medium')
    ax.text(right_x, 7.8, u'\u8de8\u5e73\u53f0\n\u6838\u5fc3\u903b\u8f91', fontsize=7,
            ha='center', va='center', color=colors['core'], fontproperties=cjk_font, fontweight='medium')
    ax.text(right_x, 3.9, u'\u5e73\u53f0\n\u539f\u751f\u5c42', fontsize=7, ha='center',
            va='center', color=colors['native'], fontproperties=cjk_font, fontweight='medium')

    # Save
    output_path = '/Users/etkm/work_kuikly_web/KuiklyUI-Mirror0527/a2ui/docs/a2ui_architecture.png'
    plt.subplots_adjust(left=0.02, right=0.98, top=0.98, bottom=0.02)
    plt.savefig(output_path, dpi=200, bbox_inches='tight', pad_inches=0.1,
                facecolor='white', edgecolor='none')
    plt.close()
    print(f"Architecture diagram saved to: {output_path}")
    return output_path


if __name__ == '__main__':
    create_architecture_diagram()
