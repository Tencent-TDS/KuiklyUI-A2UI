#!/usr/bin/env python3
"""
Generate a professional composite showcase image for A2UI component demos.
Combines 18 component screenshots into a single polished PNG.
Requirements: pip install Pillow
"""

from PIL import Image, ImageDraw, ImageFilter, ImageFont
import os
import math

def add_rounded_corners(img, radius):
    """Add rounded corners to an image."""
    mask = Image.new('L', img.size, 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle([(0, 0), img.size], radius=radius, fill=255)
    result = img.copy()
    result.putalpha(mask)
    return result

def add_phone_frame(img, corner_radius=20, border_width=3, border_color=(200, 200, 200)):
    """Add a subtle phone-like frame with rounded corners and thin border."""
    # Add rounded corners
    img_rgba = img.convert('RGBA')
    img_rounded = add_rounded_corners(img_rgba, corner_radius)

    # Create a slightly larger canvas for the border
    border_canvas = Image.new('RGBA',
                              (img.width + border_width * 2, img.height + border_width * 2),
                              (0, 0, 0, 0))

    # Draw border background
    draw = ImageDraw.Draw(border_canvas)
    draw.rounded_rectangle(
        [(0, 0), (border_canvas.width - 1, border_canvas.height - 1)],
        radius=corner_radius + border_width,
        fill=(*border_color, 255)
    )

    # Paste the rounded image on top
    border_canvas.paste(img_rounded, (border_width, border_width), img_rounded)

    return border_canvas

def add_shadow(img, offset=(8, 8), shadow_color=(0, 0, 0, 60), blur_radius=15):
    """Add a drop shadow to an image."""
    # Create shadow layer
    shadow_size = (img.width + blur_radius * 4, img.height + blur_radius * 4)
    shadow = Image.new('RGBA', shadow_size, (0, 0, 0, 0))

    # Place shadow shape
    shadow_offset_x = blur_radius * 2 + offset[0]
    shadow_offset_y = blur_radius * 2 + offset[1]
    shadow.paste(
        Image.new('RGBA', img.size, shadow_color),
        (shadow_offset_x, shadow_offset_y)
    )

    # Blur the shadow
    shadow = shadow.filter(ImageFilter.GaussianBlur(blur_radius))

    # Composite original on top
    paste_x = blur_radius * 2
    paste_y = blur_radius * 2
    shadow.paste(img, (paste_x, paste_y), img)

    return shadow, (paste_x, paste_y)

def create_showcase():
    """Create the composite showcase image."""
    # Configuration
    source_dir = '/Users/etkm/work_kuikly_web/__bak/a2ui_demo截图/组件截图'
    output_path = '/Users/etkm/work_kuikly_web/KuiklyUI-Mirror0527/a2ui/docs/a2ui_component_showcase.png'

    # Layout: 6 columns x 3 rows
    cols = 6
    rows = 3

    # Thumbnail size (scale down from 1290x2796)
    thumb_width = 280
    thumb_height = int(thumb_width * 2796 / 1290)  # ~607

    # Spacing and padding
    h_spacing = 30  # horizontal spacing between thumbnails
    v_spacing = 30  # vertical spacing between thumbnails
    padding_x = 60  # left/right padding
    padding_y = 80  # top/bottom padding (top has title)
    title_height = 80  # space for title

    # Background color - clean light gray gradient feel
    bg_color = (248, 249, 252)

    # Calculate canvas size
    canvas_width = padding_x * 2 + cols * thumb_width + (cols - 1) * h_spacing
    canvas_height = padding_y + title_height + rows * thumb_height + (rows - 1) * v_spacing + padding_y

    # Create canvas
    canvas = Image.new('RGB', (canvas_width, canvas_height), bg_color)
    draw = ImageDraw.Draw(canvas)

    # Add a subtle gradient overlay at the top
    for y in range(min(200, canvas_height)):
        alpha = int(15 * (1 - y / 200))
        draw.line([(0, y), (canvas_width, y)], fill=(230, 235, 245))

    # Re-fill with semi-transparent background to keep it subtle
    canvas_bg = Image.new('RGB', (canvas_width, canvas_height), bg_color)
    canvas = Image.blend(canvas_bg, canvas, 0.85)
    draw = ImageDraw.Draw(canvas)

    # Add title
    try:
        # Try to use a nice font
        font_paths = [
            '/System/Library/Fonts/Helvetica.ttc',
            '/System/Library/Fonts/SFNSDisplay.ttf',
            '/Library/Fonts/Arial.ttf',
        ]
        title_font = None
        for fp in font_paths:
            if os.path.exists(fp):
                title_font = ImageFont.truetype(fp, 42)
                break
        if title_font is None:
            title_font = ImageFont.load_default()

        subtitle_font_paths = [
            '/System/Library/Fonts/PingFang.ttc',
            '/System/Library/Fonts/STHeiti Light.ttc',
        ]
        subtitle_font = None
        for fp in subtitle_font_paths:
            if os.path.exists(fp):
                subtitle_font = ImageFont.truetype(fp, 24)
                break
        if subtitle_font is None:
            subtitle_font = ImageFont.load_default()
    except Exception:
        title_font = ImageFont.load_default()
        subtitle_font = ImageFont.load_default()

    # Draw title text
    title_text = "A2UI Component Showcase"
    title_bbox = draw.textbbox((0, 0), title_text, font=title_font)
    title_w = title_bbox[2] - title_bbox[0]
    draw.text(
        ((canvas_width - title_w) // 2, padding_y // 2),
        title_text,
        fill=(50, 55, 70),
        font=title_font
    )

    # Draw subtitle
    subtitle_text = "18 个核心组件 · 跨平台一致渲染"
    subtitle_bbox = draw.textbbox((0, 0), subtitle_text, font=subtitle_font)
    subtitle_w = subtitle_bbox[2] - subtitle_bbox[0]
    draw.text(
        ((canvas_width - subtitle_w) // 2, padding_y // 2 + 50),
        subtitle_text,
        fill=(120, 125, 140),
        font=subtitle_font
    )

    # Convert canvas to RGBA for compositing
    canvas = canvas.convert('RGBA')

    # Load and place each screenshot
    for i in range(18):
        img_path = os.path.join(source_dir, f'{i + 1}.jpg')
        if not os.path.exists(img_path):
            print(f"Warning: {img_path} not found, skipping.")
            continue

        # Load and resize
        img = Image.open(img_path).convert('RGB')
        img = img.resize((thumb_width, thumb_height), Image.LANCZOS)

        # Add phone frame (rounded corners + border)
        framed = add_phone_frame(img, corner_radius=16, border_width=2,
                                 border_color=(210, 212, 218))

        # Calculate position
        row = i // cols
        col = i % cols
        x = padding_x + col * (thumb_width + h_spacing)
        y = padding_y + title_height + row * (thumb_height + v_spacing)

        # Adjust for border width
        x -= 2
        y -= 2

        # Paste framed image
        canvas.paste(framed, (x, y), framed)

        print(f"  Placed image {i + 1} at row={row}, col={col}")

    # Add a subtle bottom decoration line
    draw2 = ImageDraw.Draw(canvas)
    line_y = canvas_height - padding_y // 2
    line_margin = canvas_width // 4
    draw2.line(
        [(line_margin, line_y), (canvas_width - line_margin, line_y)],
        fill=(200, 205, 215, 180),
        width=1
    )

    # Save as PNG (convert back to RGB for final output)
    final = canvas.convert('RGB')
    final.save(output_path, 'PNG', quality=95, optimize=True)
    print(f"\nShowcase image saved to: {output_path}")
    print(f"Image size: {final.width} x {final.height} pixels")

    return output_path

if __name__ == '__main__':
    create_showcase()
