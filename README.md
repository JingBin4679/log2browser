# log2browser
This library provides a way to get android app log from browser.

##集成方法
    1、新建一个android.app.Application的子类App,
    2、复写onCreate方法
    3、在onCreate中LogcatHelper.init();
    4、在AndroidManifest.xml的application节点中使用APP

参照app实现

    默认地址为：http://deviceIp:55552/util/logcat_console