package com.nttdata.de.sky.webservice.service;

import com.nttdata.de.ityx.cx.workflow.outgoing.mxpostprocessing.MxDbSingleton;
import com.nttdata.de.ityx.sharedservices.utils.ShedulerUtils;
import com.nttdata.de.lib.logging.SkyLogger;
import com.nttdata.de.sky.ityx.common.DocIdGenerator;
import com.nttdata.de.sky.ityx.common.PushToUserClient;
import com.nttdata.de.sky.ityx.common.PushToUserMessageScm;
import com.nttdata.de.sky.ityx.common.TagMatchDefinitions;
import com.nttdata.de.ws.sky.if141.scmconnector._1.PortType;
import com.nttdata.de.ws.sky.if141.scmconnector._1.PushNewQuestionToAgentRequest;
import com.nttdata.de.ws.sky.if141.scmconnector._1.PushNewQuestionToAgentResponse;
import de.ityx.mediatrix.api.API;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Question;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by meinusch on 03.08.15.
 */



@WebService(targetNamespace = "http://sky.ws.de.nttdata.com/if141/ScmConnector/1.0",
		endpointInterface = "com.nttdata.de.ws.sky.if141.scmconnector._1.PortType",
		portName = "ScmConnectorPort",
		wsdlLocation = "WEB-INF/wsdl/scmConnector.wsdl",
		serviceName = "ScmConnector")

public class ScmPushServerImpl implements PortType {

	public static final int SUCCESS = 0;
	public static final int ERROR = 1;

	private PushToUserClient puc;

	public synchronized PushToUserClient getPushClient() {
		if (puc == null || !puc.isReady()) {
			puc = new PushToUserClient();
		}
		return puc;
	}

	@WebMethod
	@Override
	public PushNewQuestionToAgentResponse pushNewQuestionToAgent(
			@WebParam(name = "pushNewQuestionToAgentRequest", targetNamespace = "http://sky.ws.de.nttdata.com/if141/ScmConnector/1.0", partName = "pushNewQuestionToAgentRequest") PushNewQuestionToAgentRequest pushNewQuestionToAgentRequest) {


		SkyLogger.getConnectorLogger().info("IF_WSSCM: START WS-Call for params: u:" + pushNewQuestionToAgentRequest.getMxLogin() + " c:" + pushNewQuestionToAgentRequest.getCustomerId());


		Question question = new Question();

		Connection con = null;
		try {
			con = MxDbSingleton.getMxConnection("");
			String documentid = DocIdGenerator.createUniqueDocumentId(con, TagMatchDefinitions.DocumentDirection.INBOUND, TagMatchDefinitions.Channel.SOCIALMEDIA, new Date());
			question.setBody(pushNewQuestionToAgentRequest.getHtmlContent());
			question.setProjectId(110);
			question.setSubprojectId(900); //ToDo: Prototype only

			String headers = question.getHeaders();
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CUSTOMER_ID, pushNewQuestionToAgentRequest.getCustomerId());
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.X_TAGMATCH_CUSTOMER_ID, pushNewQuestionToAgentRequest.getCustomerId());
			//headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.TAGMATCH_CONTRACT_NUMBER, message.getContractid());
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.DOCUMENT_ID, documentid);
			headers = TagMatchDefinitions.addOrReplaceXTMHeader(headers, TagMatchDefinitions.CHANNEL, TagMatchDefinitions.Channel.SOCIALMEDIA.name());
			question.setHeaders(headers);
			question.setType(Question.TYPE_EMAIL);
			Case qcase=new Case();
			Customer custObj = new Customer((question.getProjectId() > 0) ? question.getProjectId() : 110);
			//custObj.setId(ServerSequencer.GetId("kunde"));

			custObj.setFirstname("Test");
			custObj.setName("Test");

			ShedulerUtils.quickCheckLicence("IF_WSSCM"+pushNewQuestionToAgentRequest.getMxLogin());
			ShedulerUtils.checkRuntimeLicense("IF_WSSCM"+pushNewQuestionToAgentRequest.getMxLogin());
			boolean stored=false;
			try{
				API.getServerAPI().getCustomerAPI().store(con, custObj, true);
				qcase.setCustomerId(custObj.getId());
				API.getServerAPI().getCaseAPI().store(con, qcase);
			  	question.setCaseId(qcase.getId());
				SkyLogger.getCommonLogger().info("ScmPushServer.QStore1 Generated docid:" + documentid + " frage:" + question.getId());
				stored=API.getServerAPI().getQuestionAPI().store(con, question);
				SkyLogger.getCommonLogger().info("ScmPushServer.QStore2 Generated docid:" + documentid + " frage:" + question.getId());

			} catch (SQLException eee) {
				SkyLogger.getConnectorLogger().warn("IF_WSSCM: StoringProblem! WS-Call for params: u:" + pushNewQuestionToAgentRequest.getMxLogin() + " c:" + pushNewQuestionToAgentRequest.getCustomerId()+" msg:"+eee.getMessage(),eee);
				ShedulerUtils.resetAuth("IF4.6" + documentid);

				API.getServerAPI().getCustomerAPI().store(con, custObj, true);
				qcase.setCustomerId(custObj.getId());
				API.getServerAPI().getCaseAPI().store(con, qcase);
				question.setCaseId(qcase.getId());
				SkyLogger.getCommonLogger().info("ScmPushServer.QStore3 Generated docid:" + documentid + " frage:" + question.getId());
				stored = API.getServerAPI().getQuestionAPI().store(con, question);
				SkyLogger.getCommonLogger().info("ScmPushServer.QStore4 Generated docid:" + documentid + " frage:" + question.getId());

			}
			if (!con.getAutoCommit()) {
				con.commit();
			}
			//hier reindexing dann einbauen
			if (!stored){
				SkyLogger.getConnectorLogger().error("IF_WSSCM: NOTSTORED! WS-Call for params: u:" + pushNewQuestionToAgentRequest.getMxLogin() + " c:" + pushNewQuestionToAgentRequest.getCustomerId());
			}
		} catch (SQLException e) {
			SkyLogger.getConnectorLogger().error("IF_WSSCM: Problem WS-Call for params: u:" + pushNewQuestionToAgentRequest.getMxLogin() + " c:" + pushNewQuestionToAgentRequest.getCustomerId() + " msg:" + e.getMessage(), e);
		} finally {
			MxDbSingleton.closeConnection(con);
		}
		SkyLogger.getConnectorLogger().info("IF_WSSCM: START WS-Call for params: u:" + pushNewQuestionToAgentRequest.getMxLogin() + " c:" + pushNewQuestionToAgentRequest.getCustomerId() + " q:" + question.getId());


		PushNewQuestionToAgentResponse resp=new PushNewQuestionToAgentResponse();
		try {
			getPushClient().sendMessage(new PushToUserMessageScm(pushNewQuestionToAgentRequest.getMxLogin(), System.currentTimeMillis() + "", pushNewQuestionToAgentRequest.getCustomerId(), question.getId()));
			resp.setErrorCode(SUCCESS);
		} catch (Exception e) {
			SkyLogger.getConnectorLogger().error("IF_WSSCM: START WS-Call for params: u:" + pushNewQuestionToAgentRequest.getMxLogin() + " c:" + pushNewQuestionToAgentRequest.getCustomerId() + " e:" + e.getMessage(), e);
			resp.setErrorCode(ERROR);
			resp.setErrorMsg(e.getMessage());
		}
		return resp;
	}


}
