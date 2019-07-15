package com.nttdata.de.lib.exception;

import org.apache.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Ein StandardException-Manager behandelt unchecked-exceptions. Dazu delegiert
 * er die jeweilige Exception an einen registrierten Exception-Handler. Bei der Auswahl
 * wird der am meisten spezialisierte Handler bzgl. der Exception-Klasse ausgewaehlt. 
 * Es sind Handler fuer die Standard-Runtime-Exceptions registriert. 
 * Diese koennen durch eigene Handler ergaenzt oder registrierte durch eigene ersetzt werden.      
 * 
 * @version 1.0.0
 * 
 * @author <a href="mailto:michael.adams@cirquent.de">Michael Adams</a>
 */
@SuppressWarnings("unchecked")
public class StandardExceptionManager implements ExceptionManager {

	private static Logger logger = Logger
			.getLogger(StandardExceptionManager.class);

	private final static ThreadLocal exceptionManagers = new ThreadLocal();
	
	private final static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		
	static TreeSet<Class> sortedExceptionHandlerCls = new TreeSet<>(new Comparator() {
		public int compare(Object o1, Object o2) {
			Class c1 = (Class) o1;
			Class c2 = (Class) o2;
			if (c1 == c2)
				return 0;
			if (c1.isAssignableFrom(c2)) {
				return 1;
			} else {
				return -1;
			}
		}
	});
	
	static {
		sortedExceptionHandlerCls.add(InvocationTargetException.class);		
		sortedExceptionHandlerCls.add(TunnelException.class);
		sortedExceptionHandlerCls.add(RuntimeException.class);
		sortedExceptionHandlerCls.add(UnsupportedOperationException.class);
		sortedExceptionHandlerCls.add(IndexOutOfBoundsException.class);
		sortedExceptionHandlerCls.add(IllegalStateException.class);
		sortedExceptionHandlerCls.add(IllegalArgumentException.class);
		sortedExceptionHandlerCls.add(NullPointerException.class);
	}
	
	static final String standard_exc_msg = "Ein unbekannter Fehler ist aufgetreten.";
	
	static public interface ExceptionHandler {
		void handleException(Throwable t);
	}
	
	static public class AbstractExceptionHandler implements ExceptionHandler {
		
		protected ExceptionPropagator propagator = null;
		
		public void handleException(Throwable t) {
			getPropagator().propagateException(t);
		}
		
		public ExceptionPropagator hasPropagator() {
			return propagator;
		}

		public ExceptionPropagator getPropagator() {
			if (propagator == null)
				propagator = new DefaultExceptionPropagator();
			return propagator;
		}

		public void setPropagator(ExceptionPropagator propagator) {
			this.propagator = propagator;
		}
	}
	
	static class IllegalArgumentExceptionHandler extends AbstractExceptionHandler {}
	static class IllegalStateExceptionHandler extends AbstractExceptionHandler {}
	static class IndexOutOfBoundsExceptionHandler extends AbstractExceptionHandler	{}
	static class UnsupportedOperationExceptionHandler extends AbstractExceptionHandler {}
	static class RuntimeExceptionHandler extends AbstractExceptionHandler {}
	static class TunnelExceptionHandler extends AbstractExceptionHandler {

		@Override
		public void handleException(Throwable t) {
			StandardExceptionManager.getNextExceptionManager().handleException(t.getCause());
		}
		
	}
	static class InvocationTargetExceptionHandler extends AbstractExceptionHandler {

		@Override
		public void handleException(Throwable t) {
			Throwable wrapped = t;
			while (wrapped instanceof InvocationTargetException
					&& wrapped.getCause() != null) {
				wrapped = wrapped.getCause();
			}
			StandardExceptionManager.getNextExceptionManager().handleException(wrapped);
		}
	}
	static class SystemExitExceptionHandler extends AbstractExceptionHandler {

		@Override
		public void handleException(Throwable t) {
			logger.warn("Exit of JVM cancelled.", t);
		}		
	}
	static class NullPointerExceptionHandler extends AbstractExceptionHandler {}

	static public interface ExceptionPropagator {

