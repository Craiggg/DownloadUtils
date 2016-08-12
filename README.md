# DownloadUtils

两行代码搞定：

参数说明：

第一个参数：context；

第二个参数：是否显示dialog，是的话就自动显示Progressdialog，此时不用添加任何监听事件，故第三个参数可为null；

第三个参数：listener，可以在下载前，下载中，下载后安装前，网络出错，这四个时机回调运行。

DownloadFileUtils.getInstand().initDownloadFileUtils(MainActivity.this, true, null);

第一个参数:下载URL，测试时的土方法，直接网上找酷市场下载的URL，不过它一直更新，可能现在这里的已经不能用了。

第二个参数：文件名字，若为空，会用URl来作为文件名字。

DownloadFileUtils.getInstand().DownloadFileOnNewThread(downUrl,filename);


示例：

![https://github.com/Craiggg/DownloadUtils/blob/master/ScreenRecorder_Exported_20160812174900.gif][示例图片](https://github.com/Craiggg/DownloadUtils/blob/master/ScreenRecorder_Exported_20160812174900.gif)


