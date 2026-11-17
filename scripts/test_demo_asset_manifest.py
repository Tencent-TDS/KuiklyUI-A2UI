import json
import tempfile
import unittest
from pathlib import Path

from generate_demo_asset_manifest import ROOT, VERSIONS, build_manifest, check_synced_assets


class DemoAssetManifestTest(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(dir=ROOT / "scripts")
        self.source = Path(self.temporary.name)
        for version in VERSIONS:
            (self.source / version).mkdir()

    def tearDown(self):
        self.temporary.cleanup()

    def write_asset(self, relative, versions):
        asset = self.source / relative
        asset.parent.mkdir(parents=True, exist_ok=True)
        asset.write_text(json.dumps({"messages": [{"version": v} if v is not None else {} for v in versions]}))

    def test_nested_directories_and_direct_files_are_enumerable(self):
        self.write_asset("v0_9/a2ui_components/text/1_text.json", ["v0.9"])
        self.write_asset("v1_0/a2ui_v10_tests/1_inline.json", ["1.0", "v1.0"])
        manifest = build_manifest(self.source)
        self.assertEqual(["a2ui_components"], manifest["directories"]["v0_9"])
        self.assertEqual(["text"], manifest["directories"]["v0_9/a2ui_components"])
        self.assertEqual(["1_text.json"], manifest["files"]["v0_9/a2ui_components/text"])
        self.assertEqual(["1_inline.json"], manifest["files"]["v1_0/a2ui_v10_tests"])

    def test_missing_version_is_not_inherited(self):
        self.write_asset("v1_0/a2ui_v10_tests/1_bad.json", ["1.0", None])
        with self.assertRaisesRegex(ValueError, "Version mismatch or missing version"):
            build_manifest(self.source)

    def test_cross_version_messages_are_rejected_in_both_directions(self):
        for directory, versions in [("v0_9", ["v0.9", "1.0"]), ("v1_0", ["1.0", "v0.9"])]:
            with self.subTest(directory=directory):
                self.write_asset(f"{directory}/1_bad.json", versions)
                with self.assertRaisesRegex(ValueError, "Version mismatch or missing version"):
                    build_manifest(self.source)
                (self.source / directory / "1_bad.json").unlink()

    def test_empty_message_stream_is_rejected(self):
        self.write_asset("v0_9/1_empty.json", [])
        with self.assertRaisesRegex(ValueError, "Missing messages"):
            build_manifest(self.source)

    def test_legacy_patch_version_is_supported(self):
        self.write_asset("v0_9/1_patch.json", ["v0.9.1"])
        self.assertEqual(["1_patch.json"], build_manifest(self.source)["files"]["v0_9"])

    def test_missing_changed_and_extra_synced_files_are_rejected(self):
        self.write_asset("v1_0/1_case.json", ["1.0"])
        target = self.source / "synced"
        with self.assertRaisesRegex(ValueError, "synchronized assets"):
            check_synced_assets(self.source, [target])
        copy = target / "v1_0/1_case.json"
        copy.parent.mkdir(parents=True)
        copy.write_bytes((self.source / "v1_0/1_case.json").read_bytes())
        check_synced_assets(self.source, [target])
        copy.write_bytes(b"changed")
        with self.assertRaisesRegex(ValueError, "synchronized assets"):
            check_synced_assets(self.source, [target])
        copy.write_bytes((self.source / "v1_0/1_case.json").read_bytes())
        (copy.parent / "2_extra.json").write_bytes(b"{}")
        with self.assertRaisesRegex(ValueError, "synchronized assets"):
            check_synced_assets(self.source, [target])


if __name__ == "__main__":
    unittest.main()
