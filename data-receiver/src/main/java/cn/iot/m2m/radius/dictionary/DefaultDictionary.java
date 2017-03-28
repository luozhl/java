package cn.iot.m2m.radius.dictionary;

import java.io.IOException;
import java.io.InputStream;

/**
 * @createTime: 2015-4-22 上午9:11:11
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime:
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum:
 */
public class DefaultDictionary extends MemoryDictionary {

    private static final String DICTIONARY_RESOURCE = "cn/iot/m2m/radius/dictionary/default_dictionary";

    private static DefaultDictionary instance = null;

    /**
     * Make constructor private so that a DefaultDictionary
     * cannot be constructed by other classes.
     */
    private DefaultDictionary() {
    }

    /**
     * Returns the singleton instance of this object.
     *
     * @return DefaultDictionary instance
     */
    public static Dictionary getDefaultDictionary() {
        if (instance == null) {
            try {
                instance = new DefaultDictionary();
                InputStream source = instance.getClass().getClassLoader().getResourceAsStream(DICTIONARY_RESOURCE);
                DictionaryParser.parseDictionary(source, instance);
            } catch (IOException e) {
                throw new RuntimeException("default dictionary unavailable", e);
            }
        }
        return instance;
    }


}
