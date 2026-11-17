import copy
import json
import unittest

from migrate_demo_v1_0 import ASSETS, PILOTS, dynamic, heading, migrate


class DemoMigrationTest(unittest.TestCase):
    def source(self, relative):
        return json.loads((ASSETS / "v0_9" / relative).read_text(encoding="utf-8"))

    def test_all_pilots_preserve_envelope_order_and_surface_ids(self):
        for relative in PILOTS:
            with self.subTest(relative=relative):
                source = self.source(relative)
                original = copy.deepcopy(source)
                result = migrate(source)
                self.assertEqual(original, source)
                self.assertEqual(len(source["messages"]), len(result["messages"]))
                for old, new in zip(source["messages"], result["messages"]):
                    branch = next(key for key in old if key != "version")
                    self.assertEqual(set(old), set(new))
                    self.assertEqual(old[branch]["surfaceId"], new[branch]["surfaceId"])
                    self.assertEqual("1.0", new["version"])
                    if branch == "updateDataModel" and old[branch]["surfaceId"] != "test-tf-regexp":
                        self.assertEqual(old[branch], new[branch])

    def test_collection_path_is_not_a_dynamic_binding(self):
        result = migrate(self.source("a2ui_databinding_tests/03_child_list_template/1_column_template.json"))
        components = result["messages"][1]["updateComponents"]["components"]
        children = next(item for item in components if item["id"] == "list")["children"]
        self.assertEqual({"componentId": "todo_tpl", "path": "/todos"}, children)
        self.assertEqual({"@call": "formatString", "args": {"value": "• ${title}"}}, next(item for item in components if item["id"] == "todo_tpl")["text"])

    def test_dynamic_heading_remains_root_bound(self):
        self.assertEqual({"@call": "formatString", "args": {"value": "### ${/title}"}}, heading(dynamic({"path": "/title"}), 3))
        self.assertEqual({"@call": "formatString", "args": {"value": "### ${icon}"}}, heading(dynamic({"path": "icon"}), 3))

    def test_legacy_regexp_is_explicitly_redesigned_as_an_editable_binding(self):
        source = self.source("a2ui_components/textfield/6_textfield_email_checks_vs_legacy_regexp.json")
        pattern = source["messages"][1]["updateComponents"]["components"][-1]["validationRegexp"]
        result = migrate(source)
        fields = result["messages"][1]["updateComponents"]["components"]
        regexp = fields[-1]
        self.assertEqual({"@path": "/emailRegexp"}, regexp["value"])
        self.assertNotIn("validationRegexp", regexp)
        self.assertEqual(pattern, regexp["@checks"][0]["condition"]["args"]["pattern"])
        self.assertEqual({"email": "", "emailRegexp": ""}, result["messages"][-1]["updateDataModel"]["value"])
        self.assertNotIn("returnType", fields[-2]["@checks"][0]["condition"])

    def test_colliding_component_ids_are_not_silently_merged(self):
        source = self.source(PILOTS[0])
        source["messages"][1]["updateComponents"]["components"].append({"id": "main_column", "component": "Text", "text": "collision"})
        with self.assertRaisesRegex(ValueError, "colliding"):
            migrate(source)

    def test_styles_and_themes_cannot_be_deleted_to_fake_compliance(self):
        source = self.source(PILOTS[0])
        source["messages"][1]["updateComponents"]["components"][0]["styles"] = {"padding": 8}
        with self.assertRaisesRegex(ValueError, "styles"):
            migrate(source)
        source = self.source(PILOTS[0])
        source["messages"][0]["createSurface"]["theme"] = {"primaryColor": "#FF0000"}
        with self.assertRaisesRegex(ValueError, "Theme"):
            migrate(source)

    def test_mixed_version_stream_is_rejected(self):
        source = self.source(PILOTS[0])
        source["messages"][1]["version"] = "1.0"
        with self.assertRaisesRegex(ValueError, "uniform"):
            migrate(source)

    def test_unsupported_dynamic_headings_fail_instead_of_freezing_values(self):
        with self.assertRaisesRegex(ValueError, "manual review"):
            heading({"@call": "formatDate", "args": {"value": {"@path": "/date"}}}, 3)


if __name__ == "__main__":
    unittest.main()
