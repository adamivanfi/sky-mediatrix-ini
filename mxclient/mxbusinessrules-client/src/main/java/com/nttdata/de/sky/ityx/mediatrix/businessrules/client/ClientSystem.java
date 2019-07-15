package com.nttdata.de.sky.ityx.mediatrix.businessrules.client;

import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.Actions;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.api.interfaces.businessrules.client.IClientSystem;
import de.ityx.mediatrix.data.CheckList;
import de.ityx.mediatrix.data.Question;
import de.ityx.mediatrix.modules.businessrules.data.BRSession;
import de.ityx.mediatrix.modules.businessrules.tools.BRSessionPoolFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ClientSystem implements IClientSystem {

	public static final String	QUESTION_ID		= "questionId";

	private static Integer		myOperatorId	= null;

	public HashMap setKeywords(Question p0, List<de.ityx.mediatrix.data.Keyword> p1, int p2, HashMap<String, Object> p3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().debug(logPrefix + ": enter");
		return new HashMap();
	}

	public void OnShutDown() {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().debug(logPrefix + ": enter");
	}

	public void OnStartUp() {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().info(logPrefix + ": enter");
	}

	public void actionCheckList(CheckList p0, Question p1, boolean p2, HashMap p3) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().debug(logPrefix + ": enter");
	}

	public void changeAddresses(Object p0) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().debug(logPrefix + ": enter");
	}



	public Object preClientExchange(String category, String command, int id, String oper, Object parameter, Integer operatorId, Integer integer) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().debug(logPrefix + ": category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);
		if (category != null && command != null && category.equals("ServletBearbeitung") && command.equals("getNeueFrage")) {
			BRSession session = BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyReindex");
			Integer questionId = (Integer) session.getPersistent(QUESTION_ID);
			SkyLogger.getClientLogger().debug(logPrefix + ": " + category + " " + command + " " + QUESTION_ID + "=" + questionId);
			if (questionId != null) {
				Question question = API.getClientAPI().getQuestionAPI().load(questionId);
				session.removeParamter(QUESTION_ID);
			}
		}
		//perfomance optimierung sucheKundeByEmail, s. mail von Garbe. 09.06.2015 ITYX Vorgangs-ID: 5227802 | Frage-ID: 4743795

		if (command!=null && "getProjectInfo".equals(command)) {
			List<Object> aparameters=new LinkedList<>();
			aparameters.add(new Integer(110));
			aparameters.add(new Integer(id));
			aparameters.add(new Integer(integer));
			return API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_GETPROJECTINFO.name(), aparameters);
		}else if (command!=null && "sucheKundeByEmail".equals(command)) {
			logPrefix+=":"+Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name()+":C:";

			SkyLogger.getClientLogger().debug(logPrefix + ":pre: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);

			List<Object> aparameters=new LinkedList<>();
			//aparameters.add(Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name());
			
			aparameters.add(new Integer(110));
			SkyLogger.getClientLogger().debug(logPrefix + ": :middle:loadingParam: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);

			aparameters.add(new Integer(id));
			aparameters.add(oper==null?"":oper);
			aparameters.add(operatorId==null?0:operatorId);
			aparameters.add(integer==null?0:integer);

			SkyLogger.getClientLogger().debug(logPrefix + ": :loadingParam2: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);
			//if (parameter instanceof Collection) {
			//	aparameters.addAll((Collection) parameter);
			//}else if (parameter!=null) {
				aparameters.add(parameter);
			//}

			SkyLogger.getClientLogger().debug(logPrefix + ": :preRemote: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);
			command="ping";
			parameter="no-reply@sky.de";

			return API.getClientAPI().getConnectionAPI().sendServerEvent(Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name(), aparameters);
			//List<Customer> customerList=new LinkedList<Customer>();
			//SkyLogger.getClientLogger().debug(logPrefix + ": :postRemote: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);

			//int counter=(customerList!=null)?customerList.size():0;

			//if (counter==0){
				//customerList= (List<Customer>) API.getClientAPI().getConnectionAPI().exchange(Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name(), Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.toString(), id, parameter);
			//}

			//SkyLogger.getClientLogger().info(logPrefix + ": :post: counter:" + counter + " category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);



			//return customerList;
			//List outboundparams=new LinkedList();
			//outboundparams.add(new Integer(110));
			//outboundparams.add(customerList);
			//return outboundparams;

			//ErpsData vielleichtso=new ErpsData();
			//vielleichtso.setContainer(customerList);
			//return vielleichtso;
		}

		return null;
	}

	public Object postClientExchange(String category, String command, int id, String oper, Object parameter, Integer operatorId, Integer integer, Object params) {
		Class clazz = getClass();
		String name = new Object() {
		}.getClass().getEnclosingMethod().getName();
		String logPrefix = clazz.getName() + "#" + name;
		SkyLogger.getClientLogger().debug(logPrefix + ": postExchange category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer);

		if (command!=null && "sucheKundeByEmail".equals(command)) {
			logPrefix+=":"+Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name()+":C:F:";
			SkyLogger.getClientLogger().debug(logPrefix + ":INSKBE: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer+" Params:"+params);

		}
		if (command!=null && "ping".equals(command)) {
			SkyLogger.getClientLogger().info(logPrefix + ":INPING1: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer + " Params:" + params);
			if (params!=null && params instanceof LinkedList){
				logPrefix+=":"+Actions.ACTION_LOAD_CUSTOMER_BYEMAIL.name() + ":C:F2:";
				SkyLogger.getClientLogger().info(logPrefix + ":INPING2: category=" + category + ": command=" + command + " id=" + id + ": oper=" + oper + ": parameter=" + parameter + ": operatorId=" + operatorId + ": integer=" + integer+" Params:"+params);
				List inboundparams=(List) params;
				return inboundparams.get(1);
			}
		}
		return null;
	}
	/**
	 * @param operatorId
	 */
	protected void setSessionOperatorId(Integer operatorId) {
		System.err.println("myOperatorId: " + myOperatorId);
		// if (myOperatorId == null) {
		BRSession session = BRSessionPoolFactory.getBRSessionPool().getBRSession("SkyOperator");
		session.put("operatorId", operatorId);
		myOperatorId = operatorId;
		// }
	}
}
