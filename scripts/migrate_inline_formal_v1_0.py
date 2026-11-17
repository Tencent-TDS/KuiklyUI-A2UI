#!/usr/bin/env python3
import argparse
import ast
import json
import re
from pathlib import Path

from migrate_formal_demo_v1_0 import migrate_messages

ROOT = Path(__file__).resolve().parents[1]
FILES = (
    "androidAppForNativePage/src/main/java/com/tencent/kuikly/a2ui/demo/NativeCardDemoActivity.kt",
    "androidAppForNativePage/src/main/java/com/tencent/kuikly/a2ui/demo/CustomIconDemoActivity.kt",
    "androidAppForBothPages/src/main/java/com/tencent/kuikly/a2ui/demo/NativePageCardActivity.kt",
    "iosAppForNativePage/Sources/NativeCardDemoViewController.swift",
    "iosAppForBothPages/Sources/NativePageCardViewController.swift",
    "iosShared/Sources/CustomIconDemoViewController.swift",
    "ohosAppForNativePage/entry/src/main/ets/pages/NativeCardDemoPage.ets",
    "ohosAppForNativePage/entry/src/main/ets/pages/CustomIconDemoPage.ets",
    "ohosAppForBothPages/entry/src/main/ets/pages/NativeCardDemoPage.ets",
    "demo/src/commonMain/kotlin/com/tencent/kuikly/a2ui/demo/pages/A2UISurfaceDemoSample.kt",
)
COMPOSE = "demo/src/commonMain/kotlin/com/tencent/kuikly/a2ui/demo/pages/ComposeSampleWithA2UIPager.kt"
TRIPLE = re.compile(r'"""(.*?)"""', re.S)
CONSTANT = re.compile(r"const\s+(\w*(?:JSON|MESSAGES))(?::\s*string)?\s*=\s*(.*?);", re.S)
LITERAL = re.compile(r"'(?:\\.|[^\\'])*'")


def extract(relative, source):
    path = Path(relative)
    records = []
    messages = []
    if path.suffix == ".ets":
        for match in CONSTANT.finditer(source):
            value = "".join(ast.literal_eval(item.group(0)) for item in LITERAL.finditer(match.group(2)))
            document = json.loads(value)
            stream = document if isinstance(document, list) else [document]
            records.append((match.start(2), match.end(2), len(messages), len(stream), isinstance(document, list), "", ""))
            messages.extend(stream)
    else:
        for match in TRIPLE.finditer(source):
            literal = match.group(1)
            if '"version"' not in literal:
                continue
            literal = literal.replace("${'$'}", "$")
            if relative == COMPOSE:
                literal = literal.replace("${surfaceId}", "compose_weather_surface").replace("$surfaceId", "compose_weather_surface")
            if path.suffix == ".swift" and '"version": "1.0"' in literal:
                literal = literal.replace("\\\\", "\\")
            document = json.loads(literal)
            if not isinstance(document, dict):
                raise ValueError(f"Expected one envelope per multiline literal: {relative}")
            first_indent = re.search(r"\n([ \t]*)\{", match.group(1))
            last_indent = re.search(r"\n([ \t]*)$", match.group(1))
            records.append((match.start(1), match.end(1), len(messages), 1, False,
                            first_indent.group(1) if first_indent else "", last_indent.group(1) if last_indent else ""))
            messages.append(document)
    return records, messages


def inspect_messages(relative, messages):
    if not messages or any(message.get("version") != "1.0" for message in messages):
        raise ValueError(f"Missing or non-1.0 inline envelopes: {relative}")
    for message in messages:
        if "createSurface" in message and not message["createSurface"].get("catalogId"):
            raise ValueError(f"Missing inline catalogId: {relative}")


