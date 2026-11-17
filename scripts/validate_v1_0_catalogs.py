#!/usr/bin/env python3
"""Materialize v1.0 catalogs and validate them against the official meta-schema, offline."""
from __future__ import annotations

import argparse
import copy
import json
import os
from pathlib import Path
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
CATALOGS_DIR = ROOT / "catalogs"
SPEC_ROOT = Path(os.environ.get("A2UI_SPEC_ROOT", "/Users/etkm/git_a2ui/a2ui"))


def read_json(path: Path) -> dict:
    with path.open(encoding="utf-8") as stream:
        return json.load(stream)


def relative_refs(value):
    if isinstance(value, list):
        return [relative_refs(item) for item in value]
    if not isinstance(value, dict):
        return value
    result = {}
    for key, item in value.items():
        if key == "$ref" and isinstance(item, str) and item.startswith(
            "https://a2ui.org/specification/v1_0/common_types.json#"
        ):
            result[key] = item.split("/v1_0/", 1)[1]
        else:
            result[key] = relative_refs(item)
    return result


def component_leaf(definition: dict) -> dict:
    if "allOf" not in definition:
        return copy.deepcopy(definition)
    result = {key: copy.deepcopy(value) for key, value in definition.items()
              if key not in {"allOf", "unevaluatedProperties"}}
    result.setdefault("properties", {})["weight"] = {"$ref": "#/$defs/Weight"}
    for branch in definition["allOf"]:
        if "$ref" in branch:
            if branch["$ref"] not in {
                "common_types.json#/$defs/ComponentCommon",
                "#/$defs/CatalogComponentCommon",
            }:
                raise ValueError(f"Unsupported custom component composition: {branch['$ref']}")
            continue
        for key, value in branch.items():
            if key == "properties":
                result["properties"].update(copy.deepcopy(value))
            elif key == "required":
                result.setdefault(key, []).extend(value)
            else:
                result[key] = copy.deepcopy(value)
    return result


def close_style_objects(node) -> None:
    if isinstance(node, list):
        for item in node:
            close_style_objects(item)
    elif isinstance(node, dict):
        if node.get("type") == "object" and "properties" in node:
            node.setdefault("additionalProperties", False)
        for item in node.values():
            close_style_objects(item)


def build_catalog(basic: dict, extension: dict, filename: str) -> dict:
    extension = relative_refs(extension)
    catalog = copy.deepcopy(basic)
    for key in ("$schema", "$id", "title", "description", "catalogId"):
        catalog[key] = extension[key]
    catalog["protocolVersion"] = "1.0"
    if filename == "kuikly_a2ui_catalog_v1_0.json":
        catalog["description"] = "Materialized official A2UI 1.0 basic catalog with optional Kuikly styles, merged closed text-style leaves, and registered custom Icon names and sizes."
        common = copy.deepcopy(extension["$defs"]["KuiklyCommonStyles"])
        text = copy.deepcopy(extension["$defs"]["KuiklyTextStyles"])
        for branch in common["properties"]["backgroundLinearGradient"]["oneOf"]:
            if branch.get("type") == "object":
                directions = branch["properties"]["direction"]["enum"]
                if "to-bottom-right" not in directions:
                    directions.append("to-bottom-right")
        text.pop("allOf", None)
        text_specific = {key: value for key, value in text["properties"].items()
                         if key not in common["properties"]}
        text["properties"] = {**copy.deepcopy(common["properties"]), **text_specific}
        text["additionalProperties"] = False
        close_style_objects(common)
        close_style_objects(text)
        catalog["$defs"].update(KuiklyCommonStyles=common, KuiklyTextStyles=text)
        for name, definition in catalog["components"].items():
            style = "KuiklyTextStyles" if name == "Text" else "KuiklyCommonStyles"
            definition.setdefault("properties", {})["styles"] = {"$ref": f"#/$defs/{style}"}
        catalog["components"]["Icon"]["properties"]["name"]["oneOf"][0].pop("enum", None)
        catalog["components"]["Icon"]["properties"]["size"] = {"type": "number", "minimum": 0}
    else:
        for name, definition in extension.get("components", {}).items():
            if name not in basic["components"]:
                catalog["components"][name] = component_leaf(definition)
        for name, definition in extension.get("functions", {}).items():
            if name not in basic["functions"]:
                definition = copy.deepcopy(definition)
                definition.pop("unevaluatedProperties", None)
                if name != "fetchUser":
                    definition["allowedCallers"] = "rendererOrAgent"
                catalog["functions"][name] = definition
        if filename == "custom_functions_catalog_v1_0.json":
            catalog["functions"]["fetchUser"] = {
                "type": "object",
                "description": "Fetches the requested user through the agent-side RPC host.",
                "properties": {
                    "@call": {"const": "fetchUser"},
                    "args": {
                        "type": "object",
                        "properties": {"id": {"$ref": "common_types.json#/$defs/DynamicNumber"}},
                        "required": ["id"],
                        "unevaluatedProperties": False,
                    },
                },
                "required": ["@call", "args"],
                "returnType": "object",
                "allowedCallers": "agentOnly",
            }
    for kind, entry in (("components", "anyComponent"), ("functions", "anyFunction")):
        catalog["$defs"][entry] = {"oneOf": [{"$ref": f"#/{kind}/{name}"} for name in catalog[kind]]}
        if kind == "components":
            catalog["$defs"][entry]["discriminator"] = {"propertyName": "component"}
    return catalog


def materialize_catalogs() -> None:
    basic = read_json(SPEC_ROOT / "catalogs/basic/v1/catalog.json")
    products = [(file, build_catalog(basic, read_json(file), file.name))
                for file in sorted(CATALOGS_DIR.glob("*_v1_0.json"))]
    for file, catalog in products:
        file.write_text(json.dumps(catalog, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
        print(f"[BUILT] {file.relative_to(ROOT)}")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--materialize", action="store_true", help="rebuild shipped catalogs from the local official basic catalog")
    parser.add_argument("--self-test", action="store_true", help="also run strict offline positive and negative cases")
    parser.add_argument("--file", type=Path, help="validate one catalog document")
    args = parser.parse_args()
    try:
        if args.materialize:
            materialize_catalogs()
        entry = ROOT.parent / "ka2ui-harness/scripts/schema.mjs"
        command = ["node", str(entry), "--catalogs"]
        if args.file:
            command.extend(["--file", str(args.file.resolve())])
        if args.self_test:
            command.append("--self-test")
        return subprocess.run(command, check=False).returncode
    except (OSError, ValueError, KeyError) as error:
        print(f"[FAIL] {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    sys.exit(main())
