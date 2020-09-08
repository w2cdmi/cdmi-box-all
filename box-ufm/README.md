#box-ufm

------------------------------------
**部署备注**
在手工部署该模块时候，需要手工在sysdb库中执行以下脚本：
INSERT INTO db_addr ( dbId,masterAddr,slaveAddr,mainAddr) VALUES ('sysdb','192.168.30.134','192.168.30.130','192.168.30.131'); 
INSERT INTO db_addr ( dbId,masterAddr,slaveAddr,mainAddr) VALUES ('logdb','192.168.30.134','192.168.30.130','192.168.30.131');
INSERT INTO db_addr ( dbId,masterAddr,slaveAddr,mainAddr) VALUES ('userdb_1','192.168.30.134','192.168.30.130','192.168.30.131'); 
其中IP地址要替换为实际部署的。第一个是服务IP，第二个是本地IP，第三个是slaver节点IP

------------------------------------

#1.5.4.2

#meixiaoqiang 20161218
#打包的时候关于license
#关闭license文件为licensechecker.java

#修改assembly打包成toolkit规范
#增加keystory目录
#修正deploy目录
#liujinghua 20161219
#修改FileBaseServiceImpl.checkSpaceAndFileCount企业容量算法有问题
