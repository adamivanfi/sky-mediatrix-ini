package com.nttdata.de.sky.ityx.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class ScriptRunner extends Timer {

    private static final long DEFAULT_LIMIT = 120000;

    private int mReturnValue = -1;
    private StringBuffer mResult = null;

    private InputStream mInputStream = null;

    public ScriptRunner() {
        super(false);
    }

    public synchronized StringBuffer execute(long timelimit, boolean isVBRunner, String... parameter) {
        mReturnValue = -1;
        mResult = new StringBuffer();
        mInputStream = null;

            try {
				executionLogic(timelimit, isVBRunner, parameter);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != mInputStream) {
					try {
						mInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				cancel();
			}
        return mResult;
    }

    /**
     *  
     * @param timelimit
     * @param isVBRunner true if the script to run is a VB script
     * @param parameter
     * @throws IOException
     * @throws InterruptedException
     */
    private void executionLogic(long timelimit, boolean isVBRunner, String... parameter) throws IOException, InterruptedException {
        String[] processBuilderInput = null;
        int offset;
        if (isVBRunner) {
        	offset = 4;
        	String[] param = new String[parameter.length + offset];
        	param[0] = ("cmd");
            param[1] = ("/c");
            param[2] = ("wscript.exe");
            param[3] = ("/nologo");
            for(int i=0; i<parameter.length;i++) {
                param[i+offset] = parameter[i];
            }
            processBuilderInput = param;
        } else {
        	offset = 2;
            String[] param = new String[parameter.length + offset];
            param[0] = "cmd";
            param[1] = "/c";
            for(int i=0; i<parameter.length;i++) {
                param[i+offset] = parameter[i];
            }
            processBuilderInput = param;
        }
        
        ProcessBuilder processBuilder = new ProcessBuilder(processBuilderInput);
        final Process process = processBuilder.start();
        if (timelimit > 0) {
            timer(timelimit, process);
        } else {
            timer(DEFAULT_LIMIT, process);
        }
        mInputStream = process.getInputStream();
        int readByte = -1;
        while (-1 != (readByte = mInputStream.read())) {
            mResult.append((char) readByte);
        }
        process.waitFor();
        mReturnValue = process.exitValue();
        
        if (0 != mReturnValue) {
            mResult = null;
        } else {
            mResult.trimToSize();
        }
    }
    
    private void timer(long timelimit, final Process process) throws NullPointerException {
        TimerTask killerTask = new TimerTask() {
            @Override
            public void run() {
                if (null != process) {
                    process.destroy();
                }
            }
        };
        schedule(killerTask, timelimit);
    }

    public int getExitValue() {
        return mReturnValue;
    }
}
