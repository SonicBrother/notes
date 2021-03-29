# mybatis：xml和dao接口在同一个包

### 1.xml文件位置问题

~~~xml
<!--当mapper.xml文件放在接口的同包，没有放在resources文件夹时，可以在maven中进行以下配置：-->
  <build>
    <resources>
      <resource>
        <directory>src/main/java</directory><!--所在的目录-->
        <includes><!--包括目录下的.properties,.xml 文件都会扫描到-->
          <include>**/*.properties</include>
          <include>**/*.xml</include>
        </includes>
        <filtering>false</filtering>
      </resource>
    </resources>
  </build>
~~~

