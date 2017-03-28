package cn.iot.m2m.gatewaybridge.radius;

import cn.iot.m2m.radius.dictionary.Dictionary;
import cn.iot.m2m.radius.dictionary.DictionaryParser;
import cn.iot.m2m.radius.dictionary.MemoryDictionary;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 *
 * @createTime: 2015-4-20 上午11:26:31
 * @author: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @version: 0.1
 * @lastVersion: 0.1
 * @updateTime: 
 * @updateAuthor: <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
 * @changesSum: 
 * 
 */
public class GPRSDictionary extends MemoryDictionary {
	
	private String dictionaryResource;

	private Dictionary instance;

	public String getDictionaryResource() {
		return dictionaryResource;
	}

	public void setDictionaryResource(String dictionaryResource) {
		this.dictionaryResource = dictionaryResource;
	}


	/**
	 * 由Spring管理并加载该类
	 * @return
	 * @throws IOException
	 * @auther <a href="mailto:yuyang@iot.chinamobile.com">Tibbers</a>
	 * 2015-4-21 上午10:08:38
	 */
	public Dictionary initDictionary() throws IOException {
		if(instance == null) {
			InputStream source = this.getClass().getClassLoader().getResourceAsStream(dictionaryResource);
			DictionaryParser.parseDictionary(source, this);
			instance = this;
		}
		return instance;
	}
}
