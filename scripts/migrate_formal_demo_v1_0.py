#!/usr/bin/env python3
import argparse
import copy
import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "androidAppForTest/src/main/assets/v0_9"
TARGET = ROOT / "androidAppForKuiklyPage/src/main/assets"
BASIC = "https://a2ui.org/specification/v1_0/catalogs/basic/catalog.json"
STYLE_CATALOG = "kuikly_a2ui_catalog_v1_0.json"
CATALOGS = {
    "https://a2ui.org/specification/v0_9/catalogs/basic/catalog.json": BASIC,
    "custom_components_catalog.json": "custom_components_catalog_v1_0.json",
    "custom_functions_catalog.json": "custom_functions_catalog_v1_0.json",
    "kuikly_a2ui_catalog.json": STYLE_CATALOG,
}
DYNAMIC_FIELDS = {"text", "label", "value", "placeholder", "url", "posterUrl", "name", "description", "min", "max", "filterable", "enableDate", "enableTime", "weight"}
CHILD_FIELDS = {"child", "trigger", "content"}


def migrate_dynamic(value):
    if isinstance(value, list):
        return [migrate_dynamic(item) for item in value]
    if not isinstance(value, dict):
        return value
    if set(value) == {"path"}:
        return {"@path": value["path"]}
    if "call" in value or "@call" in value:
        if set(value) - {"call", "@call", "args", "returnType", "catalogId"}:
            raise ValueError(f"Unsupported function fields: {value}")
        result = {"@call": value.get("@call", value.get("call"))}
        if "args" in value:
            result["args"] = migrate_dynamic(value["args"])
        if "catalogId" in value:
            result["catalogId"] = CATALOGS.get(value["catalogId"], value["catalogId"])
        return result
    return {key: migrate_dynamic(item) for key, item in value.items()}


def markdown_heading(value, level):
    prefix = "#" * level + " "
    if isinstance(value, str):
        return prefix + value
    if isinstance(value, dict) and set(value) == {"@path"}:
        return {"@call": "formatString", "args": {"value": prefix + "${" + value["@path"] + "}"}}
    if isinstance(value, dict) and value.get("@call") == "formatString" and isinstance(value.get("args", {}).get("value"), str):
        result = copy.deepcopy(value)
        result["args"]["value"] = prefix + result["args"]["value"]
        return result
    if isinstance(value, dict) and "@call" in value and "catalogId" not in value:
        def expression(argument):
            if isinstance(argument, dict) and set(argument) == {"@path"}:
                return "${" + argument["@path"] + "}"
            if isinstance(argument, str) and not any(character in argument for character in "'\\\\{}"):
                return "'" + argument + "'"
            if isinstance(argument, (int, float)) and not isinstance(argument, bool):
                return json.dumps(argument)
            raise ValueError(f"Heading interpolation needs manual review: {argument}")
        arguments = ", ".join(f"{key}:{expression(argument)}" for key, argument in value.get("args", {}).items())
        return {"@call": "formatString", "args": {"value": prefix + "${" + value["@call"] + "(" + arguments + ")}"}}
    raise ValueError(f"Heading requires manual review: {value}")


