package com.nttdata.de.lib.logging;

import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * The class <code>Message</code> is used to organize and format log-messsages according to the SKY aquirements. Each
 * <code>Message</code> consists of a severity-code, a unique error-number and an error-message. Messages can contain
 * wildcards.<p/> Messages can log themselve to a logger, if one is set in the constructor.
 * 
 * @author Michael Adams
 * 
 * @see #sprintf
 * @see #format
 */
public class Message {
    // constants
    /** the message severity critical. */
    public static final int CRITICAL = 0;
    /** the message severity major. */
    public static final int MAJOR = 1;
    /** the message severity minor. */
    public static final int MINOR = 2;
    /** the message severity warning. */
    public static final int WARNING = 3;
    /** the message severity normal. */
    public static final int NORMAL = 4;
    /** the message severity unknown. */
    public static final int UNKNOWN = 5;

    // static

    private static Calendar sCalendar = Calendar.getInstance(Locale.getDefault());

    private static String[] sSeverity = { "Crit", "Majo", "Mino", "Warn", "Norm", "Unkn" };
    private static final int TEXT_MAX_LENGTH = 220;
    public static final int OBJECT_MAX_LENGTH = 8;

    // private static methods

    // public static

    /**
     * Returns the severity value.
     * 
     * @param severity
     *            the integer representation of severity value (CRITICAL, WARNING, ...) {@link Message}
     * @return the severity value
     */
    public static String getSeverity(int severity) {
        return sSeverity[severity];
    }

    /**
     * Constructs a string from <code>message</code> replacing all wildcards by the corresponding parameters. Supported
     * wildcards are:
     * <ul>
     * <li>%s replace by the toString() result of the correpsonding argument</li>
     * </ul>
     * 
     * @param message
     *            the message with wildcards
     * @param args
     *            array of arguments that will be used to replace wildcards.
     * @return a formatted string with replaced wildcards
     */

