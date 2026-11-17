import copy
import json
import unittest

from migrate_formal_demo_v1_0 import SOURCE, TARGET, migrate_document, migrate_messages, markdown_heading


class FormalDemoMigrationTest(unittest.TestCase):
    def test_all_formal_assets_are_current_and_preserve_original_stream_order(self):
        count = 0
        for directory in ("a2ui_components", "a2ui_custom_components", "a2ui_custom_functions"):
            for source in (SOURCE / directory).rglob("*.json"):
                with self.subTest(path=source):
                    old = json.loads(source.read_text())
                    original = copy.deepcopy(old)
                    new = migrate_document(old)
                    self.assertEqual(original, old)
                    self.assertEqual(new, json.loads((TARGET / source.relative_to(SOURCE)).read_text()))
                    self.assertEqual(len(old["messages"]), len(new["messages"]))
                    for before, after in zip(old["messages"], new["messages"]):
                        branch = next(key for key in before if key != "version")
                        self.assertEqual(set(before), set(after))
                        self.assertEqual(before[branch]["surfaceId"], after[branch]["surfaceId"])
                        self.assertEqual("1.0", after["version"])
                    count += 1
        self.assertEqual(108, count)

    def test_dynamic_date_heading_keeps_its_bound_expression(self):
        self.assertEqual({"@call": "formatString", "args": {"value": "## ${formatDate(value:${/date}, format:'MMM d, yyyy')}"}},
                         markdown_heading({"@call": "formatDate", "args": {"value": {"@path": "/date"}, "format": "MMM d, yyyy"}}, 2))

    def test_children_collection_path_and_business_data_are_not_rewritten(self):
        stream = [{"version": "v0.9", "createSurface": {"surfaceId": "example"}},
                  {"version": "v0.9", "updateComponents": {"surfaceId": "example", "components": [
                      {"id": "root", "component": "List", "children": {"componentId": "item-text", "path": "/items"}},
                      {"id": "item-text", "component": "Text", "text": {"path": "name"}}]}},
                  {"version": "v0.9", "updateDataModel": {"surfaceId": "example", "value": {"items": [{"path": "literal", "name": "item-text", "call": "literal"}]}}}]
        new = migrate_messages(stream)
        self.assertEqual({"componentId": "item_text", "path": "/items"}, new[1]["updateComponents"]["components"][0]["children"])
        self.assertEqual(stream[-1]["updateDataModel"], new[-1]["updateDataModel"])

    def test_bool_custom_function_is_not_misdeclared_as_validation_result(self):
        source = SOURCE / "a2ui_custom_functions/02_boolean_return/1_custom_is_vip.json"
        new = migrate_document(json.loads(source.read_text()))
        components = new["messages"][1]["updateComponents"]["components"]
        button = next(component for component in components if component["id"] == "vip_btn")
        self.assertEqual("numeric", button["@checks"][0]["condition"]["@call"])
        self.assertEqual(3, button["@checks"][0]["condition"]["args"]["min"])
        info = next(component for component in components if component["id"] == "info")
        self.assertIn("${isVip(level:${/userLevel})}", info["text"]["args"]["value"])

    def test_id_collisions_are_rejected(self):
        stream = [{"version": "v0.9", "updateComponents": {"surfaceId": "test", "components": [
            {"id": "item-text", "component": "Text", "text": "A"}, {"id": "item_text", "component": "Text", "text": "B"}]}}]
        with self.assertRaisesRegex(ValueError, "collision"):
            migrate_messages(stream)

    def test_custom_styles_are_kept_and_use_an_explicit_extension_catalog(self):
        stream = [{"version": "v0.9", "createSurface": {"surfaceId": "test"}},
                  {"version": "v0.9", "updateComponents": {"surfaceId": "test", "components": [
                      {"id": "root", "component": "Text", "text": "A", "styles": {"fontSize": 24, "textColor": "#123456"}}]}}]
        new = migrate_messages(stream)
        self.assertEqual("kuikly_a2ui_catalog_v1_0.json", new[0]["createSurface"]["catalogId"])
        self.assertEqual(stream[1]["updateComponents"]["components"][0]["styles"], new[1]["updateComponents"]["components"][0]["styles"])


if __name__ == "__main__":
    unittest.main()