def migrate_messages(messages):
    output = copy.deepcopy(messages)
    if not output:
        raise ValueError("Empty message stream")
    mappings = {}
    components_by_surface = {}
    for message in output:
        if message.get("version") not in {"v0.9", "v0.9.1", "1.0", "v1.0"}:
            raise ValueError("Missing or unsupported explicit protocol version")
        body = message.get("updateComponents", message.get("createSurface", {}))
        if "components" not in body:
            continue
        surface = body["surfaceId"]
        mapping = mappings.setdefault(surface, {})
        for component in body["components"]:
            original = component["id"]
            converted = original.replace("-", "_")
            if not converted.isidentifier():
                raise ValueError(f"Invalid component identifier: {original}")
            if original not in mapping and converted in mapping.values():
                raise ValueError(f"Component identifier collision: {original}")
            mapping[original] = converted
            components_by_surface.setdefault(surface, []).append(component)
    initialized_models = {}
    added_bindings = {}
    for message in output:
        message["version"] = "1.0"
        if "createSurface" in message:
            surface = message["createSurface"]
            if "theme" in surface:
                raise ValueError("Theme demo requires explicit redesign")
            old_catalog = surface.get("catalogId", BASIC)
            surface["catalogId"] = CATALOGS.get(old_catalog, old_catalog)
            if surface["catalogId"] == BASIC and any("styles" in c for c in components_by_surface.get(surface["surfaceId"], [])):
                surface["catalogId"] = STYLE_CATALOG
            if "dataModel" in surface:
                initialized_models[surface["surfaceId"]] = surface["dataModel"]
        if "updateDataModel" in message:
            body = message["updateDataModel"]
            if body.get("path") in {None, "/"} and isinstance(body.get("value"), dict):
                initialized_models[body["surfaceId"]] = body["value"]
        body = message.get("updateComponents", message.get("createSurface", {}))
        if "components" not in body:
            continue
        surface_id = body["surfaceId"]
        mapping = mappings[surface_id]
        for component in body["components"]:
            component["id"] = mapping[component["id"]]
            for field in CHILD_FIELDS & component.keys():
                if not isinstance(component[field], str):
                    raise ValueError("Nested component objects must be flattened explicitly")
                component[field] = mapping.get(component[field], component[field].replace("-", "_"))
            if "children" in component:
                children = component["children"]
                if isinstance(children, list):
                    if not all(isinstance(child, str) for child in children):
                        raise ValueError("Nested children must be flattened explicitly")
                    component["children"] = [mapping.get(child, child.replace("-", "_")) for child in children]
                elif isinstance(children, dict) and set(children) == {"componentId", "path"}:
                    children["componentId"] = mapping.get(children["componentId"], children["componentId"].replace("-", "_"))
                else:
                    raise ValueError(f"Unsupported child list: {children}")
            for field in DYNAMIC_FIELDS & component.keys():
                component[field] = migrate_dynamic(component[field])
            if "@catalogId" in component:
                component["@catalogId"] = CATALOGS.get(component["@catalogId"], component["@catalogId"])
            if "tabs" in component:
                for tab in component["tabs"]:
                    tab["child"] = mapping.get(tab["child"], tab["child"].replace("-", "_"))
                    tab["title"] = migrate_dynamic(tab["title"])
            if "options" in component:
                for option in component["options"]:
                    option["label"] = migrate_dynamic(option["label"])
            if "action" in component:
                action = component["action"]
                if "functionCall" in action:
                    action["functionCall"] = migrate_dynamic(action["functionCall"])
                if "event" in action:
                    event = action["event"]
                    if "context" in event:
                        event["context"] = {key: migrate_dynamic(value) for key, value in event["context"].items()}
                    if "userMessage" in event:
                        event["userMessage"] = migrate_dynamic(event["userMessage"])
            if "checks" in component:
                component["@checks"] = component.pop("checks")
            if "@checks" in component:
                for check in component["@checks"]:
                    check["condition"] = migrate_dynamic(check["condition"])
            if "styles" in component:
                component["styles"] = {key: migrate_dynamic(value) for key, value in component["styles"].items()}
            if component["component"] == "Text" and component.get("variant") in {"h1", "h2", "h3", "h4", "h5"}:
                level = int(component.pop("variant")[1])
                component["text"] = markdown_heading(component["text"], level)
            if "validationRegexp" in component:
                pattern = component.pop("validationRegexp")
                value = component["value"]
                if not isinstance(value, dict) or "@path" not in value:
                    key = f"{component['id']}_validation_value"
                    added_bindings.setdefault(surface_id, {})[key] = value
                    component["value"] = value = {"@path": f"/{key}"}
                component.setdefault("@checks", []).append({"condition": {"@call": "regex", "args": {"value": value, "pattern": pattern}}, "message": "Please enter a value matching the required pattern"})
    for surface_id, values in added_bindings.items():
        if surface_id in initialized_models:
            model = initialized_models[surface_id]
            for key, value in values.items():
                if key in model:
                    raise ValueError(f"Model binding collision: {key}")
                model[key] = value
        else:
            create = next(m["createSurface"] for m in output if m.get("createSurface", {}).get("surfaceId") == surface_id)
            create["dataModel"] = values
    return output


def migrate_document(document):
    result = copy.deepcopy(document)
    result["messages"] = migrate_messages(result["messages"])
    for key in ("name", "description"):
        if key in result:
            result[key] = re.sub(r"v?0[._]9(?:\.1)?\+?", "1.0", result[key])
    if "validationRegexp" in document.get("name", ""):
        result["name"] = "TextField - email and regex validation via @checks"
        result["description"] = "Both editable values use model bindings. The email function and regex function validate input through A2UI 1.0 @checks."
        for message in result["messages"]:
            for component in message.get("updateComponents", {}).get("components", []):
                if component["component"] == "TextField":
                    component["label"] = "Email (email via @checks)" if component["id"] == "tf_checks" else "Email (regex via @checks)"
    if any(message.get("createSurface", {}).get("surfaceId") == "cf-bool-1" for message in result["messages"]):
        result["name"] = "自定义函数返回 Boolean 与标准 ValidationResult 校验"
        result["description"] = "isVip 返回 Boolean 并响应 userLevel 更新；按钮使用官方 numeric ValidationResult 校验同一 VIP 阈值，不将 Boolean 函数冒充 CheckFunctionCall。"
        for message in result["messages"]:
            for component in message.get("updateComponents", {}).get("components", []):
                if component["id"] == "vip_btn":
                    component["@checks"] = [{"condition": {"@call": "numeric", "args": {"value": {"@path": "/userLevel"}, "min": 3}}, "message": "仅 VIP 用户（level >= 3）可进入"}]
                elif component["id"] == "title":
                    component["text"] = "isVip(level) → Boolean；numeric → ValidationResult"
                elif component["id"] == "info":
                    component["text"] = {"@call": "formatString", "args": {"value": "当前 userLevel = ${/userLevel}，isVip 返回 ${isVip(level:${/userLevel})}"}}
    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    args = parser.parse_args()
    total = 0
    for directory in ("a2ui_components", "a2ui_custom_components", "a2ui_custom_functions"):
        for source in sorted((SOURCE / directory).rglob("*.json")):
            target = TARGET / source.relative_to(SOURCE)
            text = json.dumps(migrate_document(json.loads(source.read_text(encoding="utf-8"))), ensure_ascii=False, indent=2) + "\n"
            if args.check:
                if not target.is_file() or target.read_text(encoding="utf-8") != text:
                    raise SystemExit(f"Formal asset migration is stale: {target}")
            else:
                target.write_text(text, encoding="utf-8")
            total += 1
    print(f"{total} formal demo assets migrated to explicit 1.0")


if __name__ == "__main__":
    main()
