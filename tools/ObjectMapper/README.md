# [ObjectMapper使用](https://www.cnblogs.com/xuwenjin/p/8976696.html)

在项目中使用到了ObjectMapper，故研究了一下。现将自己的几个测试用例和大家分享一下~

首先在pom.xml文件中，加入依赖：

```
　　　　<dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.8.3</version>
        </dependency>
```

 创建一个实体类XwjUser：

```
public class XwjUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String message;

    private Date sendTime;

    // 这里手写字母大写，只是为了测试使用，是不符合java规范的
    private String NodeName;

    private List<Integer> intList;

    public XwjUser() {
        super();
    }

    public XwjUser(int id, String message, Date sendTime) {
        super();
        this.id = id;
        this.message = message;
        this.sendTime = sendTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getNodeName() {
        return NodeName;
    }

    public void setNodeName(String nodeName) {
        NodeName = nodeName;
    }

    public List<Integer> getIntList() {
        return intList;
    }

    public void setIntList(List<Integer> intList) {
        this.intList = intList;
    }

    @Override
    public String toString() {
        return "XwjUser [id=" + id + ", message=" + message + ", sendTime=" + sendTime + ", intList=" + intList + "]";
    }

}
```

先创建一个ObjectMapper，然后赋值一些属性：

```
public static ObjectMapper mapper = new ObjectMapper();

static {
    // 转换为格式化的json
    mapper.enable(SerializationFeature.INDENT_OUTPUT);

    // 如果json中有新增的字段并且是实体类类中不存在的，不报错
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
}
```

 

#### 1、对象与json字符串、byte数组

```
@Test
    public void testObj() throws JsonGenerationException, JsonMappingException, IOException {
        XwjUser user = new XwjUser(1, "Hello World", new Date());

        mapper.writeValue(new File("D:/test.txt"), user); // 写到文件中
        // mapper.writeValue(System.out, user); //写到控制台

        String jsonStr = mapper.writeValueAsString(user);
        System.out.println("对象转为字符串：" + jsonStr);

        byte[] byteArr = mapper.writeValueAsBytes(user);
        System.out.println("对象转为byte数组：" + byteArr);

        XwjUser userDe = mapper.readValue(jsonStr, XwjUser.class);
        System.out.println("json字符串转为对象：" + userDe);

        XwjUser useDe2 = mapper.readValue(byteArr, XwjUser.class);
        System.out.println("byte数组转为对象：" + useDe2);
    }
```

运行结果：

```
对象转为字符串：{
  "id" : 1,
  "message" : "Hello World",
  "sendTime" : 1525163446305,
  "intList" : null,
  "nodeName" : null
}
对象转为byte数组：[B@3327bd23
json字符串转为对象：XwjUser [id=1, message=Hello World, sendTime=Tue May 01 16:30:46 CST 2018, intList=null]
byte数组转为对象：XwjUser [id=1, message=Hello World, sendTime=Tue May 01 16:30:46 CST 2018, intList=null]
```

注意，对象转json字符串时，对象中的NodeName首字母是大写，转出来是小写

#### 2、list集合与json字符串

```
@Test
    public void testList() throws JsonGenerationException, JsonMappingException, IOException {
        List<XwjUser> userList = new ArrayList<>();
        userList.add(new XwjUser(1, "aaa", new Date()));
        userList.add(new XwjUser(2, "bbb", new Date()));
        userList.add(new XwjUser(3, "ccc", new Date()));
        userList.add(new XwjUser(4, "ddd", new Date()));

        String jsonStr = mapper.writeValueAsString(userList);
        System.out.println("集合转为字符串：" + jsonStr);
        
        List<XwjUser> userListDes = mapper.readValue(jsonStr, List.class);
        System.out.println("字符串转集合：" + userListDes);
    }
```

运行结果：

```
集合转为字符串：[ {
  "id" : 1,
  "message" : "aaa",
  "sendTime" : 1525164096846,
  "intList" : null,
  "nodeName" : null
}, {
  "id" : 2,
  "message" : "bbb",
  "sendTime" : 1525164096846,
  "intList" : null,
  "nodeName" : null
}, {
  "id" : 3,
  "message" : "ccc",
  "sendTime" : 1525164096846,
  "intList" : null,
  "nodeName" : null
}, {
  "id" : 4,
  "message" : "ddd",
  "sendTime" : 1525164096846,
  "intList" : null,
  "nodeName" : null
} ]
字符串转集合：[{id=1, message=aaa, sendTime=1525164096846, intList=null, nodeName=null}, {id=2, message=bbb, sendTime=1525164096846, intList=null, nodeName=null}, {id=3, message=ccc, sendTime=1525164096846, intList=null, nodeName=null}, {id=4, message=ddd, sendTime=1525164096846, intList=null, nodeName=null}]
```

#### 3、map与json字符串

```
@SuppressWarnings("unchecked")
    @Test
    public void testMap() {
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("name", "merry");
        testMap.put("age", 30);
        testMap.put("date", new Date());
        testMap.put("user", new XwjUser(1, "Hello World", new Date()));

        try {
            String jsonStr = mapper.writeValueAsString(testMap);
            System.out.println("Map转为字符串：" + jsonStr);
            try {
                Map<String, Object> testMapDes = mapper.readValue(jsonStr, Map.class);
                System.out.println("字符串转Map：" + testMapDes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
```

运行结果：

```
Map转为字符串：{
  "date" : 1525164199804,
  "name" : "merry",
  "user" : {
    "id" : 1,
    "message" : "Hello World",
    "sendTime" : 1525164199805,
    "intList" : null,
    "nodeName" : null
  },
  "age" : 30
}
字符串转Map：{date=1525164199804, name=merry, user={id=1, message=Hello World, sendTime=1525164199805, intList=null, nodeName=null}, age=30}
```

#### 4、修改转换时的日期格式：

```
@Test
    public void testOther() throws IOException {
        // 修改时间格式
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        XwjUser user = new XwjUser(1, "Hello World", new Date());
        user.setIntList(Arrays.asList(1, 2, 3));

        String jsonStr = mapper.writeValueAsString(user);
        System.out.println("对象转为字符串：" + jsonStr);
    }
```



运行结果：

```
对象转为字符串：{
  "id" : 1,
  "message" : "Hello World",
  "sendTime" : "2018-05-01 16:44:06",
  "intList" : [ 1, 2, 3 ],
  "nodeName" : null
}
```



 