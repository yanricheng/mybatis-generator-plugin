import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yrc
 * @date 2021/5/25
 */
public class Test {

    private static final Map<String, Class<?>> namePrimitiveMap = new HashMap<>();

    static {
        namePrimitiveMap.put("boolean", Boolean.class);
        namePrimitiveMap.put("Boolean", Boolean.class);
        namePrimitiveMap.put("byte", Byte.class);
        namePrimitiveMap.put("Byte", Byte.class);
        namePrimitiveMap.put("char", Character.class);
        namePrimitiveMap.put("Character", Character.class);
        namePrimitiveMap.put("short", Short.class);
        namePrimitiveMap.put("Short", Short.class);
        namePrimitiveMap.put("int", Integer.class);
        namePrimitiveMap.put("Integer", Integer.class);
        namePrimitiveMap.put("long", Long.class);
        namePrimitiveMap.put("Long", Long.class);
        namePrimitiveMap.put("double", Double.class);
        namePrimitiveMap.put("Double", Double.class);
        namePrimitiveMap.put("float", Float.class);
        namePrimitiveMap.put("Float", Float.class);
        namePrimitiveMap.put("void", Void.class);
    }


    public static void main(String[] args) throws IOException {

        System.out.println(new File(".").getCanonicalPath());
        System.out.println(new File(".").getAbsolutePath());
        System.out.println(System.getProperty("user.dir"));

        System.out.println(namePrimitiveMap.get("void").getSimpleName());
    }
}
