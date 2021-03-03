# 枚举的使用

- ex

```java
public enum CountryEnum {

    ONE(1, "齐"), TWO(2, "楚"), THREE(3, "燕"),
    FOUR(4, "赵"), FIVE(5, "魏"), SIX(6, "韩");

    private Integer retCode;
    private String retMsg;

    private CountryEnum(Integer retCode, String retMsg){
        this.retCode = retCode;
        this.retMsg = retMsg;
    }

    //枚举值已经设定好了,不需要有set方法
    public Integer getRetCode() {
        return retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public static CountryEnum forEach_CountryEnum(int index) {
        CountryEnum[] values = CountryEnum.values();
        for (CountryEnum element : values) {
            if (index == element.getRetCode()) {
                return element;
            }
        }
        return null;
    }
}
```

~~~java
public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 1; i <= 6; i++) {
            new Thread(CountryEnum.forEach_CountryEnum(i).getRetMsg()){
                @Override
                public void run() {
                    System.out.println(getName() + "\t国被灭.");
                    //countDownLatch.countDown();
                }
            }.start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "\t秦国一统天下.");
    }
~~~

