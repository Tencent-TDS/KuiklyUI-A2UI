import json
import unittest

from migrate_inline_formal_v1_0 import ROOT, FILES, COMPOSE, extract, streams


class InlineFormalV10Test(unittest.TestCase):
    def test_language_decoded_sources_contain_all_188_envelopes(self):
        decoded = streams()
        self.assertEqual(15, len(decoded))
        self.assertEqual(188, sum(len(stream["messages"]) for stream in decoded))
        for stream in decoded:
            self.assertTrue(all(message["version"] == "1.0" for message in stream["messages"]), stream["file"])

    def test_four_platform_card_streams_are_identical(self):
        decoded = {stream["file"]: stream["messages"] for stream in streams()}
        reference = decoded[FILES[0]]
        self.assertEqual(40, len(reference))
        for relative in (FILES[3], FILES[6], FILES[9]):
            self.assertEqual(reference, decoded[relative], relative)

    def test_component_references_exist_and_ids_do_not_collide(self):
        for stream in streams():
            definitions = {}
            for envelope in stream["messages"]:
                update = envelope.get("updateComponents")
                if update:
                    surface = update["surfaceId"]
                    components = update["components"]
                    self.assertEqual(len(components), len({component["id"] for component in components}), stream["file"])
                    definitions.setdefault(surface, {}).update({component["id"]: component for component in components})
            for surface, components in definitions.items():
                for component in components.values():
                    children = component.get("children", [])
                    references = children if isinstance(children, list) else [children["componentId"]]
                    references += [component[field] for field in ("child", "trigger", "content") if field in component]
                    for reference in references:
                        self.assertIn(reference, components, (stream["file"], surface, component["id"]))

    def test_swift_and_arkts_escaped_multiline_content_is_actual_json_text(self):
        decoded = {stream["file"]: stream["messages"] for stream in streams()}
        def descriptions(relative):
            return [component["text"] for message in decoded[relative]
                    for component in message.get("updateComponents", {}).get("components", []) if component.get("component") == "Text"]
        self.assertEqual(descriptions(FILES[0]), descriptions(FILES[3]))
        self.assertEqual(descriptions(FILES[0]), descriptions(FILES[6]))
        self.assertTrue(any("38" in str(text) for text in descriptions(FILES[0])))

    def test_compose_variable_interpolation_is_preserved(self):
        source = (ROOT / COMPOSE).read_text()
        _, messages = extract(COMPOSE, source)
        self.assertEqual(2, len(messages))
        self.assertEqual("compose_weather_surface", messages[0]["createSurface"]["surfaceId"])
        self.assertIn("protocolConfig(A2UIProtocolVersion.V1_0", source)


if __name__ == "__main__":
    unittest.main()
