#!/usr/bin/env python3
import argparse
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "androidAppForTest/src/main/assets"
OUTPUT = ROOT / "ohosAppForTest/entry/src/main/resources/rawfile/a2ui_manifest.json"
VERSIONS = {"v0_9": {"v0.9", "v0.9.1"}, "v1_0": {"1.0", "v1.0"}}


def build_manifest(source=SOURCE):
    directories = {}
    files = {}
    for version, allowed in VERSIONS.items():
        base = source / version
        if not base.is_dir():
            raise ValueError(f"Missing version directory: {base}")
        for directory in sorted([base, *(p for p in base.rglob("*") if p.is_dir())]):
            key = directory.relative_to(source).as_posix()
            directories[key] = sorted(p.name for p in directory.iterdir() if p.is_dir())
            files[key] = sorted(p.name for p in directory.glob("*.json") if p.is_file())
            for name in files[key]:
                asset = directory / name
                document = json.loads(asset.read_text(encoding="utf-8"))
                messages = document if isinstance(document, list) else document.get("messages")
                if not isinstance(messages, list) or not messages:
                    raise ValueError(f"Missing messages: {asset}")
                for index, message in enumerate(messages):
                    if not isinstance(message, dict) or message.get("version") not in allowed:
                        raise ValueError(f"Version mismatch or missing version: {asset}, message {index}")
    return {"directories": directories, "files": files}


def check_synced_assets(source=SOURCE, targets=None):
    targets = targets if targets is not None else [ROOT / "iosAppForTest/Resources", OUTPUT.parent]
    for version in VERSIONS:
        originals = {asset.relative_to(source).as_posix(): asset.read_bytes() for asset in (source / version).rglob("*.json")}
        for target in targets:
            copies = {asset.relative_to(target).as_posix(): asset.read_bytes() for asset in (target / version).rglob("*.json")}
            if originals != copies:
                raise ValueError(f"Missing, extra or changed synchronized assets: {target / version}")


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    parser.add_argument("--check-sync", action="store_true")
    args = parser.parse_args()
    text = json.dumps(build_manifest(), ensure_ascii=False, indent=2) + "\n"
    if args.check or args.check_sync:
        if not OUTPUT.is_file() or OUTPUT.read_text(encoding="utf-8") != text:
            raise SystemExit("Demo asset manifest is stale; run scripts/generate_demo_asset_manifest.py")
    else:
        OUTPUT.parent.mkdir(parents=True, exist_ok=True)
        OUTPUT.write_text(text, encoding="utf-8")
    if args.check_sync:
        check_synced_assets()
        print("Versioned Android/iOS/OHOS JSON assets are byte-identical")
    print("Versioned demo asset manifest is up to date")


if __name__ == "__main__":
    main()