    public static String sprintf(String message, Object[] args) {
        StringBuffer buffer = new StringBuffer(message.length());

        int arg = 0;
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c == '%') {
                switch (c = message.charAt(++i)) {
                case 's':
                    buffer.append(args[arg++]);
                    break;

              //  default:
             //       assert(false);
                } // switch
            } // if
            else {
                buffer.append(c);
            }
        } // for

        return buffer.toString();
    }

    /**
     * Convenience method.
     * 
     * @param message
     *            the message with wildcards
     * @param arg1
     *            argument that will be used to replace wildcards.
     * @return a formatted string with replaced wildcards
     * 
     * @see #sprintf(String message, Object[] args)
     */

    public static String sprintf(String message, Object arg1) {
        return sprintf(message, new Object[] { arg1 });
    }

    /**
     * Convenience method.
     * 
     * @param message
     *            the message with wildcards
     * @param arg1
     *            argument that will be used to replace wildcards.
     * @param arg2
     *            further argument that will be used to replace wildcards.
     * @return a formatted string with replaced wildcards
     * 
     * @see #sprintf(String message, Object[] args)
     */

    public static String sprintf(String message, Object arg1, Object arg2) {
        return sprintf(message, new Object[] { arg1, arg2 });
    }

    /**
     * Convenience method.
     * 
     * @param message
     *            the message with wildcards
     * @param arg1
     *            argument that will be used to replace wildcards.
     * @param arg2
     *            further argument that will be used to replace wildcards.
     * @param arg3
     *            further argument that will be used to replace wildcards.
     * @return a formatted string with replaced wildcards
     * 
     * @see #sprintf(String message, Object[] args)
     */

    public static String sprintf(String message, Object arg1, Object arg2, Object arg3) {
        return sprintf(message, new Object[] { arg1, arg2, arg3 });
    }

    /**
     * Convenience method.
     * 
     * @param message
     *            the message with wildcards
     * @param arg1
     *            argument that will be used to replace wildcards.
     * @param arg2
     *            further argument that will be used to replace wildcards.
     * @param arg3
     *            further argument that will be used to replace wildcards.
     * @param arg4
     *            further argument that will be used to replace wildcards.
     * @return a formatted string with replaced wildcards
     * 
     * @see #sprintf(String message, Object[] args)
     */

    public static String sprintf(String message, Object arg1, Object arg2, Object arg3, Object arg4) {
        return sprintf(message, new Object[] { arg1, arg2, arg3, arg4 });
    }

    /**
     * convenience method.
     * 
     * @param message
     *            the message with wildcards
     * @param arg1
     *            argument that will be used to replace wildcards.
     * @param arg2
     *            further argument that will be used to replace wildcards.
     * @param arg3
     *            further argument that will be used to replace wildcards.
     * @param arg4
     *            further argument that will be used to replace wildcards.
     * @param arg5
     *            further argument that will be used to replace wildcards.
     * @return a formatted string with replaced wildcards
     * 
     * @see #sprintf(String message, Object[] args)
     */

    public static String sprintf(String message, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return sprintf(message, new Object[] { arg1, arg2, arg3, arg4, arg5 });
    }

    // Format: yyyymmtthhmmss

    static synchronized void formatDate(StringBuffer buffer) {
        final DecimalFormat format2 = new DecimalFormat("00;00");
        final DecimalFormat format4 = new DecimalFormat("0000;0000");

        sCalendar.clear();
        sCalendar.setTime(new Date());

        buffer.append(format4.format(sCalendar.get(Calendar.YEAR)));
        buffer.append(format2.format(sCalendar.get(Calendar.MONTH) + 1));
        buffer.append(format2.format(sCalendar.get(Calendar.DAY_OF_MONTH)));
        buffer.append(format2.format(sCalendar.get(Calendar.HOUR_OF_DAY)));
        buffer.append(format2.format(sCalendar.get(Calendar.MINUTE)));
        buffer.append(format2.format(sCalendar.get(Calendar.SECOND)));
    }

    private static String[] sSpaces = { "", " ", "  ", "   ", "    ", "     ", "      ", "       ", "        " };

    private static String format(String severity, int code, Object object, String msg) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(severity);
        buffer.append(';');
        buffer.append(code);
        buffer.append(';');
        formatDate(buffer);
        buffer.append(';');
        String objectString = object.toString();
        if (objectString.length() > OBJECT_MAX_LENGTH) {
            objectString = objectString.substring(0, OBJECT_MAX_LENGTH); // 8
        } else if (objectString.length() < OBJECT_MAX_LENGTH) {
            objectString += sSpaces[OBJECT_MAX_LENGTH - objectString.length()];
        }

        buffer.append(objectString);
        buffer.append(';');

        if (msg.length() > TEXT_MAX_LENGTH) {
            msg = msg.substring(0, TEXT_MAX_LENGTH);
        }

        buffer.append(msg); // max 220

        return buffer.toString();
    }
    
    // instance data

    private int mSeverity;
    private int mCode;
    private String mMessage;

    /**
     * The logger to log the message to. May be null.
     */
    private Logger logger;

    /**
     * The logger to log the message and an exception to. May be null.
     */
    private Logger exceptionLogger;

    // constructor

    /**
     * create a new <code>Message</code>. The log methods may NOT be used.
     * 
     * @param severity
     *            the severity code.
     * @param code
     *            the error-number
     * @param msg
     *            he message
     */
    public Message(int severity, int code, String msg) {
        mSeverity = severity;
        mCode = code;
        mMessage = msg;
    }

    /**
     * create a new <code>Message</code>. The log methods may be used.
     * 
     * @param severity
     *            the severity code.
     * @param code
     *            the error-number
     * @param msg
     *            the message
     * @param logger
     *            the logger to log the message to.
     * @param exceptionLogger
     *            the logger to log the message with an exception to.
     */
    public Message(int severity, int code, String msg, Logger logger, Logger exceptionLogger) {
        this(severity, code, msg);
        this.logger = logger;
        this.exceptionLogger = exceptionLogger;
    }

    /**
     * Logs this message object to its logger.
     * 
     * @param args
     *            the args to fill into the message text
     * @return
     * @return returns the logged string
     */
    public String log(Object... args) {
        if (logger == null) {
            throw new IllegalStateException(
                    "Log methods cannot be used. Use the appropriate constructor of this message");
        }
        logger.info(args[0]);
        return (String)args[0];
    }

    /**
     * Logs this message object to its logger. Logs the exception to the exception log.
     * 
     * @param t
     *            the throwable to log
     * @param args
     *            the args to fill into the message text
     * @return
     * @return returns the logged string
     */
    public String log(Throwable t, Object... args) {
        String msg = log(args);
        exceptionLogger.fatal(msg, t);
        return msg;
    }

    // accessors

    /**
     * Eeturns the error-number.
     * 
     * @return the error-number
     */
    public int getCode() {
        return mCode;
    }

    /**
     * Returns the error-message.
     * 
     * @return the error-message
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Returns the severity-code.
     * 
     * @return the severity-code
     */
    public int getSeverity() {
        return mSeverity;
    }

    /**
     * Formats this message.
     * 
     * @param object
     *            an object describing the source of the error
     * @return the formatted message
     */
    public String format(Object object) {
        return format(getSeverity(getSeverity()), getCode(), object, getMessage());
    }

    /**
     * Formats this message.
     * 
     * @param object
     *            an object describing the source of the error
     * @param args
     *            an array of objects that will used to replace wildcards
     * @return the formatted message
     */

    public String format(Object object, Object[] args) {
        return format(getSeverity(getSeverity()), getCode(), object, sprintf(getMessage(), args));
    }

    /**
     * convenience method.
     * 
     * @param object
     *            an object describing the source of the error
     * @param arg1
     *            an object that will used to replace wildcards
     * @return the formatted message
     * 
     * @see #format(Object object, Object[] args)
     */
    public String format(Object object, Object arg1) {
        return format(getSeverity(getSeverity()), getCode(), object, sprintf(getMessage(), arg1));
    }

    /**
     * convenience method.
     * 
     * @param object
     *            an object describing the source of the error
     * @param arg1
     *            an object that will used to replace wildcards
     * @param arg2
     *            a further object that will used to replace wildcards
     * @return the formatted message
     * 
     * @see #format(Object object, Object[] args)
     */
    public String format(Object object, Object arg1, Object arg2) {
        return format(getSeverity(getSeverity()), getCode(), object, sprintf(getMessage(), arg1, arg2));
    }

    /**
     * convenience method.
     * 
     * @param object
     *            an object describing the source of the error
     * @param arg1
     *            an object that will used to replace wildcards
     * @param arg2
     *            a further object that will used to replace wildcards
     * @param arg3
     *            a further object that will used to replace wildcards
     * @return the formatted message
     * 
     * @see #format(Object object, Object[] args)
     */
    public String format(Object object, Object arg1, Object arg2, Object arg3) {
        return format(getSeverity(getSeverity()), getCode(), object, sprintf(getMessage(), arg1, arg2, arg3));
    }

    /**
     * convenience method.
     * 
     * @param object
     *            an object describing the source of the error
     * @param arg1
     *            an object that will used to replace wildcards
     * @param arg2
     *            a further object that will used to replace wildcards
     * @param arg3
     *            a further object that will used to replace wildcards
     * @param arg4
     *            a further object that will used to replace wildcards
     * @return the formatted message
     * 
     * @see #format(Object object, Object[] args)
     */
    public String format(Object object, Object arg1, Object arg2, Object arg3, Object arg4) {
        return format(getSeverity(getSeverity()), getCode(), object, sprintf(getMessage(), arg1, arg2, arg3, arg4));
    }

    /**
     * convenience method.
     * 
     * @param object
     *            an object describing the source of the error
     * @param arg1
     *            an object that will used to replace wildcards
     * @param arg2
     *            a further object that will used to replace wildcards
     * @param arg3
     *            a further object that will used to replace wildcards
     * @param arg4
     *            a further object that will used to replace wildcards
     * @param arg5
     *            a further object that will used to replace wildcards
     * @return the formatted message
     * 
     * @see #format(Object object, Object[] args)
     */
    public String format(Object object, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) {
        return format(getSeverity(getSeverity()), getCode(), object,
                sprintf(getMessage(), arg1, arg2, arg3, arg4, arg5));
    }
}
