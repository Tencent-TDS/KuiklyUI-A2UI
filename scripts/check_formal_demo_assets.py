#!/usr/bin/env python3
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "androidAppForKuiklyPage/src/main/assets"
DIRECTORIES = ("a2ui_components", "a2ui_custom_components", "a2ui_custom_functions")


def check_formal_assets():
    originals = {asset.relative_to(SOURCE).as_posix(): asset.read_bytes()
                 for directory in DIRECTORIES for asset in (SOURCE / directory).rglob("*.json")}
    if len(originals) != 108:
        raise ValueError(f"Expected 108 formal source assets, found {len(originals)}")
    for relative, raw in originals.items():
        document = json.loads(raw)
        for index, message in enumerate(document["messages"]):
            if message.get("version") != "1.0":
                raise ValueError(f"Non-1.0 message: {relative}, {index}")
            if "createSurface" in message and not message["createSurface"].get("catalogId"):
                raise ValueError(f"Missing catalogId: {relative}")
        for key in ("name", "description"):
            if "0.9" in document.get(key, "") or "v0_9" in document.get(key, ""):
                raise ValueError(f"Legacy demo wording: {relative}, {key}")
    targets = [ROOT / "androidAppForNativePage/src/main/assets"]
    targets += [ROOT / f"iosAppFor{kind}/Resources" for kind in ("KuiklyPage", "NativePage")]
    targets += [ROOT / f"ohosAppFor{kind}/entry/src/main/resources/rawfile" for kind in ("KuiklyPage", "NativePage")]
    for target in targets:
        copies = {asset.relative_to(target).as_posix(): asset.read_bytes()
                  for directory in DIRECTORIES for asset in (target / directory).rglob("*.json")}
        if originals != copies:
            raise ValueError(f"Missing, extra or stale formal asset copy: {target}")
    for platform in ("android", "ios", "ohos"):
        base = ROOT / f"{platform}AppForBothPages"
        directory = base / ("src/main/assets" if platform == "android" else "Resources" if platform == "ios" else "entry/src/main/resources/rawfile")
        for folder in DIRECTORIES:
            for asset in (directory / folder).rglob("*.json"):
                relative = asset.relative_to(directory).as_posix()
                if relative not in originals or originals[relative] != asset.read_bytes():
                    raise ValueError(f"Stale BothPages asset: {asset}")
    print("108 formal assets: explicit 1.0, catalogId present, no legacy labels; all five consumer copies byte-identical")


if __name__ == "__main__":
    check_formal_assets()
