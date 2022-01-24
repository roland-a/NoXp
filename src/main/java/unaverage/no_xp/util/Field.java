package unaverage.no_xp.util;

import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public final class Field<O, F> {
    private final java.lang.reflect.Field inner;
    
    public Field(Class<O> clazz, String name) {
        this(ObfuscationReflectionHelper.findField((Class)clazz, name));
    }
    
    private Field(java.lang.reflect.Field inner) {
        this.inner = inner;
    }
    
    public F get(O obj) {
        try {
            return (F)inner.get(obj);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void set(O obj, F value) {
        try {
            inner.set(obj, value);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