		void propagateException(Throwable t);
		void propagateException(Throwable t, String msg);
	}

	static public class DefaultExceptionPropagator implements ExceptionPropagator {
		
		public void propagateException(Throwable t) {
			logger.fatal(standard_exc_msg, t);
		}
		public void propagateException(Throwable t, String msg) {
			logger.fatal(msg, t);
		}
	}
	
	/**
	 * Ermittelt den aktuellen Exception-Handler.
	 * 
	 * @return Das Ergebnis der Ausführung.
	 */
	public static StandardExceptionManager getNextExceptionManager() {
		Stack<StandardExceptionManager> curExceptionManagers = (Stack<StandardExceptionManager>)exceptionManagers.get();
		return curExceptionManagers.lastElement();
	}

	/**
	 * Ermittelt den aktuellen Exception-Handler und entfernt ihn aus der Liste der 
	 * registrierten Exception-Handler.
	 * 
	 * @return Das Ergebnis der Ausführung.
	 */
	public static StandardExceptionManager pop() {
		Stack<StandardExceptionManager> curExceptionManagers = (Stack<StandardExceptionManager>)exceptionManagers.get();
		return curExceptionManagers.pop();
	}
	
	/**
	 * Registriert einen neuen Exception-Handler.
	 * 
	 * @return Der registrierte Exception-Handler.
	 */
	public static StandardExceptionManager push() {
		//logger.info("=========== push for thread " + Thread.currentThread().getName());
		Stack<StandardExceptionManager> curExceptionManagers = (Stack<StandardExceptionManager>)exceptionManagers.get();
		if (curExceptionManagers == null) {
			exceptionManagers.set(curExceptionManagers = new Stack<StandardExceptionManager>());
		}
		StandardExceptionManager curExceptionManager = curExceptionManagers.push(new StandardExceptionManager());
		curExceptionManager.initHandlers();
		return curExceptionManager;
	}
	
	public static String getStandard_exc_msg() {
		return standard_exc_msg;
	}

	HashMap<Class, ExceptionHandler> handlers = null;

	/**
	 * Behandelt eine Exception.
	 * 
	 * @param t
	 *     Die Exception, welche behandelt werden soll. 
	 */
	public void handleException(Throwable t) {
		for (Class<Throwable> handlerClass : sortedExceptionHandlerCls) {
			if (handlerClass.isAssignableFrom(t.getClass())) {
					ExceptionHandler handler = handlers.get(handlerClass);
					handler.handleException(t);
					return;
			}
		}
	}

	private void initHandlers() {
		Class current = null;
		try {
			handlers = new HashMap<Class, ExceptionHandler>();
			for (Class cls : sortedExceptionHandlerCls) {
				String handlerName = new StringBuffer(
						"StandardExceptionManager$")
						.append(cls.getSimpleName()).append("Handler")
						.toString();
				handlers.put((current = cls), (ExceptionHandler) Class.forName(
						handlerName).newInstance());
			}
		} catch (Throwable e) {
			handlers.put(current, new AbstractExceptionHandler());
		}
	}
	
	/**
	 * Gibt an, ob zu der angegebenen Exception ein Exception-Handler existiert. 
	 * 
	 *     Die Klasse, für die der Handler ermittelt werden soll.
	 * 
	 * @return Das Ergebnis der Ausführung.
	 */
	public boolean isHandledException(Throwable t) {
		for (Method m : getClass().getMethods()) {
			if (m.getName().equals("handleException")
					&& m.getParameterTypes()[0].isAssignableFrom(t.getClass()))
				return true;
		}
		return false;
	}

	/**
	 * Setzt ein Flag, welches angibt, ob bei Auftreten einer Exception eine 
	 * Exception vom Typ ExitException geworfen wird. Diese Exception wird 
	 * vom Framework entsprechend behandelt und führt in der Regel zu einem
	 * Programmabbruch oder dem Abbruch des aktuellen Testfalls. 
	 * 
	 * @param exitOnError
	 *     Flag, welches angibt ob eine ExitException geworfen wird. 
	 */
	public void setExitOnError(boolean exitOnError) {
		boolean exitOnError1 = exitOnError;
	}
	
