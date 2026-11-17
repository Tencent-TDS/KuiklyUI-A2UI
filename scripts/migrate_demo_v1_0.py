#!/usr/bin/env python3
import argparse
import copy
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "androidAppForTest/src/main/assets"
CATALOG = "https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
PILOTS = (
    "a2ui_components/text/4_text_body.json",
    "a2ui_components/text/6_text_path_binding.json",
    "a2ui_components/textfield/6_textfield_email_checks_vs_legacy_regexp.json",
    "a2ui_components/list/1_list_direction_vertical.json",
    "a2ui_databinding_tests/03_child_list_template/1_column_template.json",
    "a2ui_examples/04_weather-current.json",
)
DYNAMIC_FIELDS = {"text", "label", "value", "placeholder", "url", "posterUrl", "name"}


def dynamic(value):
    if isinstance(value, list):
        return [dynamic(item) for item in value]
    if not isinstance(value, dict):
        return value
    if "path" in value:
        if set(value) != {"path"}:
            raise ValueError(f"Unsupported binding: {value}")
        return {"@path": value["path"]}
    if "call" in value:
        if set(value) - {"call", "args", "returnType"}:
            raise ValueError(f"Unsupported function: {value}")
        return {"@call": value["call"], **({"args": dynamic(value["args"])} if "args" in value else {})}
    return {key: dynamic(item) for key, item in value.items()}


def heading(value, level):
    prefix = "#" * level + " "
    if isinstance(value, str):
        if "\n" in value or value.startswith("#"):
            raise ValueError("Multiline or existing Markdown headings require manual review")
        return prefix + value
    if isinstance(value, dict) and set(value) == {"@path"}:
        path = value["@path"]
        if "}" in path:
            raise ValueError("Unsupported heading interpolation path")
        return {"@call": "formatString", "args": {"value": prefix + "${" + path + "}"}}
    if isinstance(value, dict) and value.get("@call") == "formatString" and set(value.get("args", {})) == {"value"}:
        result = copy.deepcopy(value)
        result["args"]["value"] = prefix + result["args"]["value"]
        return result
    raise ValueError("Dynamic heading requires manual review")


def migrate(document):
    result = copy.deepcopy(document)
    messages = result.get("messages")
    if not isinstance(messages, list) or not messages:
        raise ValueError("A non-empty message stream is required")
    identities = {}
    used = {}
    for message in messages:
        if message.get("version") not in {"v0.9", "v0.9.1"}:
            raise ValueError("Only uniform 0.9 streams can be migrated")
        update = message.get("updateComponents")
        if not update:
            continue
        surface = update["surfaceId"]
        mapping = identities.setdefault(surface, {})
        reserved = used.setdefault(surface, set())
        for component in update["components"]:
            original = component["id"]
            if original in mapping:
                continue
            renamed = original.replace("-", "_")
            if not renamed.isidentifier() or renamed in reserved:
                raise ValueError(f"Unsupported or colliding component ID: {original}")
            mapping[original] = renamed
            reserved.add(renamed)

    for message in messages:
        message["version"] = "1.0"
        if "createSurface" in message:
            surface = message["createSurface"]
            if "theme" in surface:
                raise ValueError("Theme semantics require a separate 1.0 showcase")
            if surface.get("catalogId") not in {None, "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json"}:
                raise ValueError("Custom catalogs must be migrated separately")
            surface["catalogId"] = CATALOG
        elif "updateComponents" in message:
            update = message["updateComponents"]
            mapping = identities[update["surfaceId"]]
            for component in update["components"]:
                if "styles" in component or component.get("component") not in {"Card", "Column", "Row", "List", "Text", "TextField"}:
                    raise ValueError("This pilot migrator does not support styles or extension components")
                component["id"] = mapping[component["id"]]
                if "child" in component:
                    component["child"] = mapping[component["child"]]
                children = component.get("children")
                if isinstance(children, list):
                    component["children"] = [mapping[child] for child in children]
                elif isinstance(children, dict):
                    if set(children) != {"componentId", "path"}:
                        raise ValueError("Unsupported ChildList structure")
                    children["componentId"] = mapping[children["componentId"]]
                for field in DYNAMIC_FIELDS & component.keys():
                    component[field] = dynamic(component[field])
                if "checks" in component:
                    component["@checks"] = [{**check, "condition": dynamic(check["condition"])} for check in component.pop("checks")]
                if component.get("component") == "Text" and component.get("variant") in {"h1", "h2", "h3", "h4", "h5"}:
                    level = int(component.pop("variant")[1])
                    component["text"] = heading(component["text"], level)
                if "validationRegexp" in component:
                    pattern = component.pop("validationRegexp")
                    if component.get("value") != "" or component["id"] != "tf" or update["surfaceId"] != "test-tf-regexp":
                        raise ValueError("Legacy regexp migration needs an explicitly reviewed binding")
                    component["value"] = {"@path": "/emailRegexp"}
                    component["label"] = "Email (regex via @checks)"
                    component["@checks"] = [{"condition": {"@call": "regex", "args": {"value": {"@path": "/emailRegexp"}, "pattern": pattern}},
                                              "message": "Please enter an address matching the original email pattern"}]
                if component.get("component") == "TextField" and component["id"] == "tf_checks":
                    component["label"] = "Email (email via @checks)"
        elif "updateDataModel" not in message:
            raise ValueError("This pilot migrator only supports create, components and data envelopes")

    if any(message.get("updateDataModel", {}).get("surfaceId") == "test-tf-regexp" for message in messages):
        updates = [message["updateDataModel"] for message in messages if message.get("updateDataModel", {}).get("surfaceId") == "test-tf-regexp"]
        if len(updates) != 1 or updates[0].get("path") not in {None, "/"} or "emailRegexp" in updates[0]["value"]:
            raise ValueError("Legacy regexp model initialization requires manual review")
        updates[0]["value"]["emailRegexp"] = ""
        result["name"] = "TextField - email and original regex validation via @checks"
        result["description"] = "A2UI 1.0: both editable values are model-bound; email and the original regex validate via @checks. The unchanged legacy validationRegexp comparison remains in the 0.9 compatibility archive."
    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    for relative in PILOTS:
        source = ASSETS / "v0_9" / relative
        target = ASSETS / "v1_0" / relative
        text = json.dumps(migrate(json.loads(source.read_text(encoding="utf-8"))), ensure_ascii=False, indent=2) + "\n"
        if args.check:
            if not target.is_file() or target.read_text(encoding="utf-8") != text:
                raise SystemExit(f"Pilot asset is stale: {target}")
        elif target.exists():
            if target.read_text(encoding="utf-8") != text:
                raise SystemExit(f"Refusing to overwrite an edited pilot: {target}")
        else:
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(text, encoding="utf-8")
    print(f"{len(PILOTS)} reviewed 1.0 pilot assets are up to date")


if __name__ == "__main__":
    main()
