package com.nttdata.de.sky.ityx.common;

import com.nttdata.de.lib.logging.SkyLogger;
import de.ityx.check.DeadLockSync;
import de.ityx.mediatrix.data.Case;
import de.ityx.mediatrix.data.Customer;
import de.ityx.mediatrix.data.Email;
import de.ityx.mediatrix.data.Question;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by meinusch on 04.08.15.
 */

public class DocumentIDSyncFactory extends DeadLockSync {

	private final class LinkedHashMapTimeExpiringExtension<K, V extends TimedSyncObject> extends LinkedHashMap<K, V> {

		private static final long serialVersionUID = 5791085894023771237L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
			return eldest.getValue().created < (System.currentTimeMillis() - (5 * 60 * 100)); //5min expiring time
		}
	}

	protected Map<String, TimedSyncObject> syncMap;

	public DocumentIDSyncFactory() {
		syncMap = Collections.synchronizedMap(new LinkedHashMapTimeExpiringExtension<String, TimedSyncObject>());
	}

	@Override
	public Object getSyncObject(String methodName, Object... args) {
		String logpreafix = Thread.currentThread().getId() + ":" + Thread.currentThread().getName() + ":";

		Object syncType = null;

		//if (methodName.contains("store") || methodName.contains("set") ){
		if (methodName.contains("store") || methodName.contains("set")) {
			String id = "";
			for (Object arg : args) {
				boolean nolockingOnEmptyObject = false;
				if (arg instanceof Question) {
					Question q = (Question) arg;
					id += arg.getClass().getName() + "_" + q.getId() + "_" + q.getEmailId() + "_" + q.getCaseId() + "_" + q.getDocId();
				} else if (arg instanceof Email) {
					Email e = (Email) arg;
					id += arg.getClass().getName() + "_" + e.getEmailId() + "_" + e.getMessageId();
				} else if (arg instanceof Customer) {
					Customer c = (Customer) arg;
					String lockingparam = c.getId() + "_" + c.getExternalId() + "_" + Thread.currentThread().getId();
					if (c.getId() == 0) {
						lockingparam = c.getId() + "_" + c.getExternalId() + "_" + c.getName() + "_" + c.getEmail() + "_" + c.getCreatedAt() + "_" + c.getProjectId() + c.getLockedBy();
						if (c.getExternalId() == null || c.getExternalId().isEmpty() || c.getExternalId().equals("0")) {
							//	lockingparam=lockingparam+System.currentTimeMillis();
							//lockingparam += '_' + Thread.currentThread().getId();
							SkyLogger.getCommonLogger().info(logpreafix + "DocumentIDSyncFactory:EmptyCustomer:" + lockingparam);
							nolockingOnEmptyObject = true;
							/*if (SkyLogger.getCommonLogger().isDebugEnabled()) {
								String stacktrace = "";
								for (StackTraceElement e : Thread.currentThread().getStackTrace()) {
									stacktrace += " => " + e.getClassName() + ">" + e.getMethodName() + ":" + e.getLineNumber() + "\r\n";
								}
								SkyLogger.getCommonLogger().debug(logpreafix+" Problem durign collection of params: stacktrace:" + stacktrace);
							}*/
						}
					}
					id += arg.getClass().getName() + "_" + lockingparam;
					SkyLogger.getCommonLogger().info(logpreafix + "DocumentIDSyncFactory:Customer:" + lockingparam);
				} else if (arg instanceof Case) {
					Case v = (Case) arg;
					id += arg.getClass().getName() + "_" + v.getId() + "_" + v.getExternalId() + "_" + v.getCustomerId();
				} else if (arg.getClass().getName().contains("mediatrix.d")) {
					// irgendwas komisches, aus den Attributen und Namen kein rückschluss möglicb
					nolockingOnEmptyObject = true;
				} else if (arg instanceof Boolean) {
					//kommt in EmailDaemon häufig vor
					nolockingOnEmptyObject = true;
				} else if (arg instanceof Integer) {
					nolockingOnEmptyObject = true;
				} else if (arg instanceof Long) {
					nolockingOnEmptyObject = true;
				} else {
					nolockingOnEmptyObject = true;
					SkyLogger.getCommonLogger().info("DocumentIDSyncFactory:UnknownObject: " + methodName + "/" + arg.getClass().getName());
				}
				if (!nolockingOnEmptyObject) {
					syncType = arg;
				} else {
					id += methodName + "_" + arg.getClass().getName() + "_" + arg + "_" + Thread.currentThread().getId();
				}
				SkyLogger.getCommonLogger().debug("DocumentIDSyncFactory:" + methodName + "/" + id);
			}

			TimedSyncObject o = null;
			if (methodName.equals("store") && !id.isEmpty()) {
				o = syncMap.get(id);
				if (o != null) {
					SkyLogger.getCommonLogger().debug("DocumentIDSyncFactory:reusing: " + methodName + "/" + o.getClass().getName() + " id:" + id);
				}
			}
			if (o == null) {
				if (args.length > 1) {
					o = new MultiSyncObject();
				} else {
					o = getSyncOForObject(syncType);
				}
				if (methodName.equals("store")) {
					syncMap.put(id, o);
					SkyLogger.getCommonLogger().debug(logpreafix + "DocumentIDSyncFactory:Object:" + id);
				} else {
					SkyLogger.getCommonLogger().debug("DocumentIDSyncFactory:emptyObject: " + methodName + "/" + o.getClass().getName());
				}
			}
			return o;
		} else {
			String margs = "";
			for (Object arg : args) {
				margs += arg.getClass().getName() + ";";
			}
			SkyLogger.getCommonLogger().debug("DocumentIDSyncFactory:UnknownMethod:" + methodName + " argsType:" + margs);
		}
		return super.getSyncObject(methodName, args);
	}

	public TimedSyncObject getSyncOForObject(Object arg) {
		if (arg == null) {
			return new TimedSyncObject();
		} else if (arg instanceof Question) {
			return new QuestionSyncObject();
		} else if (arg instanceof Email) {
			return new EmailSyncObject();
		} else if (arg instanceof Customer) {
			return new CustomerSyncObject();
		} else if (arg instanceof Case) {
			return new CaseSyncObject();
		} else if (arg.getClass().getName().contains("mediatrix.d")) {
			return new FunctionSyncObject();
		} else if (arg instanceof Boolean) {
			return new FunctionSyncObject();
		} else if (arg instanceof Integer) {
			return new IntSyncObject();
		} else if (arg instanceof Long) {
			return new LongSyncObject();
		} else {
			SkyLogger.getCommonLogger().info("getSyncOForObject:UnknownObject: " + arg.getClass().getName());
			return new TimedSyncObject();
		}
	}

	public class TimedSyncObject extends SyncObject {
		protected final long created = System.currentTimeMillis();
	};

	public class MultiSyncObject extends TimedSyncObject {};

	public class QuestionSyncObject extends TimedSyncObject {};

	public class CaseSyncObject extends TimedSyncObject {};

	public class EmailSyncObject extends TimedSyncObject {};

	public class CustomerSyncObject extends TimedSyncObject {};

	public class FunctionSyncObject extends TimedSyncObject {};

	public class IntSyncObject extends TimedSyncObject {};

	public class LongSyncObject extends TimedSyncObject {};
}
