# windows端口被占用
方法一：

cmd --->>>> netstat -aon|findstr 1099

taskkill -f -pid 查询到占用的

就OK了
