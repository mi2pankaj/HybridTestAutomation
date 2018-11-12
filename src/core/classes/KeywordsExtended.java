package core.classes;


import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.mysql.jdbc.Connection;

import net.lightbody.bmp.proxy.ProxyServer;


public class KeywordsExtended extends Keywords{

	Logger logger = Logger.getLogger(KeywordsExtended.class.getName());

	public KeywordsExtended(Connection connection, JSONObject jsonObjectRepo, ProxyServer proxyServer) {
		super(connection, jsonObjectRepo, proxyServer);
	}

}

