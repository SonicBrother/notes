## MySql基本语法

### 1. 连接数据库

~~~cmd
mysql -u用户名 -p密码（-p和密码之间不能有空格）
~~~

##### 备份库中的数据和

~~~sql
- mysqldump -u 用户名 -p 数据库名 > 文件名.sql【window命令】
- Source 文件名.sql【在库下执行】
- mysql -uroot -p mydb1<c:\test.sql  (window命令)
~~~



### 2. 对库的操作

#### 创建库

~~~sql
CREATE  DATABASE  [IF NOT EXISTS] 库名
[DEFAULT] CHARACTER SET 字符名 |  [DEFAULT] COLLATE 校对规则 
~~~

#### 查看库

~~~sql
- SHOW DATABASES
- SHOW CREATE DATABASE 库名【查看数据库创建时的详细信息】
~~~

#### 删除库

~~~sql
DROP DATABASE  [IF EXISTS]  库名 
~~~

#### 修改库

~~~sql
ALTER  DATABASE  [IF NOT EXISTS] 库名
[DEFAULT] CHARACTER SET 字符名   | [DEFAULT] COLLATE 校对规则
~~~



### 3. 对表的操作

##### 增加表

~~~sql
CREATE TABLE 表名(      列名    类型          )
~~~

##### 修改表

~~~sql
- ALTER TABLE 表名
  ADD   ( 列名  数据类型 );
- ALTER TABLE 表名
  MODIFY( 列名  数据类型 );
- ALTER TABLE表名
  DROP(列名);
~~~

##### 查看表

~~~sql
- SHOW TABLES;
- SHOW CREATE TABLE 表名【查看表的创建细节】
- DESC 表名【查看表的结构】
~~~

##### 删除表

~~~sql
DROP TABLE table_name ;
~~~



### 4. 对表中数据操作

##### 增加

~~~sql
INSERT INTO 表名 ( 列名..) VALUES  (数据..);
~~~

##### 修改

~~~sql
UPDATE  表名
SET 列名=值.. , 列名=值
WHERE=条件 
~~~

##### 删除

~~~sql
- DELETE FROM 表名  WHERE=条件
- TRUNCATE TABLE【先摧毁整张表，再创建表结构】
~~~

##### 查看

~~~sql
SELECT 列名
FROM 表名,
WHERE 条件,
GROUP BY 列名,
HAVING BY,
ORDER BY 列名
LIMITE
~~~