def migrate_file(relative):
    path = ROOT / relative
    source = path.read_text(encoding="utf-8")
    records, messages = extract(relative, source)
    if all(message.get("version") == "1.0" for message in messages):
        inspect_messages(relative, messages)
        return len(messages)
    if any(message.get("version") not in {"v0.9", "v0.9.1"} for message in messages):
        raise ValueError(f"Mixed-version source literals: {relative}")
    output = migrate_messages(messages)
    patches = []
    for start, end, index, count, is_array, indent, closing_indent in records:
        document = output[index:index + count] if is_array else output[index]
        if path.suffix == ".ets":
            encoded = json.dumps(document, ensure_ascii=False, separators=(",", ":")).replace("\\", "\\\\").replace("'", "\\'")
            replacement = "'" + encoded + "'"
        else:
            encoded = json.dumps(document, ensure_ascii=False, indent=2)
            if path.suffix == ".swift":
                encoded = encoded.replace("\\", "\\\\")
            else:
                encoded = encoded.replace("$", "${'$'}")
            replacement = "\n" + "\n".join(indent + line for line in encoded.splitlines()) + "\n" + closing_indent
        patches.append((start, end, replacement))
    updated = source
    for start, end, replacement in reversed(patches):
        updated = updated[:start] + replacement + updated[end:]
    if source.count("%SEQ%") != updated.count("%SEQ%"):
        raise ValueError(f"Lost card sequence placeholders: {relative}")
    _, decoded = extract(relative, updated)
    if decoded != output:
        raise ValueError(f"Language literal encoding changed the generated JSON: {relative}")
    if path.read_text(encoding="utf-8") != source:
        raise ValueError(f"Concurrent source edit: {relative}")
    inspect_messages(relative, decoded)
    path.write_text(updated, encoding="utf-8")
    return len(output)


def streams():
    result = []
    total = 0
    for relative in (*FILES, COMPOSE):
        _, messages = extract(relative, (ROOT / relative).read_text(encoding="utf-8"))
        inspect_messages(relative, messages)
        result.append({"file": relative, "messages": messages})
        total += len(messages)
    if total != 180:
        raise ValueError(f"Expected 180 inline envelopes, found {total}")
    rpc_files = (
        "androidAppForNativePage/src/main/java/com/tencent/kuikly/a2ui/demo/NativeRpcDemoActivity.kt",
        "demo/src/commonMain/kotlin/com/tencent/kuikly/a2ui/demo/pages/A2UIRpcDemoPager.kt",
        "iosShared/Sources/A2UIRpcDemoViewController.swift",
        "ohosAppForNativePage/entry/src/main/ets/pages/A2UIRpcDemoPage.ets",
    )
    for relative in rpc_files:
        source = (ROOT / relative).read_text(encoding="utf-8")
        if relative.endswith(".ets"):
            match = re.search(r"initialCreateSurfaceEnvelope:\s*string\s*=\s*\((.*?)\);", source, re.S)
            literal = "".join(ast.literal_eval(item.group(0)) for item in LITERAL.finditer(match.group(1)))
        else:
            literal = next(match.group(1) for match in TRIPLE.finditer(source) if '"version"' in match.group(1))
        decoder = json.JSONDecoder()
        messages = []
        while literal.strip():
            literal = literal.lstrip()
            envelope, end = decoder.raw_decode(literal)
            messages.append(envelope)
            literal = literal[end:]
        inspect_messages(relative, messages)
        if len(messages) != 2:
            raise ValueError(f"Expected two initial RPC envelopes: {relative}")
        result.append({"file": relative, "messages": messages})
    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--check", action="store_true")
    parser.add_argument("--streams-json", action="store_true")
    args = parser.parse_args()
    if args.streams_json:
        print(json.dumps(streams(), ensure_ascii=False))
        return
    if not args.check:
        total = sum(migrate_file(relative) for relative in FILES)
        if total != 178:
            raise ValueError(f"Expected 178 migrated envelopes, found {total}")
    streams()
    print("180 explicit 1.0 inline envelopes in 11 formal demo files; language decoding verified")


if __name__ == "__main__":
    main()
