import { hapTasks } from '@ohos/hvigor-ohos-plugin';
import { execSync } from 'child_process';
import * as path from 'path';

// Pre-build hook: sync demo assets from Android source-of-truth
const syncAssetsPlugin = {
  pluginId: 'sync-demo-assets',
  apply(pluginContext: any) {
    pluginContext.registerTask({
      name: 'syncDemoAssets',
      run: (taskContext: any) => {
        const scriptPath = path.resolve(__dirname, '../../sync_demo_assets.sh');
        console.log(`[syncDemoAssets] Running: ${scriptPath} ohos`);
        try {
          execSync(`bash "${scriptPath}" ohos`, { stdio: 'inherit' });
        } catch (e) {
          console.error('[syncDemoAssets] Failed to sync assets:', e);
        }
      },
      dependencies: ['default@PreBuild'],
      postDependencies: ['default@MergeProfile']
    });
  }
};

export default {
  system: hapTasks,
  plugins: [syncAssetsPlugin]
}
