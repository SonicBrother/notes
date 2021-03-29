## 如何理解在spring中，mapper接口的实现类由持久层框架进行创建，而不由spring创建？

#### 2.1原始版jdbc语句

~~~java
public class OrderForQuery {
	/*
	 * 针对于表的字段名与类的属性名不相同的情况：
	 * 1. 必须声明sql时，使用类的属性名来命名字段的别名
	 * 2. 使用ResultSetMetaData时，需要使用getColumnLabel()来替换getColumnName(),
	 *    获取列的别名。
	 *  说明：如果sql中没有给字段其别名，getColumnLabel()获取的就是列名
	 */
	@Test
	public void testOrderForQuery(){
		String sql = "select order_id orderId,order_name orderName,order_date orderDate from `order` where order_id = ?";
		Order order = orderForQuery(sql,1);
		System.out.println(order);
	}
	
	/**
	 * 
	 * @Description 通用的针对于order表的查询操作
	 * @author shkstart
	 * @date 上午10:51:12
	 * @return
	 * @throws Exception 
	 */
	public Order orderForQuery(String sql,Object...args){
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = JDBCUtils.getConnection();
			ps = conn.prepareStatement(sql);
			for(int i = 0;i < args.length;i++){
				ps.setObject(i + 1, args[i]);
			}
			
			//执行，获取结果集
			rs = ps.executeQuery();
			//获取结果集的元数据
			ResultSetMetaData rsmd = rs.getMetaData();
			//获取列数
			int columnCount = rsmd.getColumnCount();
			if(rs.next()){
				Order order = new Order();
				for(int i = 0;i < columnCount;i++){
					//获取每个列的列值:通过ResultSet
					Object columnValue = rs.getObject(i + 1);
					//通过ResultSetMetaData
					//获取列的列名：getColumnName() --不推荐使用
					//获取列的别名：getColumnLabel()
//					String columnName = rsmd.getColumnName(i + 1);
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					//通过反射，将对象指定名columnName的属性赋值为指定的值columnValue
					Field field = Order.class.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(order, columnValue);
				}
				return order;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			JDBCUtils.closeResource(conn, ps, rs);
		}
		return null;
	}
}
~~~

#### 2.2原始版mybatis语句

~~~java
public static void main(String[] args) throws IOException {
        //访问mybatis读取student数据
        //1.定义mybatis主配置文件的名称, 从类路径的根开始（target/clasess）
        String config="mybatis.xml";
        //2.读取这个config表示的文件
        InputStream in = Resources.getResourceAsStream(config);
        //3.创建了SqlSessionFactoryBuilder对象
        SqlSessionFactoryBuilder builder  = new SqlSessionFactoryBuilder();
        //4.创建SqlSessionFactory对象
        SqlSessionFactory factory = builder.build(in);
        //5.获取SqlSession对象，从SqlSessionFactory中获取SqlSession
        SqlSession sqlSession = factory.openSession();
        //6.【重要】指定要执行的sql语句的标识。  sql映射文件中的namespace + "." + 标签的id值
        //String sqlId = "com.bjpowernode.dao.StudentDao" + "." + "selectStudents";
        String sqlId = "com.bjpowernode.dao.StudentDao.selectStudents";
        //7.【重要】执行sql语句，通过sqlId找到语句
        List<Student> studentList = sqlSession.selectList(sqlId);
        //8.输出结果
        //studentList.forEach( stu -> System.out.println(stu));
        for(Student stu : studentList){
            System.out.println("查询的学生="+stu);
        }
        //9.关闭SqlSession对象
        sqlSession.close();
    }
~~~

~~~
//6.【重要】指定要执行的sql语句的标识。  sql映射文件中的namespace + "." + 标签的id值
String sqlId = "com.bjpowernode.dao.StudentDao.selectStudents";
//7.【重要】执行sql语句，通过sqlId找到语句
List<Student> studentList = sqlSession.selectList(sqlId);
	【最重要的两行代码】：
	定义好 namespace 和 对应方法签名后，mybatis根据方法签名可以找到对应的sql语句，在使用jdbc技术，进行查找封装，这其中是固定代码模板。
	【框架】：
	框架可以理解为半成品，提供固定模板，加入参数以后，可以实现功能。
~~~

#### 2.3最终实现

~~~java
    @Test
    public void testSelectStudents(){
        /**
         * 使用mybatis的动态代理机制， 使用SqlSession.getMapper(dao接口)
         * getMapper能获取dao接口对于的实现类对象。
         */
        SqlSession sqlSession = MyBatisUtils.getSqlSession();
        StudentDao dao  =  sqlSession.getMapper(StudentDao.class);

        //com.sun.proxy.$Proxy2 : jdk的动态代理
        System.out.println("dao="+dao.getClass().getName());
        //调用dao的方法， 执行数据库的操作
        List<Student> students = dao.selectStudents();
        for(Student stu: students){
            System.out.println("学生="+stu);
        }
    }
~~~

~~~
mybatis根据接口（接口全路径+方法签名），再结合jdbc的固定套路，可以实现对应的查找功能。
	因此：spring的dao层接口实现类是由持久层负责生成，而不是由框架生成。
~~~



