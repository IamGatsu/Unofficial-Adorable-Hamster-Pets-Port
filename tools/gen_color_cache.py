#!/usr/bin/env python3
"""Computes the average colors (HSB position) of the genetics palettes and writes them as a small number table to
src/main/resources/data/adorablehamsterpets/genetics/color_cache.json.

Background: ColorSpaceUtil.analyzeImage used to read these PNGs directly from the mod jar (also server-side). Without assets in the jar,
ColorCache provides these values instead. The calculation matches ColorSpaceUtil.analyzeImage (fully opaque pixels only,
java.awt.Color.RGBtoHSB, averaging on the color wheel).

Usage:  python tools/gen_color_cache.py <original.jar>      (requires Pillow: pip install pillow)
"""
import io, json, math, re, sys, zipfile
from pathlib import Path
from PIL import Image

if len(sys.argv) < 2:
    sys.exit(__doc__)

PATTERN = re.compile(r"^assets/adorablehamsterpets/textures/entity/hamster/[^/]+/[^/]+\.png$")

def rgb_to_hsb(r, g, b):
    """Nachbau von java.awt.Color.RGBtoHSB."""
    cmax, cmin = max(r, g, b), min(r, g, b)
    brightness = cmax / 255.0
    saturation = (cmax - cmin) / cmax if cmax != 0 else 0.0
    if saturation == 0:
        return 0.0, saturation, brightness
    d = float(cmax - cmin)
    rc, gc, bc = (cmax - r) / d, (cmax - g) / d, (cmax - b) / d
    if r == cmax:
        hue = bc - gc
    elif g == cmax:
        hue = 2.0 + rc - bc
    else:
        hue = 4.0 + gc - rc
    hue /= 6.0
    if hue < 0:
        hue += 1.0
    return hue, saturation, brightness

def analyze(png_bytes):
    img = Image.open(io.BytesIO(png_bytes)).convert("RGBA")
    sx = sy = sz = 0.0
    n = 0
    for r, g, b, a in img.getdata():
        if a < 255:
            continue
        h, s, v = rgb_to_hsb(r, g, b)
        ang = h * math.pi * 2.0
        sx += s * math.cos(ang)
        sy += s * math.sin(ang)
        sz += v
        n += 1
    return [0.0, 0.0, 0.0] if n == 0 else [sx / n, sy / n, sz / n]

out = {}
with zipfile.ZipFile(sys.argv[1]) as z:
    for name in sorted(z.namelist()):
        if PATTERN.match(name):
            out[name] = analyze(z.read(name))

root = Path(__file__).resolve().parent.parent
target = root / "src" / "main" / "resources" / "data" / "adorablehamsterpets" / "genetics" / "color_cache.json"
target.parent.mkdir(parents=True, exist_ok=True)
target.write_text(json.dumps(out, indent=1) + "\n")
print(f"{len(out)} Eintraege -> {target.relative_to(root)}")
