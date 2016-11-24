## DownloadUtils
用法：
>可以选择直接copy项目到AS，添加为AndroidModel；
>或者copy代码。

撸代码：
```java
new FileDownloadBuilder(FileDownloadBuilder.File_Type.APK)
		.setUrl(downUrl)//添加你的下载连接
		.setDownloadCallback(callback)//传入你的回调实例
		.creat()
		.start();
```
没了  
还有封装一些Service的后台下载，我这人比较懒，喜欢一条龙的玩意 （—。—）
在Service的onStartCommand中写：
```java
ServiceDownloadManager downloadManager = new ServiceDownloadManager(this);//this就是Service。。。
downloadManager.autoDownloadFile(downUrl);//传入String类型的下载链接
```
还有断点下载的功能没写，简单的轻量下载工具，目前是在更新APP用。
以下是需要的权限：
```java
<uses-permission android:name="android.permission.INTERNET"/>

<!-- 在SDCard中创建与删除文件权限 -->
<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
<!-- 往SDCard写入数据权限 -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<!-- 下拉系统状态栏的权限，利用反射调用的，不加Service可以不添加 -->
<uses-permission android:name="android.permission.EXPAND_STATUS_BAR"/>
```



