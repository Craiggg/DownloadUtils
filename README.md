## DownloadUtils
用法：
>可以选择直接copy项目到AS，添加为AndroidModel；
>或者copy代码。

撸代码：
```java
new SimpleFileDownload.Builder()
		.setUrl(downUrl)//添加你的下载连接
		.autoInstall(null);//是否下载完自动安装，传入context就代表是，传入null就代表否
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
