# [CopyOnWriteArrayList,CopyOnWriteArraySet源码分析](https://www.cnblogs.com/zofun/p/12206924.html)

## 概述

`CopyOnWriteArrayList`是一个线程安全的`ArrayList`，通过内部的`volatile`数组和显示锁ReentrantLock来实现线程安全。`CopyOnWriteArraySet`的底层也是基于`CopyOnWriteArrayList`实现的。`CopyOnWriteArrayList`更适合于读多写少的环节。

## CopyOnWriteArrayList源码分析

### 核心属性

```java
Copy    /** The lock protecting all mutators */
    final transient ReentrantLock lock = new ReentrantLock();
    /** 用于存储元素的volatile修饰的内部数组 */
    private transient volatile Object[] array;
```

它的内部属性也非常的简单,值得注意的是array是通过volatile修饰的。

### 重要方法分析

#### add方法

```java
Copy    public boolean add(E e) {
        final ReentrantLock lock = this.lock;
        lock.lock();//加锁
        try {
        //拿到原数组
            Object[] elements = getArray();
            int len = elements.length;
            //将原数组拷贝到原长度+1的新数组中
            Object[] newElements = Arrays.copyOf(elements, len + 1);
            //将元素加入
            newElements[len] = e;
            //用新数组替代原数组
            setArray(newElements);
            return true;
        } finally {
        //解锁
            lock.unlock();
        }
    }
```

从这段代码我们可以看出`CopyOnWriteArrayList`每次添加元素都是直接创建一个长度为原数组长度加一的新数组，然后将该旧数组的数据复制到新数组中。然后将待添加的元素添加到新数组的最后一个位置。最后将旧数组用新数组替换掉。

### get

```java
Copy    public E get(int index) {
        return get(getArray(), index);
    }
    
        private E get(Object[] a, int index) {
        return (E) a[index];
    }
```

get方法非常的简单，不过我们需要注意一点**get方法并没有做同步**。

通过看了add和get两个方法的实现。我们可以发现add是使用了同步的，而get没有使用同步。在这个地方我的理解是：get之所以不需要同步，这是因为get的读取，本质上是“快照读”。add方法z每次都要复制一份，因为这样写操作不会影响读的。

至于为什么add要加锁，这个也非常好理解，因为如果不加锁的话，会出现更新丢失。

## CopyOnWriteArraySet源码分析

### 核心属性

```java
Copy    private final CopyOnWriteArrayList<E> al;
```

`CopyOnWriteArraySet`内部其实只有一个`CopyOnWriteArrayList`.

### 重要方法

#### add方法

```java
Copy    public boolean add(E e) {
        return al.addIfAbsent(e);
    }
    
    public boolean addIfAbsent(E e) {
        Object[] snapshot = getArray();
        //如果数组中已经存在e，则返回false，否则调用addIfAbsent
        return indexOf(e, snapshot, 0, snapshot.length) >= 0 ? false :
            addIfAbsent(e, snapshot);
    }
    
    private boolean addIfAbsent(E e, Object[] snapshot) {
        final ReentrantLock lock = this.lock;
        lock.lock();//加锁
        try {
            Object[] current = getArray();//拿到当前的数组
            int len = current.length;
            if (snapshot != current) {
                // Optimize for lost race to another addXXX operation
                int common = Math.min(snapshot.length, len);
                for (int i = 0; i < common; i++)
                    if (current[i] != snapshot[i] && eq(e, current[i]))
                    //在添加过程中有其它线程插入的元素
                        return false;
                if (indexOf(e, current, common, len) >= 0)
                        return false;
            }
            Object[] newElements = Arrays.copyOf(current, len + 1);
            //将当前元素加入到了新数组的最后一个位置
            newElements[len] = e; 
            setArray(newElements);
            return true;
        } finally {
            lock.unlock();
        }
    }
    
    //作用就是在数组中查找是否o已经存在，包括null
    private static int indexOf(Object o, Object[] elements,
                               int index, int fence) {
        if (o == null) {
        //如果o为空，遍历找到数组中第一个同样为空的位置
            for (int i = index; i < fence; i++)
                if (elements[i] == null)
                    return i;
        } else {
        //如果o不为空，则找到与之相等的元素的位置
            for (int i = index; i < fence; i++)
                if (o.equals(elements[i]))
                    return i;
        }
        //数组中没有该元素，返回-1
        return -1;
    }
```

整个插入过程：

- 调用`indexOf`方法，查看快照数组中是否已经有该元素了（包括null），如果已经有该元素了，那么返回false，否则进行调用`addIfAbsent`添加元素
- `addIfAbsent`方法全程加锁。首先将之前的快照与当前数组快照进行比较，如果当前数组快照相较于之前数组快照已经发送了改变，那么说明已经有线程完成了添加，那么当前线程竞争失败，直接返回false（为了避免之前线程更新丢失）。如果没有改变。那么就复制一个长度是原数组长度加一的数组，然后将元素添加到尾部，更新数组。

通过源码我们可以知道，`CopyOnWriteArraySet`允许加入null。并且`CopyOnWriteArraySet`保证元素不重复，就是简简单单的遍历查找一遍。

作者：[ zofun](https://www.cnblogs.com/zofun/)

出处：https://www.cnblogs.com/zofun/p/12206924.html

版权：本站使用「[CC BY 4.0](https://creativecommons.org/licenses/by/4.0)」创作共享协议，转载请在文章明显位置注明作者及出处。