# xhs-xp-rpc
小红书 xposed 相关 使用 adb 跳过登录 直接加载笔记数据，xposed hook 推送 response 到指定接口

小红书内容页
``` lua
Import "shanhai.lua"
shanhai.execute("am start -a android.intent.action.VIEW -d xhsdiscover://item/5f5b3fbc0000000001004c3e")
```
小红书个人主页
``` lua
Import "shanhai.lua"
shanhai.execute("am start -a android.intent.action.VIEW -d xhsdiscover://user/5f12d8cf0000000001004c15")
```
转成 adb 试试
