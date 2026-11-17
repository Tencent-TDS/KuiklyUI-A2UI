import { router } from '@kit.ArkUI';
import { A2UIDemoAssetUtil } from '../a2ui/A2UIDemoAssetUtil';

@Entry
@Component
struct AssetBrowserPage {
  @State directory: string = 'v0_9';
  @State folders: string[] = [];
  private filesSection: string = '{}';
  @State errorMessage: string = '';

  aboutToAppear(): void {
    const params = router.getParams() as Record<string, string>;
    this.directory = params ? params['directory'] || 'v0_9' : 'v0_9';
    const segments = this.directory.split('/');
    if ((segments[0] !== 'v0_9' && segments[0] !== 'v1_0') || segments.some((segment: string) => segment === '..' || segment.length === 0)) {
      this.errorMessage = '无效的版本目录';
      return;
    }
    const manifest = A2UIDemoAssetUtil.readRawFileSync(getContext(this).resourceManager, 'a2ui_manifest.json');
    this.folders = A2UIDemoAssetUtil.getStringArrayByKey(A2UIDemoAssetUtil.getObjectByKey(manifest, 'directories'), this.directory);
    this.filesSection = A2UIDemoAssetUtil.getObjectByKey(manifest, 'files');
    if (A2UIDemoAssetUtil.getStringArrayByKey(this.filesSection, this.directory).length > 0) {
      router.replaceUrl({
        url: 'pages/ComponentTestPage',
        params: { rootDir: this.directory, componentDir: '', componentName: this.directory, titleSuffix: '回归样本' }
      });
    }
  }

  private openDirectory(directory: string): void {
    if (A2UIDemoAssetUtil.getStringArrayByKey(this.filesSection, directory).length > 0) {
      router.pushUrl({
        url: 'pages/ComponentTestPage',
        params: { rootDir: directory, componentDir: '', componentName: directory, titleSuffix: '回归样本' }
      });
    } else {
      router.pushUrl({ url: 'pages/AssetBrowserPage', params: { directory: directory } });
    }
  }

  build() {
    Column() {
      Text(`←  ${this.directory.startsWith('v0_9') ? '0.9 兼容回归' : '1.0 回归样本'}`)
        .fontSize(20)
        .padding({ top: 48, bottom: 16 })
        .onClick(() => router.back())
      Text(this.directory).fontSize(13).fontColor('#666666').margin({ bottom: 16 })
      Scroll() {
        Column() {
          if (this.errorMessage.length > 0) {
            Text(this.errorMessage).fontColor('#CC0000')
          } else {
            ForEach(this.folders, (folder: string) => {
              Text(`${folder}  ›`)
                .fontSize(16)
                .padding(16)
                .width('100%')
                .backgroundColor(Color.White)
                .borderRadius(8)
                .margin({ bottom: 8 })
                .onClick(() => this.openDirectory(`${this.directory}/${folder}`))
            }, (folder: string) => folder)
            if (this.folders.length === 0) {
              Text('暂无样本')
            }
          }
        }
        .width('100%')
        .justifyContent(FlexAlign.Start)
        .alignItems(HorizontalAlign.Start)
      }
      .layoutWeight(1)
      .align(Alignment.Top)
    }
    .justifyContent(FlexAlign.Start)
    .padding({ left: 16, right: 16, bottom: 16 })
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F5F5')
  }
}
