package com.nttdata.de.ityx.cx.sky.routing;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Creates initial routing table for contex.
 * First copy /ityx/src/main/resources/ContexRouting.csv to  <mediatrix-base>/conf/server then
 * call once from <mediatrix-base>/libs with:
 * java -Duser.home=<mediatrix-base>/conf/server -cp clientlibs\ityx-0.0.1-SNAPSHOT.jar;common\jpa-api.jar;common\hibernate-entitymanager.jar;common\hibernate-core.jar;common\hibernate-annotations.jar;common\dom4j.jar;common\hibernate-commons-annotations.jar;common\slf4j-api.jar;common\slf4j-jdk14.jar;common\javassist.jar;common\commons-collections.jar;common\jta.jar;db\ojdbc6_g-11.1.0.7.0.jar ReadCSVMapping
 *  
 * @author DHIFLM
 *
 */
public class ReadCSVMapping {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		final String home = System.getProperty("ityx.home","");
		Properties p = new Properties();
		p.load(new FileInputStream(System.getProperty("de.ityx.contex.config",home+File.separator+"contex.properties")));
		final String server =  p.getProperty("host","muc-ityx-02.de.softlab.net");
		final String port =  p.getProperty("port","1525");
		final String database =  p.getProperty("database","orcl");
		final String user = p.getProperty("login","DHIFLM_CONTEX_TEST");
		final String password = p.getProperty("password","CQ");
		p.setProperty("hibernate.dialect","org.hibernate.dialect.Oracle10gDialect");                             
		p.setProperty("hibernate.connection.driver_class", "oracle.jdbc.driver.OracleDriver");                         
		p.setProperty("hibernate.connection.url", "jdbc:oracle:thin:@"+server+":"+port+":"+database);     
		p.setProperty("hibernate.connection.username", user);                                    
		p.setProperty("hibernate.connection.password", password);
		p.setProperty("hibernate.hbm2ddl.auto", "update");
		EntityManagerFactory pf = Persistence.createEntityManagerFactory("ityxPersistenceUnit",p);
		EntityManager em = pf.createEntityManager();
		em.getTransaction().begin();
		List<InternalRoutingEntry> list = new ArrayList<>();
		BufferedReader in = new BufferedReader(new FileReader(home+File.separator+ "ContexRouting.csv"));
		String line;
		while ((line = in.readLine()) != null) {
			String[] entry = line.split(";");
			final InternalRoutingEntry routingEntry = new InternalRoutingEntry(
					list.size() + 1,
					InternalRoutingEntry.Kundendaten.valueOf(entry[3]),
					entry[1], entry[0], entry[2]);
			list.add(routingEntry);
			em.persist(routingEntry);
		}
		em.getTransaction().commit();
	}

}
