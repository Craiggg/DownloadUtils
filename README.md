# DownloadUtils

###两行代码搞定：

第一种是自动弹窗到前台显示下载进度：
参数说明：

第一个参数：context；

第二个参数：是否显示dialog，是的话就自动显示Progressdialog，此时不用添加任何监听事件，故第三个参数可为null；

第三个参数：listener，可以在下载前，下载中，下载后安装前，网络出错，这四个时机回调运行。
```Java
DownloadFileUtils.getInstand().initDownloadFileUtils(MainActivity.this, true, null);
```
第一个参数:下载URL，测试时的土方法，直接网上找酷市场下载的URL，不过它一直更新，可能现在这里的已经不能用了。

第二个参数：文件名字，若为空，会用URl来作为文件名字。
```Java
DownloadFileUtils.getInstand().DownloadFileOnNewThread(downUrl,filename);
```

示例：

![][示例图片]

[示例图片]:https://github.com/Craiggg/DownloadUtils/blob/master/ScreenRecorder_Exported_20160812174900.gif


也可以通过建立后台服务来下载：
```java
 DownLoadService.setInitData(downUrl, filename, -1);
 startService(new Intent(MainActivity.this, DownLoadService.class));//开启服务启动后台下载处理。问题：app退出后，服务没有退出，若是绑定，切换Activity时，服务不能保证继续运行。
```