	/**
	 * Setzt einen Exception-Propagator, der von einem Exception-Handler verwendet wird, 
	 * um eine Nachricht im Falle einer Exception zu generieren. 
	 * 
	 * @param exceptionClass
	 *     Die Klasse, für die der Propagator gesetzt werden soll.
	 * @param propagator
	 *     Der Propagator, welcher für das entsprechende Handler-Objekt gesetzt wird.     
	 */
	public void setExceptionPropagator(Class exceptionClass, ExceptionPropagator propagator) {
		((AbstractExceptionHandler)handlers.get(exceptionClass)).setPropagator(propagator);
	}
	
	/**
	 * Gibt an, ob der Handler zu der angegebenen Klasse einen
	 * Propagator besitzt. 
	 * 
	 * @param exceptionClass
	 *     Die Klasse, für die der Propagator ermittelt werden soll.
	 * 
	 * @return Das Ergebnis der Ausführung.
	 */
	public boolean hasExceptionPropagator(Class exceptionClass) {
		return ((AbstractExceptionHandler)handlers.get(exceptionClass)).hasPropagator() != null;
	}
	
	/**
	 * Registriert einen Exception-Handler für eine Exception-Klasse. Ist die Klasse bereits
	 * vorhanden, wird der Handler für diese Klasse neu gesetzt. 
	 * 
	 * @param exceptionClass
	 *     Die Klasse, für die der Handler gesetzt werden soll.
	 * @param handler
	 *     Der zu registrierende Handler.     
	 */
	public void setExceptionHandler(Class exceptionClass, ExceptionHandler handler) {		
		if (!handlers.containsKey(exceptionClass)) {
			if (!RuntimeException.class.isAssignableFrom(exceptionClass))
				throw new RuntimeException();
			sortedExceptionHandlerCls.add(exceptionClass);
		}
		handlers.put(exceptionClass, handler);
	}
		
	static public class RunnableBody {
		public void run() {			
		}
	}
		
	/*
	 * Ermüglicht die asynchrone Ausführung des übergebenen Programmcodes
	 * innerhalb der Klammer eines Exception-Managers.
	 * 
	 * @param runnable
	 *     Der asynchron auszuführende Programmcode.
	 */
	static public synchronized Future<?> runAsync(
			final StandardExceptionManager.RunnableBody runnable) {		
		try {
			return cachedThreadPool.submit(new Runnable() {
				public void run() {
					StandardExceptionManager currentExceptionManager = null;
					try {
						currentExceptionManager = push();						
						runnable.run();
					} catch (Throwable e) {
						assert currentExceptionManager != null;
						currentExceptionManager.handleException(e);
					} finally {
						pop();
					}
				}
			});
		} catch (Throwable e) {
			throw new RuntimeException();
		}
	}
	
	/*
	 * Ermüglicht die asynchrone Ausführung des übergebenen Programmcodes
	 * innerhalb der Klammer eines Exception-Managers und eines Exception-Handlers.
	 * 
	 * @param runnable
	 *     Der asynchron auszuführende Programmcode.
	 * @param exceptionClass
	 *     Die Klasse, für die ein Exception-Handler gesetzt werden soll.
	 * @param handler
	 *     Der zu registrierende Handler.
	 */
	static public synchronized Future<?> runAsyncWithExceptionAndHandler(
			final StandardExceptionManager.RunnableBody runnable,
			final Class exceptionClass, final AbstractExceptionHandler handler) {		
		try {
			return cachedThreadPool.submit(new Runnable() {
				public void run() {
					StandardExceptionManager currentExceptionManager = null;
					try {
						currentExceptionManager = push();
						currentExceptionManager.setExceptionHandler(
								exceptionClass, handler);
						runnable.run();
					} catch (Throwable e) {
						if (currentExceptionManager!=null) {
							currentExceptionManager.handleException(e);
						}
					} finally {
						pop();
					}
				}
			});
		} catch (Throwable e) {
			throw new RuntimeException();
		}
	}
}
