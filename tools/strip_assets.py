#!/usr/bin/env python3
"""Moves the protected assets from src/main/resources into a backup OUTSIDE the repo.

Only files that also exist in the original jar are moved (directly by path or via the
GeckoLib mapping geo/ -> geckolib/models/, animations/ -> geckolib/animations/). Files that exist ONLY in the port
(e.g. self-made trim masks) stay in place and are reported.

Usage from the project folder (AHP/):
    python tools/strip_assets.py <pfad/zu/adorablehamsterpets-...-1_21_1_fabric.jar> [--dry-run]
"""
import shutil, sys, zipfile
from pathlib import Path

args = [a for a in sys.argv[1:] if not a.startswith("--")]
dry = "--dry-run" in sys.argv
if not args:
    sys.exit(__doc__)

root = Path(__file__).resolve().parent.parent
res = root / "src" / "main" / "resources"
backup = root.parent / "AHP_original_assets_BACKUP"
NS = "assets/adorablehamsterpets/"
PREFIXES = (NS, "assets/xaerominimap/entity/")

with zipfile.ZipFile(args[0]) as z:
    original = {n for n in z.namelist() if n.startswith(PREFIXES) and not n.endswith("/")}

def original_equivalents(rel: str):
    yield rel
    if rel.startswith(NS + "geckolib/models/"):
        yield NS + "geo/" + rel[len(NS + "geckolib/models/"):]
    if rel.startswith(NS + "geckolib/animations/"):
        yield NS + "animations/" + rel[len(NS + "geckolib/animations/"):]

moved, kept = 0, []
for f in sorted((res / "assets").rglob("*")):
    if not f.is_file():
        continue
    rel = f.relative_to(res).as_posix()
    if not rel.startswith(PREFIXES):
        continue
    if any(e in original for e in original_equivalents(rel)):
        dst = backup / rel
        if not dry:
            dst.parent.mkdir(parents=True, exist_ok=True)
            shutil.move(str(f), str(dst))
        moved += 1
    else:
        kept.append(rel)

print(f"{'(dry-run) ' if dry else ''}verschoben: {moved} Dateien -> {backup}")
print(f"nur im Port vorhanden, bleiben liegen: {len(kept)}")
for k in kept:
    print("  ", k)
print("\nNicht angefasst: assets/minecraft/atlases (kleine Atlas-Konfiguration), assets/LICENSE_NOTICE.txt, src/main/generated")
